package pvp_in_the_spire.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//假的荆棘，只显示buff但不产生伤害，伤害会由对面传导
public class FakeThornsPower extends AbstractPower {
    private static final Logger logger = LogManager.getLogger(com.megacrit.cardcrawl.powers.ThornsPower.class.getName());
    public static final String POWER_ID = "Thorns";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    public FakeThornsPower(AbstractCreature owner, int thornsDamage) {
        this.name = NAME;
        this.ID = "Thorns";
        this.owner = owner;
        this.amount = thornsDamage;
        this.updateDescription();
        this.loadRegion("thorns");
    }

    public void stackPower(int stackAmount) {
        if (this.amount == -1) {
            logger.info(this.name + " does not stack");
        } else {
            this.fontScale = 8.0F;
            this.amount += stackAmount;
            this.updateDescription();
        }
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings("Thorns");
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}
