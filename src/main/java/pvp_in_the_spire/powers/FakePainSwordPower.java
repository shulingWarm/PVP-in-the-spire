package pvp_in_the_spire.powers;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;

//假的疼痛buff,玩家这一端只是做一个显示
public class FakePainSwordPower extends PainSwordPower {

    public FakePainSwordPower(AbstractCreature owner, int amount)
    {
        super(owner,amount);
    }

    //产生伤害时并不会有什么效果
    public void onInflictDamage(DamageInfo info, int damageAmount, AbstractCreature target) {

    }

}
