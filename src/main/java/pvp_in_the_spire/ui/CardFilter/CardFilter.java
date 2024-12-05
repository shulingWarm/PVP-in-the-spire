package pvp_in_the_spire.ui.CardFilter;

import com.megacrit.cardcrawl.cards.AbstractCard;

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

}
