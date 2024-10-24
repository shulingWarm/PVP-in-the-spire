package pvp_in_the_spire.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.BiasPower;

public class FakeBias extends BiasPower {

    public FakeBias(AbstractCreature owner, int setAmount) {
        super(owner,setAmount);
    }

    //重新设置每回合的操作，每回合不需要任何操作
    @Override
    public void atStartOfTurn() {
    }

}
