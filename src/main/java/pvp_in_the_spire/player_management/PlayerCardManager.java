package pvp_in_the_spire.player_management;

import pvp_in_the_spire.patches.CardShowPatch.CardRecorder;
import pvp_in_the_spire.patches.CardShowPatch.DrawPileSender;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        //解码升级次数
        int upgradeTime = idCard/ DrawPileSender.UPGRADE_LEVEL;
        idCard = idCard%DrawPileSender.UPGRADE_LEVEL;
        if(cardMap.containsKey(idCard))
        {
            AbstractCard tempCard = cardMap.get(idCard);
            //判断是否需要补充升级次数
            upgradeTime -= tempCard.timesUpgraded;
            for(int idUp=0;idUp<upgradeTime;++idUp)
                tempCard.upgrade();
            return tempCard;
        }
        return null;
    }

    //根据卡牌获取对应的id
    public int getCardId(AbstractCard card)
    {
        for(Map.Entry<Integer,AbstractCard> eachCard : cardMap.entrySet())
        {
            //判断是否为要找的牌
            if(eachCard.getValue()==card)
                return eachCard.getKey();
        }
        return -1;
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

    //更新即将抽到的牌
    public void updateDrawPile(ArrayList<Integer> cardIdList)
    {
        convertToCardList(cardIdList,this.cardRecorder.drawingCards);
        cardRecorder.justUpdateFlag = true;
    }
}
