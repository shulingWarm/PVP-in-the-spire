package pvp_in_the_spire.ui.CardFilter;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pvp_in_the_spire.events.BanCardStream;
import pvp_in_the_spire.pvp_api.Communication;

import java.util.ArrayList;
import java.util.HashSet;

//卡牌过滤器
public class CardFilter {

    public HashSet<String> bannedCards = new HashSet<>();

    public void banCard(String cardId)
    {
        bannedCards.add(cardId);
    }

    public void restoreCard(String cardId)
    {
        bannedCards.remove(cardId);
    }

    public boolean isCardAvailable(String cardId)
    {
        return !(bannedCards.contains(cardId));
    }

    public ArrayList<AbstractCard> filterCard(ArrayList<AbstractCard> cards)
    {
        ArrayList<AbstractCard> retCards = new ArrayList<>();
        for(AbstractCard eachCard : cards)
        {
            if(this.isCardAvailable(eachCard.cardID))
            {
                retCards.add(eachCard);
            }
        }
        return retCards;
    }

    //发送所有的卡牌禁用信息
    public void sendBanCardStage()
    {
        ArrayList<String> banCardNames = new ArrayList<>();
        boolean resetFlag = true;
        for(String eachCard : bannedCards)
        {
            banCardNames.add(eachCard);
            if(banCardNames.size()>=10)
            {
                Communication.sendEvent(new BanCardStream(banCardNames,resetFlag));
                banCardNames = new ArrayList<>();
                resetFlag = false;
            }
        }
        if(!banCardNames.isEmpty() || resetFlag)
        {
            Communication.sendEvent(new BanCardStream(banCardNames,resetFlag));
        }
    }

}
