package pvp_in_the_spire.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

//假的机器学习buff
public class FakeDrawPower extends AbstractPower {

    public static final String POWER_ID = "Draw";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    public FakeDrawPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = "Draw";
        this.owner = owner;
        this.amount = amount;
        this.updateDescription();
        this.loadRegion("draw");
        if (amount < 0) {
            this.type = PowerType.DEBUFF;
            this.loadRegion("draw2");
        } else {
            this.type = PowerType.BUFF;
            this.loadRegion("draw");
        }
    }

    public void updateDescription() {
        if (this.amount > 0) {
            if (this.amount == 1) {
                this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
            } else {
                this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[3];
            }

            this.type = PowerType.BUFF;
        } else {
            if (this.amount == -1) {
                this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[2];
            } else {
                this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[4];
            }

            this.type = PowerType.DEBUFF;
        }

    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings("Draw");
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }

}
