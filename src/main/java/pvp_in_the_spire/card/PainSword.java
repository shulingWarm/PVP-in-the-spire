package pvp_in_the_spire.card;

import pvp_in_the_spire.powers.FakePainSwordPower;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

//疼痛之刃，获得一回合的小手buff
public class PainSword extends CustomCard {

    public static final String ID = "painSword";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String IMG = "pvp_in_the_spire/images/cards/skill/painSword.png";
    private static final int COST = 1;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final CardType TYPE = CardType.SKILL;
    private static final CardColor COLOR = CardColor.GREEN;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.SELF;

    public PainSword()
    {
        super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.baseMagicNumber = 1;
        this.magicNumber = this.baseMagicNumber;
    }

    //升级后改为保留牌
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            //更改描述
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            //把这张牌改成保留
            this.selfRetain = true;
            this.initializeDescription();
        }
    }

    //给自己添加燃烧转移的power
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new ApplyPowerAction(p, p, new FakePainSwordPower(p,this.magicNumber), this.magicNumber));
    }

    public AbstractCard makeCopy() {
        return new PainSword();
    }

}
