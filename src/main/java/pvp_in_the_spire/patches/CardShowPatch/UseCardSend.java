package pvp_in_the_spire.patches.CardShowPatch;

import pvp_in_the_spire.events.CardInfoEvent;
import pvp_in_the_spire.events.UseCardEvent;
import pvp_in_the_spire.pvp_api.Communication;
import pvp_in_the_spire.SocketServer;
import pvp_in_the_spire.actions.FightProtocol;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.tempCards.Expunger;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//显示对方玩家出过的牌
public class UseCardSend {

    public static HashMap<AbstractCard,Integer> playerCardMap =
            new HashMap<AbstractCard,Integer>();
    //敌人的牌，需要记录从数字到牌的映射
    public static HashMap<Integer,AbstractCard> monsterCardMap =
            new HashMap<Integer,AbstractCard>();

    //从哈希表反算敌人的牌id 使用频率不高，所以遍历一下查找就可以了
    public static int getMonsterCardId(AbstractCard card)
    {
        //遍历敌人手牌的每个配对
        for(Map.Entry<Integer,AbstractCard> eachCard : monsterCardMap.entrySet())
        {
            //判断是否为要找的牌
            if(eachCard.getValue()==card)
                return eachCard.getKey();
        }
        System.out.printf("cannot find %s\n",card.name);
        return -1;
    }

    //根据id反算自己的手牌标号
    public static AbstractCard getPlayerCardInstance(int idCard)
    {
        for(Map.Entry<AbstractCard,Integer> eachCard : playerCardMap.entrySet())
        {
            //判断是否为要找的牌
            if(eachCard.getValue()==idCard)
                return eachCard.getKey();
        }
        System.out.printf("cannot find id%d\n",idCard);
        return null;
    }

    //初始化记录牌名的哈希表
    public static void initHashMap()
    {
        playerCardMap.clear();
        monsterCardMap.clear();
    }

