package pvp_in_the_spire.actions.actionList;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;

import java.util.ArrayList;

public class BlockActList extends ActListGeneratorBase {

    public ArrayList<AbstractGameAction> getActList(AbstractCreature actSource, int baseValue)
    {
        //用于存储action信息的列表
        ArrayList<AbstractGameAction> actList = new ArrayList<AbstractGameAction>();
        //添加格挡的效果数据
        actList.add(new GainBlockAction(actSource,actSource,baseValue));
        return actList;
    }

}
