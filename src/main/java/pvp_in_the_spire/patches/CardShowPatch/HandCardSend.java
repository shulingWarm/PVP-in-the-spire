package pvp_in_the_spire.patches.CardShowPatch;

import pvp_in_the_spire.events.UpdateHandCardEvent;
import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.Communication;
import pvp_in_the_spire.SocketServer;
import pvp_in_the_spire.actions.FightProtocol;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

//当玩家的手牌发生变化时，发送手牌的信息
public class HandCardSend {

    //敌人头上显示的手牌序列
    public static CardRecorder monsterCardList = new CardRecorder();

    //对发来的手牌信息的解码
    public static void handCardDecode(DataInputStream streamHandle)
    {
        monsterCardList.cardList.clear();
        try
        {
            //读取牌的个数
            int cardNum = streamHandle.readInt();
            //遍历读取每个位置的牌
            for(int idCard=0;idCard<cardNum;++idCard)
            {
                //读取当前位置的id
                int tempCode = streamHandle.readInt();
                //读取这个id对应的牌
                if(UseCardSend.monsterCardMap.containsKey(tempCode))
                {
                    AbstractCard tempCard = UseCardSend.monsterCardMap.get(tempCode);
                    //在列表里面记录这个牌
                    monsterCardList.cardList.add(tempCard);
                }
            }
            monsterCardList.justUpdateFlag = true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //发送卡牌的编码信息
    public static void handCardEncode(ArrayList<Integer> codeList,
      DataOutputStream streamHandle)
    {
        try
        {
            streamHandle.writeInt(FightProtocol.UPDATE_HAND_CARD);
            //发送牌数
            streamHandle.writeInt(codeList.size());
            //遍历发送每个手牌的标号
            Iterator codeIter = codeList.iterator();
            while(codeIter.hasNext())
            {
                int tempCode = (int)codeIter.next();
                streamHandle.writeInt(tempCode);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //监测玩家手牌位置的更新，如果牌的位置更新，在另一个玩家的手里更新显示
    @SpirePatch(clz = CardGroup.class,method = "refreshHandLayout")
    public static class HandCardRefreshEvent
    {
        //发生变化时，遍历所有的手牌，告诉对面目前都有什么牌
        @SpirePrefixPatch
        public static void fix(CardGroup __instance)
        {
            //我方已经死亡的情况下就不需要做这些了
            if(!SocketServer.USE_NETWORK ||
                GlobalManager.getBattleInfo().selfDeadFlag)
            {
                return;
            }
            //计算每个牌的列表
            ArrayList<Integer> cardIdList = new ArrayList<Integer>();
            //遍历每个牌，计算它的标号
            Iterator cardIter = __instance.group.iterator();
            while (cardIter.hasNext())
            {
                //取出下一个位置的牌
                AbstractCard tempCard = (AbstractCard) cardIter.next();
                //计算这个牌的编码
                int tempCode = UseCardSend.getCardCommunicationID(tempCard);
                cardIdList.add(tempCode);
            }
            //调用发送卡牌的编码信息
            Communication.sendEvent(new UpdateHandCardEvent(cardIdList));
            //更新即将抽到的牌的信息
            DrawPileSender.updateDrawingCards();
        }
    }

}
