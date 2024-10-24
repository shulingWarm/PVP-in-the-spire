package pvp_in_the_spire.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

//为了禁止转换地图的时候回血，任何时候都是禁止回血的
//回血只能是死了再回
public class StopTransHeal {

    @SpirePatch(clz = AbstractDungeon.class,method = "dungeonTransitionSetup")
    public static class StopEndDungeonHeal
    {

        //临时记录的血量
        public static int healthSave = 0;

        //处理之前玩家的血量，在处理之后强行把它恢复回来
        @SpirePrefixPatch
        public static void prefix()
        {
            //记录临时的血量
            healthSave = AbstractDungeon.player.currentHealth;
        }

        @SpirePostfixPatch
        public static void postfix()
        {
            //记录临时的血量
            AbstractDungeon.player.currentHealth = healthSave;
        }
    }

}
