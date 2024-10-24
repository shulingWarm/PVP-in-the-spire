package pvp_in_the_spire.events;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.BaseEvent;
import pvp_in_the_spire.actions.TransformCardAction;
import pvp_in_the_spire.character.PlayerMonster;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//状态牌转移相关的事件
public class TransformCardEvent extends BaseEvent {

    AbstractCard card;
    int sendNum;
    int sendType;
    PlayerMonster monster;

    public TransformCardEvent(AbstractCard card,
                              int sendNum,
                              int sendType,
                              PlayerMonster monster)
    {
        this.eventId = "TransformCardEvent";
        this.card = card;
        this.sendNum = sendNum;
        this.sendType = sendType;
        this.monster = monster;
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        try
        {
            streamHandle.writeInt(monster.playerTag);
            streamHandle.writeUTF(card.cardID);
            streamHandle.writeInt(sendType);
            streamHandle.writeInt(sendNum);
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
            //读取玩家的tag
            int tempTag = streamHandle.readInt();
            if(tempTag != GlobalManager.myPlayerTag)
                return;
            String cardId = streamHandle.readUTF();
            if(!CardLibrary.cards.containsKey(cardId))
                return;
            int tempType = streamHandle.readInt();
            int tempNum = streamHandle.readInt();
            //构造action
            AbstractDungeon.actionManager.addToBottom(
                    TransformCardAction.getMakeCardAction(
                        CardLibrary.cards.get(cardId),tempNum,tempType
                    )
            );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
