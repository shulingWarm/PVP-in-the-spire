package pvp_in_the_spire.card.CardAction;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pvp_in_the_spire.card.AdaptableCard;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.HashMap;

//卡牌的执行操作
//这是专门用于执行的卡牌操作
public abstract class AbstractCardAction {

    //action的id
    public String actionId;

    //静态的action library
    public static HashMap<String, AbstractCardAction> actionLibrary = new HashMap<>();

    //注册静态的library
    public static void registerAction(String actionId, AbstractCardAction action)
    {
        actionLibrary.put(actionId,action);
    }

    //根据action id获得action
    public static AbstractCardAction getCardAction(String actionId)
    {
        //判断是否存在这个action
        if(actionLibrary.containsKey(actionId))
        {
            return actionLibrary.get(actionId);
        }
        return null;
    }

    public AbstractCardAction(String actionId)
    {
        this.actionId = actionId;
    }

    //对卡牌执行相应的动作
    public abstract void doCardAction(AbstractPlayer p, AbstractMonster m);

    //调整action数值时的操作
    public void adjustRepeatAction(AbstractCardAction action){}

    //判断能否直接应用在卡牌上
    public boolean tryDirectApply(AbstractCard card)
    {
        return false;
    }

    //保存卡牌的action
    public abstract void saveCardAction(DataOutputStream stream);

    //读取card action
    public abstract void loadCardAction(DataInputStream stream);

    //复制action
    public abstract AbstractCardAction copyAction();

    public String getActionId() {
        return actionId;
    }

    //复制action
    public abstract AbstractCardAction makeCopy();
}
