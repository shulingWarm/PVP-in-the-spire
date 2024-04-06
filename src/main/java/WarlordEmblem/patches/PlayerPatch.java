package WarlordEmblem.patches;

import WarlordEmblem.Room.FriendManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

//对玩家渲染和更新的patch,需要确保把玩家显示在最前面
public class PlayerPatch {

    @SpirePatch(clz = AbstractPlayer.class, method = "update")
    public static class UpdatePatch
    {
        //更新时先更新friend管理器
        @SpirePrefixPatch
        public static void fix(AbstractPlayer __instance)
        {
            //调用友军的更新
            FriendManager.instance.update();
        }
    }

    //对player的渲染
    @SpirePatch(clz = AbstractPlayer.class, method = "render")
    public static class RenderPatch
    {
        @SpirePrefixPatch
        public static void fix(AbstractPlayer __instance,
           SpriteBatch sb)
        {
            FriendManager.instance.render(sb);
        }
    }

}
