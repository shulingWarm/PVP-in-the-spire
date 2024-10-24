package pvp_in_the_spire.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.RegenPower;

//虚假的再生，用于显示在敌方的头上
public class FakeRegenPower extends RegenPower {

    public FakeRegenPower(AbstractCreature owner, int heal)
    {
        super(owner,heal);
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        this.flashWithoutSound();
        //把数量减1
        this.amount--;
        if(this.amount<=0)
            this.addToBot(
            new RemoveSpecificPowerAction(this.owner,this.owner,this));
    }
}
