package pvp_in_the_spire.patches;

import pvp_in_the_spire.reward.BossRelicItem;
import pvp_in_the_spire.reward.FirstRelicItem;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.neow.NeowReward;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

//对涅奥的第2个奖励的特殊操作
//涅奥的第2个奖励里面可能出现1血祝福，因此需要重写一下
public class NeowRewardPatch {

    //手动实体那个涉及到涅奥的祝福的特定case
    @SpirePatch(clz = NeowReward.class,method = "getRewardOptions")
    public static class ChangeCasePatch
    {

        //是否已经换过了boss遗物
        public static boolean bossRelicChanged = false;

        @SpirePrefixPatch
        public static SpireReturn<ArrayList<NeowReward.NeowRewardDef>> fix(
                NeowReward __instance, int category, int ___hp_bonus
        )
        {
            //如果是可能存在涅奥的祝福的类型，就自己实现它
            if(category==1)
            {
                ArrayList<NeowReward.NeowRewardDef> rewardOptions = new ArrayList();
                rewardOptions.add(new NeowReward.NeowRewardDef(NeowReward.NeowRewardType.THREE_SMALL_POTIONS, NeowReward.TEXT[5]));
                rewardOptions.add(new NeowReward.NeowRewardDef(NeowReward.NeowRewardType.RANDOM_COMMON_RELIC, NeowReward.TEXT[6]));
                rewardOptions.add(new NeowReward.NeowRewardDef(NeowReward.NeowRewardType.TEN_PERCENT_HP_BONUS, NeowReward.TEXT[7] + ___hp_bonus + " ]"));
                rewardOptions.add(new NeowReward.NeowRewardDef(NeowReward.NeowRewardType.HUNDRED_GOLD, NeowReward.TEXT[8] + 100 + NeowReward.TEXT[9]));
                return SpireReturn.Return(rewardOptions);
            }
            //如果是换boss遗物的类型，但已经处理过了，也自己实现它，但是按照诅咒的形式来处理
            else if(category==3 && bossRelicChanged)
            {
                try
                {
                    Method getOptionMethod = NeowReward.class.getDeclaredMethod("getRewardOptions",int.class);
                    getOptionMethod.setAccessible(true);
                    return SpireReturn.Return(
                            (ArrayList<NeowReward.NeowRewardDef>)getOptionMethod.invoke(__instance,2)
                    );
                }
                catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
                {
                    e.printStackTrace();
                }
            }
            return SpireReturn.Continue();
        }
    }

    //可反悔的boss遗物选项
    public static void addRegretBossRelic()
    {
        //获取当前的第1个遗物
        AbstractRelic firstRelic = AbstractDungeon.player.relics.get(0);
        //移除这个遗物
        AbstractDungeon.player.loseRelic(firstRelic.relicId);
        //获得一个随机的boss遗物
        AbstractRelic bossRelic = AbstractDungeon.returnRandomRelic(AbstractRelic.RelicTier.BOSS);
        //AbstractRelic bossRelic = new TinyHouse();
        //把两个遗物添加到奖励列表里面
        AbstractRoom room = AbstractDungeon.getCurrRoom();
        room.rewards.clear();
        RewardItem item1 = new FirstRelicItem(firstRelic);
        RewardItem item2 = new BossRelicItem(bossRelic);
        item1.relicLink = item2;
        item2.relicLink = item1;
        room.rewards.add(item1);
        room.rewards.add(item2);
        //如果路过的话，下次也不能换4了
        ChangeCasePatch.bossRelicChanged = true;
        AbstractDungeon.combatRewardScreen.open();

        //删除奖励页面自带的那个卡牌
        int remove = -1;

        for(int j = 0; j < AbstractDungeon.combatRewardScreen.rewards.size(); ++j) {
            if (((RewardItem)AbstractDungeon.combatRewardScreen.rewards.get(j)).type == RewardItem.RewardType.CARD) {
                remove = j;
                break;
            }
        }

        if (remove != -1) {
            AbstractDungeon.combatRewardScreen.rewards.remove(remove);
        }
    }

    //判断是否拿到了换boss遗物的操作，如果有这个操作的话，记录下来
    //最多只能拿一次
    @SpirePatch(clz = NeowReward.class, method = "activate")
    public static class RecordBossRelicChange
    {

        //前导的操作，确保可以正常获得boss遗物
        @SpirePrefixPatch
        public static SpireReturn<Void> preChange(NeowReward __instance)
        {
            if(__instance.type == NeowReward.NeowRewardType.BOSS_RELIC)
            {
                RoomPatch.RemoveBossRelicForLoser.isDisable = true;
                addRegretBossRelic();
                RoomPatch.RemoveBossRelicForLoser.isDisable = false;
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }

//        @SpirePostfixPatch
//        public static void fix(NeowReward __instance)
//        {
//            //如果传入的种类是换boss遗物，就记录下已经换过boss遗物了
//            if(__instance.type== NeowReward.NeowRewardType.BOSS_RELIC)
//            {
//                ChangeCasePatch.bossRelicChanged = true;
//            }
//            RoomPatch.RemoveBossRelicForLoser.isDisable = false;
//        }
    }

}
