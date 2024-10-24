package pvp_in_the_spire.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.SmilingMask;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StoreRelic;

//对商店里面内容的修改
//一些修改游戏逻辑相关的patch
public class ShopChange {

    //禁止会员卡对删牌的效果
    @SpirePatch(clz = ShopScreen.class,method = "applyDiscount")
    public static class BanPurgeCardCostChange
    {
        //调用函数之后的处理，调用之后把删牌恢复到正常的水平
        @SpirePostfixPatch
        public static void fix(ShopScreen __instance)
        {
            //判断玩家是否有微笑面具，如果有的话就什么都不需要做
            if(!AbstractDungeon.player.hasRelic(SmilingMask.ID))
            {
                //更改删牌的钱，确保它不能打折
                ShopScreen.actualPurgeCost = ShopScreen.purgeCost;
            }
        }
    }

    //禁止因为送货员刷新遗物
    @SpirePatch(clz = StoreRelic.class,method = "purchaseRelic")
    public static class BanFlushRelic
    {

        //前导的时候，把送货员的id临时改掉
        @SpirePostfixPatch
        public static void postfix(StoreRelic __instance)
        {
            //强行把它改成购买过了
            __instance.isPurchased = true;
        }
    }


}
