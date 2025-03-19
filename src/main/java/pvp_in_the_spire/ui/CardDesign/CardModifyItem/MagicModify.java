package pvp_in_the_spire.ui.CardDesign.CardModifyItem;

import pvp_in_the_spire.card.AdaptableCard;
import pvp_in_the_spire.card.CardAction.MagicCardAction;
import pvp_in_the_spire.ui.Events.CardModifyEvent;

public class MagicModify implements CardModifyEvent {

    @Override
    public String initCardInfo(AdaptableCard card) {
        return String.valueOf(card.baseMagicNumber);
    }

    @Override
    public void cardModifyTrigger(AdaptableCard card, String configText) {
        try
        {
            int tempMagic = Integer.parseInt(configText);
            card.addActionToCard(new MagicCardAction(tempMagic));
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
    }
}
