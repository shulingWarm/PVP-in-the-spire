package WarlordEmblem.patches;

import UI.Chat.ChatFoldPage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

//对于游戏地图相关的patch 这其实是为了聊天框的渲染和更新
public class DungeonPatch {

    @SpirePatch(clz = AbstractDungeon.class, method = "render")
    public static class DungeonRenderPatch
    {
        @SpirePostfixPatch
        public static void fix(AbstractDungeon __instance, SpriteBatch sb)
        {
            ChatFoldPage.getInstance().render(sb);
        }
    }

    //更新聊天页面的操作
    @SpirePatch(clz = AbstractDungeon.class, method = "update")
    public static class DungeonUpdatePatch
    {
        @SpirePostfixPatch
        public static void fix(AbstractDungeon __instance)
        {
            //更新聊天框
            ChatFoldPage.getInstance().update();
        }

    }

}
