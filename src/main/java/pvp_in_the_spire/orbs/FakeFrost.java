package pvp_in_the_spire.orbs;

import pvp_in_the_spire.character.ControlMoster;
import com.megacrit.cardcrawl.orbs.Frost;
import com.megacrit.cardcrawl.powers.AbstractPower;

//假的冰球
public class FakeFrost extends Frost {

    public FakeFrost()
    {

    }

    @Override
    public void applyFocus() {
        AbstractPower power = ControlMoster.instance.getPower("Focus");
        if (power != null && !this.ID.equals("Plasma")) {
            this.passiveAmount = Math.max(0, this.basePassiveAmount + power.amount);
            this.evokeAmount = Math.max(0, this.baseEvokeAmount + power.amount);
        } else {
            this.passiveAmount = this.basePassiveAmount;
            this.evokeAmount = this.baseEvokeAmount;
        }
    }
}
