package pvp_in_the_spire.actions.actionList;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;

import java.util.ArrayList;

//基本的行动列表的基类
public class ActListGeneratorBase {

    //定义基本的虚函数
    public ArrayList<AbstractGameAction> getActList(AbstractCreature actSource, int baseValue)
    {
        return new ArrayList<AbstractGameAction>();
    }

}
