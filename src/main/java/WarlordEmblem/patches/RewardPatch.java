package WarlordEmblem.patches;

import WarlordEmblem.GlobalManager;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;

//战斗结束时的奖励内容
public class RewardPatch {

    //在最后一次胜利时移除奖励
    @SpirePatch(clz = CombatRewardScreen.class,
        method = "setupItemReward")
    public static class RemoveRewardForWinner
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> fix(CombatRewardScreen __instance)
        {
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
