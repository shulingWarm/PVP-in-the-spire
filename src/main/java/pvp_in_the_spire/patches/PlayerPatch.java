package pvp_in_the_spire.patches;

import pvp_in_the_spire.events.PlayerTurnBegin;
import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.Communication;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

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

    //玩家回合开始时的patch,调用这个东西是为了方便同步失去block的操作
    @SpirePatch(clz = AbstractPlayer.class, method = "applyStartOfTurnOrbs")
    public static class PlayerTurnStartPatch
    {
        @SpirePostfixPatch
        public static void fix(AbstractPlayer __instance)
        {
            //刚进入房间战斗准备的那次不需要发
            if(GlobalManager.getBattleInfo().getIdTurn() > 1)
            {
                //发送开始回合的消息
                Communication.sendEvent(new PlayerTurnBegin());
            }
        }
    }


}
