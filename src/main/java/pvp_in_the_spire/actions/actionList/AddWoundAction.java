package pvp_in_the_spire.actions.actionList;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.AbstractCreature;

import java.util.ArrayList;

//向牌组里面临时添加伤口的操作
public class AddWoundAction extends ActListGeneratorBase{

    public ArrayList<AbstractGameAction> getActList(AbstractCreature actSource, int baseValue)
    {
        //用于存储action信息的列表
        ArrayList<AbstractGameAction> actList = new ArrayList<AbstractGameAction>();
        //往牌里面添加一个临时的伤口
        actList.add(new MakeTempCardInDrawPileAction(new Wound(),baseValue,true,true));
        return actList;
    }

}
