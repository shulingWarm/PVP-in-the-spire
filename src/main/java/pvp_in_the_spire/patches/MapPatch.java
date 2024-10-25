package pvp_in_the_spire.patches;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.screens.PVPVictory;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

//与地图转换相关的patch
public class MapPatch {

    //检查是否执行胜利条件的patch
    @SpirePatch(clz = AbstractDungeon.class,method = "nextRoomTransitionStart")
    public static class CheckWin
    {
        @SpirePrefixPatch
        public static void fix()
        {
            //检查是否可以执行胜利
            if(GlobalManager.prepareWin)
            {
                PVPVictory.enterVictory();
            }
        }
    }

}
