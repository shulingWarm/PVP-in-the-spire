package pvp_in_the_spire.patches;

import pvp_in_the_spire.ui.Lobby.LobbyScreen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.screens.mainMenu.MenuPanelScreen;

//为了处理主渲染界面的patch
//主要是用于处理游戏大厅相关的逻辑
public class PanelScreenPatch {

    //用于标识现在是否进入了游戏大厅的阶段
    public static boolean lobbyFlag = false;

    //截流游戏主界面的渲染逻辑
    @SpirePatch(clz = MenuPanelScreen.class, method = "render")
    public static class PanelRenderPatch
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> fix(MenuPanelScreen __instance, SpriteBatch sb)
        {
            if(lobbyFlag)
            {
                //改为调用lobby大厅的渲染
                LobbyScreen.instance.render(sb);
                //直接禁止渲染
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    //截流主界面渲染时的update信息
    @SpirePatch(clz = MenuPanelScreen.class, method = "update")
    public static class PanelUpdatePatch
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> fix(MenuPanelScreen __instance)
        {
            if(lobbyFlag)
            {
                LobbyScreen.instance.update();
                //直接禁止渲染
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

}
