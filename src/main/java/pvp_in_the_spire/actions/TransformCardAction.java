package pvp_in_the_spire.actions;

import pvp_in_the_spire.events.TransformCardEvent;
import pvp_in_the_spire.pvp_api.Communication;
import pvp_in_the_spire.character.PlayerMonster;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//把一个指定的牌从当前的牌组中消耗，然后转移到对方的牌组中
public class TransformCardAction extends AbstractGameAction {

    //需要被转移给对面的牌
    public AbstractCard card;
    //牌所属的位置，例如弃牌堆，手牌这些
    public CardGroup group;
    //要发送的卡牌数量
    public int sendNum;
    //要发送的目标玩家
    PlayerMonster monster;

    //发送卡牌的种类
    //送进手牌
    public static final int HAND=0;
    //送进弃牌堆
    public static final int DISCARD=1;
    //送进抽牌堆
    public static final int DRAW = 2;

    //记录需要被转换的牌
    public TransformCardAction(AbstractCard card, CardGroup group,int sendNum,PlayerMonster monster)
    {
        this.card = card;
        this.group = group;
        this.sendNum = sendNum;
        this.monster = monster;
    }

    public static void addCardEncode(AbstractCard card, int sendNum,int sendType, DataOutputStream streamHandle)
    {
        try
        {
            //发送给对面施加卡牌的数据头
            streamHandle.writeInt(FightProtocol.TRANSFORM_CARD);
            //发送卡牌的名称
            streamHandle.writeUTF(card.cardID);
            //发送添加卡牌的方式
            streamHandle.writeInt(sendType);
            //发送要添加的牌的数量
            streamHandle.writeInt(sendNum);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static AbstractGameAction getMakeCardAction(AbstractCard card,
       int addNum,int addType)
    {
        switch (addType)
        {
            case HAND:
                return new MakeTempCardInHandAction(card,addNum);
            case DISCARD:
                return new MakeTempCardInDiscardAction(card,addNum);
        }
        return new MakeTempCardInDrawPileAction(card,addNum,true,true);
    }

    //接收加入卡牌的信息
    public static void addCardDecode(DataInputStream streamHandle)
    {
        try
        {
            //读取卡牌的名称
            String cardName = streamHandle.readUTF();
            //添加卡牌的方式
            int sendType = streamHandle.readInt();
            //读取要添加的个数
            int addNum = streamHandle.readInt();
            //判断卡库里面有没有这个牌
            if(CardLibrary.cards.containsKey(cardName))
            {
                //复制一份卡牌
                AbstractCard card = CardLibrary.cards.get(cardName).makeCopy();
                //把卡牌添加到弃牌
                AbstractDungeon.actionManager.addToBottom(
                    getMakeCardAction(card,addNum,sendType)
                );
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void sendAddCard(AbstractCard card, int sendNum, int sendType,
                                   PlayerMonster monster)
    {
        Communication.sendEvent(new TransformCardEvent(
            card,sendNum,sendType,monster
        ));
    }


    //发送卡牌的编码信息，表示把这个牌塞给对面
    public static void sendAddCard(AbstractCard card,int sendNum,PlayerMonster monster)
    {
        sendAddCard(card,sendNum,DISCARD,monster);
    }


    public void update()
    {
        this.isDone = true;
        if(!group.contains(this.card))
            return;
        //把要移除的卡牌发送给对方
        sendAddCard(this.card,this.sendNum,this.monster);
        //把牌从对应的地方移除
        group.removeCard(this.card);
    }

}
