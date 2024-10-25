package pvp_in_the_spire.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;

//假的失去能量的buff
public class FakeDropEnergy extends DropEnergy {

    public FakeDropEnergy(AbstractCreature owner, int amount)
    {
        super(owner,amount);
    }

    //在回合开始时什么都不需要做
    public void atStartOfTurn() {
    }
}
