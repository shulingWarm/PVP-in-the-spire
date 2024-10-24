package pvp_in_the_spire.actions.actionList;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.red.Berserk;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.RegenPower;

import java.util.ArrayList;

//获得再生的action
public class RegeneratePowerAction extends ActListGeneratorBase{

    public ArrayList<AbstractGameAction> getActList(AbstractCreature actSource, int baseValue)
    {
        //用于存储action信息的列表
        ArrayList<AbstractGameAction> actList = new ArrayList<AbstractGameAction>();
        actList.add(new SFXAction("VO_CULTIST_1B"));
        //赠送一张狂暴
        actList.add(new MakeTempCardInDrawPileAction(new Berserk(),1,true,true));
        //添加格挡的效果数据
        actList.add(new ApplyPowerAction(actSource, actSource, new RegenPower(actSource, baseValue)));
        return actList;
    }

}
