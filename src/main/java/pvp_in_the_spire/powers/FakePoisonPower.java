package pvp_in_the_spire.powers;

import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

//假的毒buff,只负责触发伤害
public class FakePoisonPower extends AbstractPower {
    public static final String POWER_ID = "Poison";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    private AbstractCreature source;

    public FakePoisonPower(AbstractCreature owner, AbstractCreature source, int poisonAmt) {
        this.name = NAME;
        this.ID = "Poison";
        this.owner = owner;
        this.source = source;
        this.amount = poisonAmt;
        if (this.amount >= 9999) {
            this.amount = 9999;
        }

        this.updateDescription();
        this.loadRegion("poison");
        this.type = PowerType.DEBUFF;
        this.isTurnBased = true;
    }

    public void playApplyPowerSfx() {
        CardCrawlGame.sound.play("POWER_POISON", 0.05F);
    }

    public void updateDescription() {
        if (this.owner != null && !this.owner.isPlayer) {
            this.description = DESCRIPTIONS[2] + this.amount + DESCRIPTIONS[1];
        } else {
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
        }

    }

    public void atStartOfTurn() {
        //更改毒的数值
        --this.amount;
    }

    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        if (this.amount > 98 && AbstractDungeon.player.chosenClass == PlayerClass.THE_SILENT) {
            UnlockTracker.unlockAchievement("CATALYST");
        }

    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings("Poison");
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }
}
