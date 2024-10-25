package pvp_in_the_spire.actions.actionList;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.red.Corruption;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.CuriosityPower;

import java.util.ArrayList;

//获得好奇buff的action 觉醒者的那个buff
public class CuriosityActList extends ActListGeneratorBase{

    public boolean haveGiveCard = false;

    public ArrayList<AbstractGameAction> getActList(AbstractCreature actSource, int baseValue)
    {
        //用于存储action信息的列表
        ArrayList<AbstractGameAction> actList = new ArrayList<AbstractGameAction>();
        //赠送一个壁垒
        if(!haveGiveCard)
        {
            actList.add(new MakeTempCardInDrawPileAction(new Corruption(),1,true,true));
            haveGiveCard = true;
        }
        actList.add(new SFXAction("VO_CULTIST_1A"));
        //添加格挡的效果数据
        actList.add(new ApplyPowerAction(actSource, actSource, new CuriosityPower(actSource, baseValue)));
        return actList;
    }

}
