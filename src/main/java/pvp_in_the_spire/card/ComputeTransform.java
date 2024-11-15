package pvp_in_the_spire.card;

import pvp_in_the_spire.powers.BurnTransformPower;
import pvp_in_the_spire.powers.ComputeTransformPower;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pvp_in_the_spire.util.CardStats;

//计算转移，把被弃置的牌转移给对面
public class ComputeTransform extends BaseCard {
    public static final String ID = makeID(ComputeTransform.class.getSimpleName());
    private static final CardStats info = new CardStats(
            CardColor.GREEN,
            CardType.POWER,
            CardRarity.UNCOMMON,
            CardTarget.SELF,
            1 //Card cost. -1 is X cost, -2 is no cost for unplayable cards
    );

    private static final int STACK_AMOUNT = 1;
    private static final int UPG_COST_AMOUNT = 0;

    public ComputeTransform() {
        super(ID,info);
        setCostUpgrade(UPG_COST_AMOUNT);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new ComputeTransformPower(p), STACK_AMOUNT));
    }
}
