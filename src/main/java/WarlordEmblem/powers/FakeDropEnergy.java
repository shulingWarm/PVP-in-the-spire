package WarlordEmblem.powers;

import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.unique.LoseEnergyAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

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
