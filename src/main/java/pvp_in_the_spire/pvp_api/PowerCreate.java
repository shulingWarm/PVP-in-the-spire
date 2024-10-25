package pvp_in_the_spire.pvp_api;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

//通常我方显示的debuff都是个空壳子，实际结算是在施加power的一方进行的
//这个class是为了处理各种把power转换成空壳子的逻辑的
public class PowerCreate {
    public AbstractPower make(AbstractCreature owner, int amount, boolean isSourceMonster)
    {
        return null;
    }
}
