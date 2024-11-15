package pvp_in_the_spire.card;

import pvp_in_the_spire.powers.FakeAngerPower;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

//获得猛男buff的操作
public class StrongMan extends CustomCard {

    public static final String ID = "strongMan";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    //临时用斩破命运的图片来代替，所有的图片最后统一处理
    public static final String IMG = "pvp_in_the_spire/images/cards/skill/strongMan.png";
    private static final int COST = 3;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final CardType TYPE = CardType.POWER;
    private static final CardColor COLOR = CardColor.COLORLESS;
    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.SELF;

    public StrongMan()
    {
        super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.baseMagicNumber = 1;
        this.magicNumber = this.baseMagicNumber;
    }

    //升级时把1力量换成2力量
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            //提升数值
            this.upgradeMagicNumber(1);
            //更新描述
            this.initializeDescription();
        }
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new ApplyPowerAction(p, p,
                new FakeAngerPower(p,this.magicNumber), this.magicNumber));
    }

    public AbstractCard makeCopy() {
        return new StrongMan();
    }

}
