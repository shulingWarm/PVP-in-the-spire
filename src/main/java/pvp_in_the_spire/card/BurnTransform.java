package pvp_in_the_spire.card;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import pvp_in_the_spire.powers.BurnTransformPower;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pvp_in_the_spire.util.CardStats;

//战士的燃烧转移，把烧掉的牌转移给对面
public class BurnTransform extends BaseCard {
    public static final String ID = makeID(BurnTransform.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CardColor.RED,
            CardType.SKILL,
            CardRarity.UNCOMMON,
            CardTarget.SELF,
            1 //Card cost. -1 is X cost, -2 is no cost for unplayable cards
    );

    private static final int STACK_AMOUNT = 1;
    private static final boolean RETAIN = false;
    private static final boolean UPG_RETAIN = true;

    public BurnTransform() {
        super(ID,info);
        setSelfRetain(RETAIN, UPG_RETAIN);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new BurnTransformPower(p), STACK_AMOUNT));
    }
}
