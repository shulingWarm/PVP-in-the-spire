package WarlordEmblem.patches;

import WarlordEmblem.GlobalManager;
import WarlordEmblem.Room.FriendManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

//对玩家渲染和更新的patch,需要确保把玩家显示在最前面
public class PlayerPatch {

//    @SpirePatch(clz = AbstractPlayer.class, method = "update")
//    public static class UpdatePatch
//    {
//        //更新时先更新friend管理器
//        @SpirePrefixPatch
//        public static void fix(AbstractPlayer __instance)
//        {
//            //调用友军的更新
//            FriendManager.instance.update();
//        }
//    }
//
//    //对player的渲染
//    @SpirePatch(clz = AbstractPlayer.class, method = "render")
//    public static class RenderPatch
//    {
//        @SpirePrefixPatch
//        public static void fix(AbstractPlayer __instance,
//           SpriteBatch sb)
//        {
//            FriendManager.instance.render(sb);
//        }
//    }

    @SpirePatch(clz = MonsterGroup.class, method = "render")
    public static class FriendRenderPatch
    {
        @SpirePostfixPatch
        public static void fix(MonsterGroup __instance,
                               SpriteBatch sb)
        {
            GlobalManager.playerManager.battleInfo.friendPlayerGroup.render(sb);
        }
    }

    @SpirePatch(clz = MonsterGroup.class, method = "update")
    public static class FriendUpdatePatch
    {
        @SpirePostfixPatch
        public static void fix(MonsterGroup __instance)
        {
            GlobalManager.playerManager.battleInfo.friendPlayerGroup.update();
        }
    }


}
