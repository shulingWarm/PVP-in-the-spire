package pvp_in_the_spire.ui.CardDesign.CardModifyItem;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pvp_in_the_spire.ui.Events.CardModifyEvent;

//对于伤害值修改的实现，用于修改卡牌的数值
public class AttackModify implements CardModifyEvent {


    @Override
    public String initCardInfo(AbstractCard card) {
        return "伤害值";
    }

    @Override
    public void cardModifyTrigger(AbstractCard card, String configText) {
        try
        {
            //记录新的卡牌数值
            card.damage = Integer.parseInt(configText);
        }
        catch (NumberFormatException e)
        {
            //
        }
    }
}
