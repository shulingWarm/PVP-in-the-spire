package pvp_in_the_spire.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.InvinciblePower;

//在结束阶段也会触发重置的坚不可摧
public class InvincibleAtStartPower extends InvinciblePower {

    public InvincibleAtStartPower(AbstractCreature owner, int amount)
    {
        super(owner,amount);
    }

    //在回合结束时恢复坚不可摧
    @Override
    public void atEndOfTurn(boolean isPlayer) {
        super.atStartOfTurn();
    }
}
