package pvp_in_the_spire.patches;

//和姿态相关的patch
public class StancePatch {

    //退出暴怒时给观者强行塞一张灼伤
//    @SpirePatch(clz = WrathStance.class, method = "onExitStance")
//    public static class BurnExitWrath
//    {
//        //退出姿态后，强行添加一个灼伤
//        @SpirePostfixPatch
//        public static void fix(WrathStance __instance)
//        {
//            //在玩家的抽牌堆里面加入一个灼伤
//            AbstractDungeon.actionManager.addToBottom(
//                new MakeTempCardInDiscardAction(new Burn(),1)
//            );
//        }
//    }

}
