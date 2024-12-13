package pvp_in_the_spire.patches;

import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.rewards.RewardItem;
import pvp_in_the_spire.GlobalManager;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;

import java.util.ArrayList;

//战斗结束时的奖励内容
public class RewardPatch {

    //因失败跳过下一次奖励
    public static boolean loserJumpRewardFlag = false;

    //在最后一次胜利时移除奖励
    @SpirePatch(clz = CombatRewardScreen.class,
        method = "setupItemReward")
    public static class RemoveRewardForWinner
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> fix(CombatRewardScreen __instance)
        {
            //如果是有玩家失败，就跳过奖励，执行针对失败者的奖励
            if(loserJumpRewardFlag)
            {
                loserJumpRewardFlag = false;
                //取消点击左键
                InputHelper.justClickedLeft = false;
                //初始化一个reward列表，但不再使用战斗房间的公共奖励
                __instance.rewards = new ArrayList<>();
                if(GlobalManager.loserGoldAmount > 0)
                {
                    //往里面添加金币奖励
                    __instance.rewards.add(new RewardItem(GlobalManager.loserGoldAmount));
                }
                if(GlobalManager.loserCardFlag)
                {
                    __instance.rewards.add(new RewardItem());
                }
                if(AbstractDungeon.player.hasRelic("White Beast Statue"))
                {
                    //如果玩家有白兽雕像，就再获得三瓶药水
                    __instance.rewards.add(new RewardItem(AbstractDungeon.returnRandomPotion()));
                    __instance.rewards.add(new RewardItem(AbstractDungeon.returnRandomPotion()));
                    __instance.rewards.add(new RewardItem(AbstractDungeon.returnRandomPotion()));
                }
                AbstractDungeon.overlayMenu.proceedButton.show();
                __instance.hasTakenAll = false;
                //指定reward的渲染位置
                __instance.positionRewards();
                return SpireReturn.Return();
            }

            //判断是否已经游戏结束
            if(GlobalManager.prepareWin)
            {
                AbstractDungeon.overlayMenu.proceedButton.show();
                __instance.hasTakenAll = true;
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    //禁止打开奖励页面
//    @SpirePatch(clz = CombatRewardScreen.class, method = "open",
//        paramtypez = {})
//    public static class CloseRewardScreen
//    {
//        @SpirePrefixPatch
//        public static SpireReturn<Void> fix(CombatRewardScreen __instance)
//        {
//            if(GlobalManager.prepareWin)
//                return SpireReturn.Return();
//            return SpireReturn.Continue();
//        }
//
//    }

}
