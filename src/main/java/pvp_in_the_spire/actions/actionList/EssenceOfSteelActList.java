package pvp_in_the_spire.actions.actionList;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.red.Barricade;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;

import java.util.ArrayList;

public class EssenceOfSteelActList extends ActListGeneratorBase{

    //是否已经给过壁垒
    boolean haveGiveCard = false;


    public ArrayList<AbstractGameAction> getActList(AbstractCreature actSource, int baseValue)
    {
        //用于存储action信息的列表
        ArrayList<AbstractGameAction> actList = new ArrayList<AbstractGameAction>();
        //播放声音的动画
        actList.add(new SFXAction("VO_CULTIST_1B"));
        //给玩家添加一张壁垒
        if(!haveGiveCard)
        {
            actList.add(new MakeTempCardInDrawPileAction(new Barricade(),1,true,true));
            haveGiveCard = true;
        }
        //叠加多层护甲
        actList.add(new ApplyPowerAction(actSource,actSource,new PlatedArmorPower(actSource,baseValue)));
        return actList;
    }

}
