package pvp_in_the_spire.card;

import pvp_in_the_spire.actions.ElectronicInterferenceAction;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

//电子干扰，机器人的x费牌，用于当对方失去能量X回合
public class ElectronicInterference extends CustomCard {

    public static final String ID = "electronicInterference";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    //临时用斩破命运的图片来代替，所有的图片最后统一处理
    public static final String IMG = "pvp_in_the_spire/images/cards/skill/electronicInterference.png";
    private static final int COST = -1;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final CardType TYPE = CardType.SKILL;
    private static final CardColor COLOR = CardColor.BLUE;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    public ElectronicInterference()
    {
        super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.baseMagicNumber = 1;
        this.magicNumber = this.baseMagicNumber;
        //设置为消耗
        this.exhaust = true;
    }

    //更新描述
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }

    //让目标失去对应回合数的能量
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new ElectronicInterferenceAction(p,m,
        this.freeToPlayOnce, this.energyOnUse, this.upgraded));
    }

    public AbstractCard makeCopy() {
        return new ElectronicInterference();
    }

}
