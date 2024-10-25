package pvp_in_the_spire.actions.actionList;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.purple.MasterReality;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.RitualPower;

import java.util.ArrayList;

public class RitualActList extends ActListGeneratorBase {

    public ArrayList<AbstractGameAction> getActList(AbstractCreature actSource, int baseValue)
    {
        //用于存储action信息的列表
        ArrayList<AbstractGameAction> actList = new ArrayList<AbstractGameAction>();
        //播放声音的动画
        actList.add(new SFXAction("VO_CULTIST_1A"));
        //给操控现实
        if(baseValue > 3)
            actList.add(new MakeTempCardInDrawPileAction(new MasterReality(),1,true,true));
        //叠加仪式层
        actList.add(new ApplyPowerAction(actSource,actSource,new RitualPower(actSource,baseValue,false)));
        //返回已经添加好的action列表
        return actList;
    }

}
