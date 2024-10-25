package pvp_in_the_spire.card;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.SelfRepair;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.RegenPower;

//改版的自我修复
public class SelfRepairChange extends AbstractCard {

    public static final String ID = "Self Repair";
    private static final CardStrings cardStrings;

    public SelfRepairChange() {
        super("Self Repair", cardStrings.NAME, "blue/power/self_repair", 1, cardStrings.DESCRIPTION, CardType.POWER, CardColor.BLUE, CardRarity.UNCOMMON, CardTarget.SELF);
        this.baseMagicNumber = 4;
        this.magicNumber = this.baseMagicNumber;
        this.tags.add(CardTags.HEALING);
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new RegenPower(AbstractDungeon.player, this.magicNumber), this.magicNumber));
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(1);
        }

    }

    //强制获取修改过的自我修复
    @SpirePatch(clz = SelfRepair.class,method = "makeCopy")
    public static class ChangeCardPatch
    {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> fix(SelfRepair __instance)
        {
            return SpireReturn.Return(new SelfRepairChange());
        }
    }

    public AbstractCard makeCopy() {
        return new SelfRepairChange();
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings("ChangeSelfRepair");
    }

}
