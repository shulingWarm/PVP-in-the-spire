package pvp_in_the_spire.actions;

import pvp_in_the_spire.powers.PelletsPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class DisableBuffOneTurn extends AbstractGameAction {

    public static boolean hasDebuff(AbstractCreature creature)
    {
        //遍历它的每个power
        for(AbstractPower eachPower : creature.powers)
        {
            if(eachPower.type == AbstractPower.PowerType.DEBUFF)
                return true;
        }
        return false;
    }

    public DisableBuffOneTurn()
    {
        this.target = AbstractDungeon.player;
    }

    @Override
    public void update() {
        //判断player是否存在负面，如果没有负面的话就不用操作了
        if(!hasDebuff(target))
        {
            this.isDone = true;
            return;
        }
        //移除玩家身上的负面
        PelletsPower pelletsPower = new PelletsPower(target);
        //把负面效果的记录添加进去
        AbstractDungeon.actionManager.addToTop(
            new ApplyPowerAction(AbstractDungeon.player,
                    AbstractDungeon.player,pelletsPower)
        );
        this.isDone = true;
    }
}
