package WarlordEmblem.patches;

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

}
