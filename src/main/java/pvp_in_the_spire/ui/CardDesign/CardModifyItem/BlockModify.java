package pvp_in_the_spire.ui.CardDesign.CardModifyItem;

import pvp_in_the_spire.card.AdaptableCard;
import pvp_in_the_spire.card.CardAction.BlockCardAction;
import pvp_in_the_spire.ui.Events.CardModifyEvent;

import java.io.IOException;

public class BlockModify implements CardModifyEvent {

    @Override
    public String initCardInfo(AdaptableCard card) {
        if(card.baseBlock > 0)
        {
            return String.valueOf(card.baseBlock);
        }
        return "";
    }

    @Override
    public void cardModifyTrigger(AdaptableCard card, String configText) {
        try
        {
            int tempBlock = Integer.parseInt(configText);
            card.addActionToCard(new BlockCardAction(tempBlock));
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
    }
}
