package pvp_in_the_spire.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.helpers.input.InputAction;

public class InputActionPatch {

    //禁用shortcut的标志
    public static boolean allowShortcut = true;

    //这是用来禁用快捷键的补丁
    @SpirePatch(clz = InputAction.class, method = "isJustPressed")
    public static class BanShortcut
    {
        @SpirePrefixPatch
        public static SpireReturn<Boolean> fix()
        {
            if(allowShortcut)
                return SpireReturn.Continue();
            return SpireReturn.Return(false);
        }
    }

//    //按下tab键时，打开或关闭聊天窗口
//    @SpirePatch(clz = TopPanel.class, method = "updateButtons")
//    public static class OpenChatBox
//    {
//        @SpirePrefixPatch
//        public static void fix()
//        {
//            //判断tab键是否被按下过
//            if(Gdx.input.isKeyJustPressed(61))
//            {
//                //重置聊天窗口的状态
//                ChatFoldPage.getInstance().invertStage();
//            }
//        }
//    }

}
