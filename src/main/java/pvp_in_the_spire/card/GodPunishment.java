package pvp_in_the_spire.card;

import pvp_in_the_spire.powers.GodPunishmentPower2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.green.WraithForm;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

//天罚形态
public class GodPunishment extends AbstractCard {

    public static final String ID = "Wraith Form v2";
    private static final CardStrings cardStrings;

    public GodPunishment() {
        super("Wraith Form v2", cardStrings.NAME, "green/power/wraith_form", 3, cardStrings.DESCRIPTION, CardType.POWER, CardColor.GREEN, CardRarity.RARE, CardTarget.SELF);
        this.baseMagicNumber = 3;
        this.magicNumber = this.baseMagicNumber;
    }

    //获得天罚形态
    public void use(AbstractPlayer p, AbstractMonster m) {
        //获得一层无实体
        this.addToBot(new ApplyPowerAction(p, p, new GodPunishmentPower2(p, this.magicNumber), this.magicNumber));
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            //提升数值
            this.upgradeMagicNumber(1);
            //更新描述
            this.initializeDescription();
        }
    }

    //截取幽魂的获取渠道，改成获得这张牌
    @SpirePatch(clz = WraithForm.class,method = "makeCopy")
    public static class CardChangePatch
    {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> fix(WraithForm __instance)
        {
            return SpireReturn.Return(new GodPunishment());
        }
    }

    public AbstractCard makeCopy() {
        return new GodPunishment();
    }

    //虽然id还是幽魂形态，但解包的时候使用的另一个id
    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings("godPunishment");
    }


}
