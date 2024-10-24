package pvp_in_the_spire.actions.actionList;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.WeakPower;

import java.util.ArrayList;

public class WeakenAddAction extends ActListGeneratorBase{

    public ArrayList<AbstractGameAction> getActList(AbstractCreature actSource, int baseValue)
    {
        //用于存储action信息的列表
        ArrayList<AbstractGameAction> actList = new ArrayList<AbstractGameAction>();
        actList.add(new SFXAction("VO_CULTIST_1B"));
        //添加易伤效果
        actList.add(new ApplyPowerAction(AbstractDungeon.player, actSource, new WeakPower(AbstractDungeon.player, baseValue,true)));
        return actList;
    }

}
