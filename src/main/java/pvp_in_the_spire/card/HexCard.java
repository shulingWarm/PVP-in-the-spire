package pvp_in_the_spire.card;

import pvp_in_the_spire.powers.FakeHexPower;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

//令对方获得邪咒
public class HexCard extends CustomCard {

    public static final String ID = "hexCard";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    //临时用斩破命运的图片来代替，所有的图片最后统一处理
    public static final String IMG = "pvp_in_the_spire/images/cards/skill/hexCard.png";
    private static final int COST = 1;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final CardType TYPE = CardType.SKILL;
    private static final CardColor COLOR = CardColor.COLORLESS;
    private static final CardRarity RARITY = CardRarity.RARE;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    public HexCard()
    {
        super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.baseMagicNumber = 1;
        this.magicNumber = this.baseMagicNumber;
        //设置为虚无和消耗
        this.exhaust = true;
    }

    //升级时改成施加两层
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            //提升数值
            this.upgradeMagicNumber(1);
            //更新描述
            this.initializeDescription();
        }
    }

    //给目标施加一层邪咒
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new ApplyPowerAction(m, p, new FakeHexPower(m,magicNumber), 1));
    }

    public AbstractCard makeCopy() {
        return new HexCard();
    }

}
