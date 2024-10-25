package pvp_in_the_spire.relics;

import pvp_in_the_spire.ui.TextureManager;
import pvp_in_the_spire.actions.DisableBuffOneTurn;
import basemod.abstracts.CustomRelic;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

//改版后的橙色药丸
public class OrangePelletsChange extends CustomRelic {

    public static final String ID = "OrangePelletsChange";
    private static boolean SKILL = false;
    private static boolean POWER = false;
    private static boolean ATTACK = false;

    public OrangePelletsChange() {
        super("OrangePelletsChange", TextureManager.RED_PELLETS,
            RelicTier.SHOP, LandingSound.CLINK);
    }

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    public void atTurnStart() {
        SKILL = false;
        POWER = false;
        ATTACK = false;
    }

    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card.type == AbstractCard.CardType.ATTACK) {
            ATTACK = true;
        } else if (card.type == AbstractCard.CardType.SKILL) {
            SKILL = true;
        } else if (card.type == AbstractCard.CardType.POWER) {
            POWER = true;
        }

        if (ATTACK && SKILL && POWER) {
            this.flash();
            this.addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            this.addToBot(new DisableBuffOneTurn());
            SKILL = false;
            POWER = false;
            ATTACK = false;
        }

    }

    //强行覆盖橙色药丸的复制操作
//    @SpirePatch(clz= OrangePellets.class, method = "makeCopy")
//    public static class OrangePelletsCopyPatch
//    {
//        @SpirePrefixPatch
//        public static SpireReturn<AbstractRelic> fix()
//        {
//            return SpireReturn.Return(new OrangePelletsChange());
//        }
//    }

    public AbstractRelic makeCopy() {
        return new OrangePelletsChange();
    }
}
