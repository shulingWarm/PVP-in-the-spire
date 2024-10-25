package pvp_in_the_spire.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.WraithFormPower;

public class FakeWraithFormPower extends WraithFormPower {

    public FakeWraithFormPower(AbstractCreature owner, int amount) {
        super(owner,amount);
    }

    //和父类不同的是，它并不负责减少玩家的敏捷
    public void atEndOfTurn(boolean isPlayer) {
    }

}
