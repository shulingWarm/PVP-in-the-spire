package pvp_in_the_spire.powers;

import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.unique.LoseEnergyAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

//丢失能量
public class DropEnergy extends AbstractPower {

    public static final String POWER_ID = "dropEnergy";
    private static final PowerStrings powerStrings;

    public DropEnergy(AbstractCreature owner, int amount) {
        this.name = powerStrings.NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.updateDescription();
        this.loadRegion("bias");
        this.type = PowerType.DEBUFF;
    }

    public void updateDescription() {
        this.description = powerStrings.DESCRIPTIONS[0] +
            this.amount + powerStrings.DESCRIPTIONS[1];
    }

    public void atStartOfTurn() {
        //失去能量
        AbstractDungeon.actionManager.addToBottom(
            new LoseEnergyAction(1)
        );
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

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings("dropEnergy");
    }

}
