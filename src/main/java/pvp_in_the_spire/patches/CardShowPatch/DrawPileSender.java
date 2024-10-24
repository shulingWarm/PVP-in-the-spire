package pvp_in_the_spire.patches.CardShowPatch;

import pvp_in_the_spire.events.DrawCardUpdateEvent;
import pvp_in_the_spire.pvp_api.Communication;
import pvp_in_the_spire.actions.FightProtocol;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

//抽牌堆信息的发送 里面只有静态函数，更新的时候交给control monster去更新
public class DrawPileSender {

    //实时显示的抽牌数
    public static final int SHOW_DRAW_NUM = 5;
    //升级次数的字符位
    public static final int UPGRADE_LEVEL = 100000;

    //即将抽到的牌
    public static ArrayList<AbstractCard> drawingCards = new ArrayList<>();

    //比较抽牌堆和上次的记录
    public static boolean compareCardRecord(ArrayList<AbstractCard> realGroup,
         ArrayList<AbstractCard> recordGroup)
    {
        //如果卡组不小于5并且真实卡组更多的话，就直接依次比较
        if(recordGroup.size()==realGroup.size() ||
            (realGroup.size()>=SHOW_DRAW_NUM && recordGroup.size()==SHOW_DRAW_NUM)
            )
        {
            for(int idCard=0;idCard<recordGroup.size();++idCard)
            {
                if(recordGroup.get(idCard)!=realGroup.get(realGroup.size()-idCard-1))
                    return false;
            }
            return true;
        }
        return false;
    }

    //记录目前要抽到的牌
    public static void recordCards(ArrayList<AbstractCard> cards)
    {
        int sendingNum = Math.min(cards.size(), SHOW_DRAW_NUM);
        drawingCards.clear();
        for(int idCard=1;idCard<=sendingNum;++idCard)
            drawingCards.add(cards.get(cards.size()-idCard));
    }

    //发送目前要抽到的牌
    public static void drawCardEncode(DataOutputStream streamHandle,
      ArrayList<Integer> cards)
    {
        try
        {
            streamHandle.writeInt(FightProtocol.DRAWING_CARD_UPDATE);
            //要发送的牌数
            streamHandle.writeInt(cards.size());
            for(int eachCardId : cards)
            {
                streamHandle.writeInt(eachCardId);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //接收抽牌堆的信息
    public static void drawCardDecode(DataInputStream streamHandle)
    {
        try
        {
            //需要处理的抽牌信息
            CardRecorder recorder = HandCardSend.monsterCardList;
            recorder.justUpdateFlag=true;
            recorder.drawingCards.clear();
            //读取牌数
            int cardNum = streamHandle.readInt();
            for(int idCard=0;idCard<cardNum;++idCard)
            {
                //读取当前的卡牌id
                int tempId = streamHandle.readInt();
                //解码升级次数
                int upgradeTime = tempId/UPGRADE_LEVEL;
                tempId = tempId%UPGRADE_LEVEL;
                if(UseCardSend.monsterCardMap.containsKey(tempId))
                {
                    AbstractCard tempCard = UseCardSend.monsterCardMap.get(tempId);
                    //补充缺少的升级次数
                    upgradeTime-=tempCard.timesUpgraded;
                    for(int i=0;i<upgradeTime;++i)
                        tempCard.upgrade();
                    //在列表里面记录这个牌
                    recorder.drawingCards.add(tempCard);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //更新即将抽到的牌的显示
    public static void updateDrawingCards()
    {
        //目前的抽牌堆
        ArrayList<AbstractCard> cardGroup = AbstractDungeon.player.drawPile.group;
        if(!compareCardRecord(cardGroup,drawingCards))
        {
            recordCards(cardGroup);
            //准备每个牌的标号
            ArrayList<Integer> cardIdList = new ArrayList<>();
            for(AbstractCard eachCard : drawingCards)
            {
                //计算两边的通信哈希值
                int tempCode = UseCardSend.getCardCommunicationID(eachCard);
                //叠加升级过的字符位
                tempCode += eachCard.timesUpgraded*UPGRADE_LEVEL;
                cardIdList.add(tempCode);
            }
            Communication.sendEvent(new DrawCardUpdateEvent(cardIdList));
        }
    }

}
