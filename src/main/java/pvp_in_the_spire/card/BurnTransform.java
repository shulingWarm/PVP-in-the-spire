package pvp_in_the_spire.card;

import pvp_in_the_spire.powers.BurnTransformPower;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

//战士的燃烧转移，把烧掉的牌转移给对面
public class BurnTransform extends CustomCard {

    public static final String ID = "burnTransform";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    //临时用斩破命运的图片来代替，所有的图片最后统一处理
    public static final String IMG = "images/cards/skill/burnTransform.png";
    private static final int COST = 1;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final CardType TYPE = CardType.SKILL;
    private static final CardColor COLOR = CardColor.RED;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.SELF;

    public BurnTransform()
    {
        super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.baseMagicNumber = 1;
        this.magicNumber = this.baseMagicNumber;
    }

    //升级后改为记录额外添加两张晕眩
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            //改为0费
            this.upgradeBaseCost(0);
            this.initializeDescription();
        }
    }

    //给自己添加燃烧转移的power
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new ApplyPowerAction(p, p, new BurnTransformPower(p), 1));
    }

    public AbstractCard makeCopy() {
        return new BurnTransform();
    }

}
