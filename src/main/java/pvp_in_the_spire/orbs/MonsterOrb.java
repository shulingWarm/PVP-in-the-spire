package pvp_in_the_spire.orbs;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.AbstractPower;

//敌人对应的球
public abstract class MonsterOrb extends AbstractOrb {
    //所属的玩家
    public AbstractCreature owner = null;

    public void setOwner(AbstractCreature owner) {
        if(this.owner == null)
        {
            this.owner = owner;
            updateDescription();
        }
    }

    public void applyFocus() {
        if(this.owner == null)
            return;
        AbstractPower power = owner.getPower("Focus");
        if (power != null && !this.ID.equals("Plasma")) {
            this.passiveAmount = Math.max(0, this.basePassiveAmount + power.amount);
            this.evokeAmount = Math.max(0, this.baseEvokeAmount + power.amount);
        } else {
            this.passiveAmount = this.basePassiveAmount;
            this.evokeAmount = this.baseEvokeAmount;
        }
    }
}
