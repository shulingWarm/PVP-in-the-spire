package pvp_in_the_spire.patches.RelicPool;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.RelicStrings;
import pvp_in_the_spire.relics.RedStoneChange;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.ConfusionPower;
import com.megacrit.cardcrawl.relics.*;

//对各种遗物功能的修改
public class RelicPatch {

    //用于修改遗物描述的字符串
    public static final RelicStrings modifyStrings = CardCrawlGame.languagePack.getRelicStrings("RelicModify");

    //修改白兽雕像的描述
    @SpirePatch(clz = WhiteBeast.class, method = "getUpdatedDescription")
    public static class WhiteBeastChange
    {
        @SpirePrefixPatch
        public static SpireReturn<String> fix()
        {
            return SpireReturn.Return(modifyStrings.DESCRIPTIONS[0]);
        }
    }

    //红石复制的时候改为复制自己
    @SpirePatch(clz=PhilosopherStone.class, method = "makeCopy")
    public static class RedStoneCopyChange
    {
        //需要复制的时候改成复制改过的红石
        @SpirePrefixPatch
        public static SpireReturn<AbstractRelic> fix()
        {
            return SpireReturn.Return(new RedStoneChange());
        }
    }

    //蛇眼的触发逻辑
    @SpirePatch(clz = SneckoEye.class,method = "atPreBattle")
    public static class SnackEyeChange
    {
        //施加蛇眼buff的时候不再施加，而仅仅是添加一个buff的信息就可以
        //这个添加信息是用来通知对面的
        @SpirePrefixPatch
        public static SpireReturn<Void> fix()
        {
            //初始化一个添加蛇眼的信息，这样玩家并不会再添加一个蛇眼，但对方会收到这个信息
            new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new ConfusionPower(AbstractDungeon.player));
            return SpireReturn.Return();
        }
    }

    //把绿帽改成每层都可以获得
    @SpirePatch(clz = Ectoplasm.class, method = "canSpawn")
    public static class GreenHatPatch
    {
        @SpirePrefixPatch
        public static SpireReturn<Boolean> fix()
        {
            return SpireReturn.Return(true);
        }
    }

    //禁止锚的遗物功能生效
//    @SpirePatch(clz = Anchor.class,method = "atBattleStart")
//    public static class BanAnchorStart
//    {
//        public static boolean justStart = false;
//
//        //战斗开始时的状态
//        @SpirePrefixPatch
//        public static SpireReturn<Void> fix(Anchor __instance)
//        {
//            //记录刚刚做过初始化，但其它的信息都不需要做
//            justStart = true;
//            return SpireReturn.Return();
//        }
//    }
//
//    //让锚在回合开始时生效，但只处理一次
//    @SpirePatch(clz = Anchor.class,method = "onPlayerEndTurn")
//    public static class AnchorTurnStart
//    {
//        @SpirePostfixPatch
//        public static void fix(Anchor __instance)
//        {
//            //判断是否需要生效一次
//            if(BanAnchorStart.justStart)
//            {
//                BanAnchorStart.justStart = false;
//                __instance.flash();
//                AbstractDungeon.actionManager.addToBottom(
//                    new RelicAboveCreatureAction(AbstractDungeon.player, __instance)
//                );
//                AbstractDungeon.actionManager.addToBottom(
//                        new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, 10)
//                );
//                __instance.grayscale = true;
//            }
//        }
//    }
//
//
//    //更改红石的触发时机，不在战斗开始的时候触发，而是改成摸完牌的时候触发，这样更稳定一些
//    @SpirePatch(clz = PhilosopherStone.class,method = "atBattleStart")
//    public static class RedStoneStopTrigger
//    {
//        //是否刚刚开始，下一次需要生效
//        public static boolean justStart = false;
//
//        public static SpireReturn<Void> fix(PhilosopherStone __instance)
//        {
//            justStart = true;
//            //这个函数不再生效，改成在另一个地方生效
//            return SpireReturn.Return();
//        }
//    }
//
//    //让红石在第一回合抽完牌的地方生效
//    @SpirePatch(clz = PhilosopherStone.class,method = "onPlayerEndTurn")
//    public static class RedStoneTurnStart
//    {
//        @SpirePostfixPatch
//        public static void fix(PhilosopherStone __instance)
//        {
//            //判断是否需要生效一次
//            if(RedStoneStopTrigger.justStart)
//            {
//                RedStoneStopTrigger.justStart = false;
//                Iterator var1 = AbstractDungeon.getMonsters().monsters.iterator();
//
//                while(var1.hasNext()) {
//                    AbstractMonster m = (AbstractMonster)var1.next();
//                    //给所有的敌人加一点力量
//                    AbstractDungeon.actionManager.addToTop(
//                        new RelicAboveCreatureAction(m, __instance)
//                    );
//                    AbstractDungeon.actionManager.addToBottom(
//                        new ApplyPowerAction(m,AbstractDungeon.player,
//                            new StrengthPower(m,1),1)
//                    );
//                }
//            }
//        }
//    }


}
