package pvp_in_the_spire.events;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.BaseEvent;
import pvp_in_the_spire.network.PlayerInfo;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.tempCards.Expunger;
import com.megacrit.cardcrawl.helpers.CardLibrary;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


//发送卡牌信息的事件
public class CardInfoEvent extends BaseEvent {

    public AbstractCard card;
    int idCard;

    public CardInfoEvent(AbstractCard card,int idCard)
    {
        this.eventId = "CardInfoEvent";
        this.card = card;
        this.idCard = idCard;
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        //发送卡牌的名称
        try{
            //发送本玩家的tag
            GlobalManager.playerManager.encodePlayer(streamHandle);
            streamHandle.writeUTF(card.cardID);
            //升级过的次数
            streamHandle.writeInt(card.timesUpgraded);
            //卡牌的编码
            streamHandle.writeInt(idCard);
            //卡牌的基本magic number, 这是为了兼容灭除之刃
            streamHandle.writeInt(card.baseMagicNumber);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void decode(DataInputStream streamHandle) {
        try
        {
            //解码出对应的玩家
            PlayerInfo info = GlobalManager.playerManager.decodePlayerInfo(streamHandle);
            if(info == null)
                return;
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
            info.cardManager.addCard(cardHashCode,card);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
