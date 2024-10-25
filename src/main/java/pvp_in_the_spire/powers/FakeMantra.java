package pvp_in_the_spire.powers;

import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

//假的神格power
//屏蔽达到10时转换姿态的操作
public class FakeMantra extends AbstractPower {

    public static final String POWER_ID = "Mantra_fake";
    private static final PowerStrings powerStrings;
    private final int PRAYER_REQUIRED = 10;

    public FakeMantra(AbstractCreature owner, int amount) {
        this.name = powerStrings.NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.updateDescription();
        this.loadRegion("mantra");
        this.type = PowerType.BUFF;
        GameActionManager var10000 = AbstractDungeon.actionManager;
        var10000.mantraGained += amount;
    }

    public void playApplyPowerSfx() {
        CardCrawlGame.sound.play("POWER_MANTRA", 0.05F);
    }

    public void updateDescription() {
        this.description = powerStrings.DESCRIPTIONS[0] + 10 + powerStrings.DESCRIPTIONS[1];
    }

    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        if (this.amount >= 10) {
            this.amount -= 10;
            if (this.amount <= 0) {
                this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
            }
        }

    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings("Mantra");
    }

}
