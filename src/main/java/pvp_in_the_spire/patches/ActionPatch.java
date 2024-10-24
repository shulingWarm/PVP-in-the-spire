package pvp_in_the_spire.patches;

import pvp_in_the_spire.actions.MultiPauseAction;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.actions.GameActionManager;

//与玩家行动相关的patch
public class ActionPatch {

    //禁止回合的情况下，完全禁止执行玩家开始时的操作
    @SpirePatch(clz = GameActionManager.class, method = "getNextAction")
    public static class StopPlayerStartDuringPause
    {
        public static boolean srcEndTurnFlag = false;

        @SpirePrefixPatch
        public static void fix(GameActionManager __instance)
        {
            srcEndTurnFlag = __instance.turnHasEnded;
            //判断是否需要强制指定回合还没有结束
            if(MultiPauseAction.pauseStage)
                __instance.turnHasEnded = false;
            if(__instance.turnHasEnded)
            {
                System.out.println("Turn will begin!!!");
            }
        }

        @SpirePostfixPatch
        public static void postfix(GameActionManager __instance)
        {
            if(MultiPauseAction.pauseStage)
                __instance.turnHasEnded = srcEndTurnFlag;
        }

    }

}
