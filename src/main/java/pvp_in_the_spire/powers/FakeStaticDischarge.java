package pvp_in_the_spire.powers;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.StaticDischargePower;

//假的静电释放power
public class FakeStaticDischarge extends StaticDischargePower {

    public FakeStaticDischarge(AbstractCreature owner, int lightningAmount) {
        super(owner,lightningAmount);
    }

    //重写被攻击的函数，它被攻击时并不会发生什么
    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {

        return damageAmount;
    }

}
