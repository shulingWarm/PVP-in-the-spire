package pvp_in_the_spire.powers;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FakeFlameBarrierPower extends AbstractPower {
    private static final Logger logger = LogManager.getLogger(com.megacrit.cardcrawl.powers.FlameBarrierPower.class.getName());
    public static final String POWER_ID = "Flame Barrier";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    public FakeFlameBarrierPower(AbstractCreature owner, int thornsDamage) {
        this.name = NAME;
        this.ID = "Flame Barrier";
        this.owner = owner;
        this.amount = thornsDamage;
        this.updateDescription();
        this.loadRegion("flameBarrier");
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

    public void atStartOfTurn() {
        this.addToBot(new RemoveSpecificPowerAction(owner, AbstractDungeon.player, "Flame Barrier"));
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings("Flame Barrier");
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}
