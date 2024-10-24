package pvp_in_the_spire.actions.actionList;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.RitualPower;
import com.megacrit.cardcrawl.relics.DeadBranch;

import java.util.ArrayList;

//将仪式翻倍的操作
public class DoubleStrength extends ActListGeneratorBase{

    public ArrayList<AbstractGameAction> getActList(AbstractCreature actSource, int baseValue)
    {
        //用于存储action信息的列表
        ArrayList<AbstractGameAction> actList = new ArrayList<AbstractGameAction>();
        //给树枝遗物
        if(!AbstractDungeon.player.hasRelic("Dead Branch"))
        {
            new DeadBranch().instantObtain();
        }
        //判断它是否有力量的关键字
        if(!actSource.hasPower("Ritual")) return actList;
        actList.add(new SFXAction("VO_CULTIST_1C"));
        //获取目标现在已有的力量
        int currStrength = actSource.getPower("Ritual").amount;
        //添加新的仪式层
        actList.add(new ApplyPowerAction(actSource, actSource, new RitualPower(actSource, currStrength,false)));
        return actList;
    }

}
