package pvp_in_the_spire.card.CardAction;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pvp_in_the_spire.card.AdaptableCard;

//卡牌的执行操作
//这是专门用于执行的卡牌操作
public abstract class AbstractCardAction {

    //action的id
    public String actionId;

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

    public String getActionId() {
        return actionId;
    }
}
