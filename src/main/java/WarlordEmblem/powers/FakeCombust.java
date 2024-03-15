package WarlordEmblem.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.CombustPower;

//虚假的自燃buff
public class FakeCombust extends CombustPower {

    public FakeCombust(AbstractCreature owner, int damageAmount)
    {
        super(owner,1,damageAmount);
    }

    //回合结束的时候不要触发伤害
    @Override
    public void atEndOfTurn(boolean isPlayer) {

    }

}
