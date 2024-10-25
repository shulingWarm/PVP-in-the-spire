package pvp_in_the_spire.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.Melter;
import com.megacrit.cardcrawl.cards.blue.StaticDischarge;
import com.megacrit.cardcrawl.cards.colorless.DarkShackles;
import com.megacrit.cardcrawl.cards.green.PiercingWail;
import com.megacrit.cardcrawl.cards.red.Disarm;
import com.megacrit.cardcrawl.cards.red.Entrench;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

//为了方便通信传输，改变某些牌的实现方式
public class CardUseChange {
    //对巩固实现方式的修改，改成获得对应数量的格挡
    @SpirePatch(clz = Entrench.class ,method = "use")
    public static class EntrethUseChange
    {
        //改成获得相同数量的格挡
        @SpirePrefixPatch
        public static SpireReturn<Void> fix(Entrench __instance,
            AbstractPlayer p, AbstractMonster m)
        {
            //玩家目前的格挡数量
            int currBlock = p.currentBlock;
            //获得相应的格挡数量
            AbstractDungeon.actionManager.addToBottom(
                    new GainBlockAction(p,currBlock)
            );
            return SpireReturn.Return();
        }

    }

    //卡牌稀有度的集中修改
    @SpirePatch(clz = AbstractCard.class,method = "initializeTitle")
    public static class CardRarityChange
    {

        //对熔化的特殊修改
        public static void melterChange(AbstractCard melter)
        {
            //改成金卡
            melter.rarity = AbstractCard.CardRarity.RARE;
            //添加消耗字段
            melter.exhaust = true;
            //在描述的地方添加消耗
            melter.rawDescription = GameDictionary.EXHAUST.NAMES[0] + " NL " +
                melter.rawDescription;
        }

        //构造函数之后，如果是某几个特殊的牌，直接改了它的稀有度
        @SpirePrefixPatch
        public static void fix(AbstractCard __instance)
        {

            //判断是不是那几个特殊的牌
            switch (__instance.cardID)
            {
                case Melter.ID:
                    melterChange(__instance);
                    break;
                case DarkShackles.ID:
                    __instance.cost = 1;
                    __instance.costForTurn = 1;
                case Disarm.ID:
                case StaticDischarge.ID://静电释放
                    __instance.rarity = AbstractCard.CardRarity.RARE;
                    break;
                case PiercingWail.ID: //尖啸
                    __instance.rarity = AbstractCard.CardRarity.UNCOMMON;
                    break;
            }
        }

    }
}
