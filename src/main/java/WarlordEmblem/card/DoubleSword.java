package WarlordEmblem.card;

import WarlordEmblem.actions.TransformCardAction;
import WarlordEmblem.powers.FakeHexPower;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

//战士的双刃剑，造成6点伤害，然后自己获得两张伤口，目标也获得两张伤口
public class DoubleSword extends CustomCard {

    public static final String ID = "doubleSword";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    //临时用斩破命运的图片来代替，所有的图片最后统一处理
    public static final String IMG = "pvp/card/doubleSword.png";
    private static final int COST = 1;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final CardType TYPE = CardType.ATTACK;
    private static final CardColor COLOR = CardColor.RED;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    public DoubleSword()
    {
        super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.baseDamage = 6;
        this.damage = baseDamage;
        this.baseMagicNumber=1;
        this.magicNumber=1;
    }

    //升级时改成施加两层
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            //提升数值 原来是6,后来打9
            this.upgradeDamage(3);
            //改为塞两张伤口
            this.upgradeMagicNumber(1);
            //更新描述
            this.initializeDescription();
        }
    }

    //造成对应的伤害，给自己和对方同时添加两张伤口
    public void use(AbstractPlayer p, AbstractMonster m) {
        //给对方造成伤害
        this.addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
        //给自己塞两张伤口
        this.addToBot(new MakeTempCardInHandAction(new Wound(),this.magicNumber));
        //给对方也塞两张伤口
        TransformCardAction.sendAddCard(new Wound(),this.magicNumber,
                TransformCardAction.HAND);
    }

    public AbstractCard makeCopy() {
        return new DoubleSword();
    }

}
