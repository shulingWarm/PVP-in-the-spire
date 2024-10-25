package pvp_in_the_spire.powers;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

//真正的邪咒power
//但不一样的是，它的生效回合是有限的
public class RealHexPower extends AbstractPower {

    public static final String POWER_ID = "Hex";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    public RealHexPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = "Hex";
        this.owner = owner;
        this.amount = amount;
        this.description = DESCRIPTIONS[0] + 1 + DESCRIPTIONS[1];
        this.loadRegion("hex");
        this.type = PowerType.DEBUFF;
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + 1 + DESCRIPTIONS[1];
    }

    //回合结束时将数量减1
    public void atEndOfTurn(boolean isPlayer) {
        //判断是不是需要删除
        if(this.amount<=1)
        {
            AbstractDungeon.actionManager.addToBottom(
                new RemoveSpecificPowerAction(this.owner,this.owner,POWER_ID)
            );
        }
        else {
            //将power数量减1
            AbstractDungeon.actionManager.addToBottom(
                    new ReducePowerAction(this.owner,this.owner,POWER_ID,1)
            );
        }
    }

    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card.type != AbstractCard.CardType.ATTACK && owner == AbstractDungeon.player) {
            this.flash();
            this.addToBot(new MakeTempCardInDrawPileAction(new Dazed(), 1, true, true));
        }
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings("Hex");
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }

}
