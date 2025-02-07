package pvp_in_the_spire.ui.CardDesign.CardModifyItem;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pvp_in_the_spire.card.AdaptableCard;
import pvp_in_the_spire.card.CardAction.DamageCardAction;
import pvp_in_the_spire.ui.Events.CardModifyEvent;

//对于伤害值修改的实现，用于修改卡牌的数值
public class AttackModify implements CardModifyEvent {

    @Override
    public String initCardInfo(AdaptableCard card) {
        if(card.baseCard.baseDamage > 0)
            return String.valueOf(card.baseCard.baseDamage);
        return "";
    }

    @Override
    public void cardModifyTrigger(AdaptableCard card, String configText) {
        try
        {
            int tempDamage = Integer.parseInt(configText);
            card.addActionToCard(new DamageCardAction(tempDamage));
        }
        catch (NumberFormatException e)
        {
            //
        }
    }
}
