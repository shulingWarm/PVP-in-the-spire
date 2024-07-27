package WarlordEmblem.PlayerManagement;

import WarlordEmblem.patches.CardShowPatch.CardRecorder;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;
import java.util.HashMap;

//玩家的卡牌管理器
//这是用于更新显示玩家的卡牌的
//这个东西后面还会进一步加入渲染功能
public class PlayerCardManager {

    public HashMap<Integer, AbstractCard> cardMap;

    //当前角色的实际卡牌内容
    public CardRecorder cardRecorder;

    public PlayerCardManager()
    {
        cardMap = new HashMap<>();
        //记录当前角色的实际卡牌内容
        this.cardRecorder = new CardRecorder();
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

    //把id list转换成card list
    public void convertToCardList(ArrayList<Integer> idList,
                                  ArrayList<AbstractCard> cardList
    )
    {
        cardList.clear();
        for(int eachId : idList)
        {
            AbstractCard tempCard = getCard(eachId);
            if(tempCard != null)
                cardList.add(tempCard);
        }
    }

    //更新角色的手牌
    public void updateHandCard(ArrayList<Integer> cardIdList)
    {
        //把手牌的id列表转换成card的列表
        convertToCardList(cardIdList,this.cardRecorder.cardList);
        cardRecorder.justUpdateFlag = true;
    }
}