    //发送卡牌的基本信息
    public static void sendCardInfo(AbstractCard card,
        DataOutputStream streamHandle,int cardId)
    {
        //发送卡牌的数据头
        try
        {
            streamHandle.writeInt(FightProtocol.CARD_INFO_HEAD);
            //发送卡牌的名称
            streamHandle.writeUTF(card.cardID);
            //发送已经被升级过的次数
            streamHandle.writeInt(card.timesUpgraded);
            //发送卡牌的编码
            streamHandle.writeInt(cardId);
            //发送卡牌的magic number,为了兼容灭除之刃
            streamHandle.writeInt(card.baseMagicNumber);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //对卡牌信息的解码
    public static void receiveCardInfo(DataInputStream streamHandle)
    {
        try
        {
            //接收卡牌的名称
            String cardName = streamHandle.readUTF();
            //接收卡牌升级过的次数
            int upgradeNum = streamHandle.readInt();
            //接收卡牌对应的哈希编号
            int cardHashCode = streamHandle.readInt();
            //接收magicNumber
            int magicNumber = streamHandle.readInt();
            //根据卡牌名称映射出实际的卡牌
            if(!CardLibrary.cards.containsKey(cardName))
            {
                System.out.print("unknown card: ");
                System.out.println(cardName);
                return;
            }
            //复制一份卡牌
            AbstractCard card = CardLibrary.cards.get(cardName).makeCopy();
            //判断是不是灭除之刃
            if(card instanceof Expunger)
            {
                ((Expunger) card).setX(magicNumber);
            }
            //把牌升级指定的次数
            for(int idUp=0;idUp<upgradeNum;++idUp)
            {
                card.upgrade();
            }
            //把牌记录下来
            monsterCardMap.put(cardHashCode,card);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //获得卡牌的编码，这里的特殊之处是如果卡牌是第一次发的话，顺便把卡牌的基本定义也发出去
    //这里特指的是发送用于通信的id
    public static int getCardCommunicationID(AbstractCard card)
    {
        //判断是否已经记录过这个card
        if(!playerCardMap.containsKey(card))
        {
            int newCode = playerCardMap.size();
            playerCardMap.put(card,newCode);
            //发送卡牌的基本信息
            Communication.sendEvent(new CardInfoEvent(card,newCode));
            // sendCardInfo(card,streamHandle,newCode);
            return newCode;
        }
        //已经存在的情况下直接正常返回就可以
        return playerCardMap.get(card);
    }

    public static void useCardEncode(DataOutputStream streamHandle,
       AbstractCard card)
    {
        //发送信息前缀
        try
        {
            //获得卡牌的编码
            int cardCode = getCardCommunicationID(card);
            streamHandle.writeInt(FightProtocol.USE_CARD);
            //发送卡牌的编码
            streamHandle.writeInt(cardCode);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //对方使用牌信息的解码操作
    public static void useCardDecode(DataInputStream streamHandle)
    {
        try
        {
            //接收牌的编号
            int cardCode = streamHandle.readInt();
            //检查是否存在这个牌
            if(monsterCardMap.containsKey(cardCode))
            {
//                System.out.println("card decode");
//                System.out.println(cardCode);
                //获取对应的牌
                AbstractCard card = monsterCardMap.get(cardCode);
                //System.out.println(card.cardID);
                card.unfadeOut();
                //调用显示这个牌
                AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(card));
            }
            else {
                System.out.println("cannot find card code");
                System.out.println(cardCode);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static class CardUseManager
    {

        public AbstractCard card;

        int usedAmount = 0;

        public static int MAX_USE_TIME = 2;

        //对它变费时的费用
        int saveCost = 0;
        int saveTurnCost = 0;
        boolean cardModifiedFlag = false;

        public CardUseManager(AbstractCard card)
        {
            this.card = card;
        }

        public void improveAmount()
        {
            ++usedAmount;
            if(usedAmount>MAX_USE_TIME)
            {
                //第一次变费时记录它默认的状态
                if(usedAmount==(MAX_USE_TIME+1))
                {
                    saveCost = card.cost;
                    saveTurnCost = card.costForTurn;
                    cardModifiedFlag = card.isCostModified;
                }
                //给牌加费
                card.cost++;
                card.costForTurn++;
                card.isCostModified = true;
            }
        }

        //恢复卡牌的费用
        public void resetCard()
        {
            //判断是否需要恢复
            if(usedAmount>MAX_USE_TIME)
            {
                card.cost = saveCost;
                card.costForTurn = saveTurnCost;
                card.isCostModified = this.cardModifiedFlag;
            }
        }


    }


    //捕捉玩家使用的牌
    @SpirePatch(clz = AbstractPlayer.class,method = "useCard")
    public static class SendUseCardAction
    {

        //每种牌使用过的次数
        public static HashMap<AbstractCard,CardUseManager> cardUseTime=
                new HashMap<AbstractCard,CardUseManager>();

        //开始涨费的使用次数
        public static final int UP_COST_TIME = 2;

        //恢复上回合使用过的牌的费用
        public static void resetCardCost()
        {
            //遍历所有的牌
            for(Map.Entry<AbstractCard,CardUseManager> eachCard : cardUseTime.entrySet())
            {
                //恢复使用次数
                eachCard.getValue().resetCard();
            }
            //清空哈希表
            cardUseTime.clear();
        }

        //更新卡牌的使用次数
        public static void updateUseTime(AbstractCard card)
        {
            //判断是否有这个牌
            if(!cardUseTime.containsKey(card))
            {
                cardUseTime.put(card,new CardUseManager(card));
            }
            cardUseTime.get(card).improveAmount();
        }

        @SpirePostfixPatch
        public static void fix(AbstractPlayer __instance,
           AbstractCard c, AbstractMonster monster, int energyOnUse)
        {
            if(!SocketServer.USE_NETWORK)
            {
                return;
            }
            //当发生卡牌使用时，把用牌的消息发送给对方
            int cardCode = getCardCommunicationID(c);
            Communication.sendEvent(new UseCardEvent(cardCode));
            updateUseTime(c);
        }
    }
}
