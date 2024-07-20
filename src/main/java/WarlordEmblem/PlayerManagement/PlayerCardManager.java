package WarlordEmblem.PlayerManagement;

import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.HashMap;

//玩家的卡牌管理器
//这是用于更新显示玩家的卡牌的
//这个东西后面还会进一步加入渲染功能
public class PlayerCardManager {

    public HashMap<Integer, AbstractCard> cardMap;

    public PlayerCardManager()
    {
        cardMap = new HashMap<>();
    }

    //添加新的卡牌记录
    public void addCard(int idCard, AbstractCard card)
    {
        cardMap.put(idCard,card);
    }

    //根据code获取card
    public AbstractCard getCard(int idCard)
    {
        if(cardMap.containsKey(idCard))
            return cardMap.get(idCard);
        return null;
    }
}
