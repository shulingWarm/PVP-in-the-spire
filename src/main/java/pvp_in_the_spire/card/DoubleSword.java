package pvp_in_the_spire.card;

import pvp_in_the_spire.actions.TransformCardAction;
import pvp_in_the_spire.character.PlayerMonster;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pvp_in_the_spire.util.CardStats;

//战士的双刃剑，造成6点伤害，然后自己获得两张伤口，目标也获得两张伤口
public class DoubleSword extends BaseCard {
    public static final String ID = makeID(DoubleSword.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CardColor.RED,
            CardType.ATTACK,
            CardRarity.UNCOMMON,
            CardTarget.ENEMY,
            1 //Card cost. -1 is X cost, -2 is no cost for unplayable cards
    );

    private static final int DAMAGE = 6;
    private static final int UPG_DAMAGE = 3;
    private static final int WOUND_AMOUNT = 1;
    private static final int UPG_WOUND_AMOUNT = 1;

    public DoubleSword() {
        super(ID,info);

        setDamage(DAMAGE, UPG_DAMAGE);
        setMagic(WOUND_AMOUNT, UPG_WOUND_AMOUNT);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, this.damage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
        addToBot(new MakeTempCardInHandAction(new Wound(), this.magicNumber));
        if(m instanceof PlayerMonster) {
            TransformCardAction.sendAddCard(new Wound(),this.magicNumber, TransformCardAction.HAND,(PlayerMonster) m);
        }
    }

}
