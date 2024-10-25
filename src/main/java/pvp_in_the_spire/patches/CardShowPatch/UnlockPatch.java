package pvp_in_the_spire.patches.CardShowPatch;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

//一个单纯用来解决所有的卡牌的patch
//另外会有一个别的文件用于处理所有的显示对方玩家出牌相关的patch
public class UnlockPatch {

    //当判断一个牌能不能被显示时，直接永远显示true
    @SpirePatch(clz = UnlockTracker.class,method = "isCardSeen")
    public static class UnlockAllCard
    {
        @SpirePrefixPatch
        public static SpireReturn<Boolean> fix(String key)
        {
            //直接默认返回true
            return SpireReturn.Return(true);
        }
    }

}
