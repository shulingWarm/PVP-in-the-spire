package pvp_in_the_spire.events;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.BaseEvent;
import pvp_in_the_spire.actions.ExhaustDrawPileCard;
import pvp_in_the_spire.patches.CardShowPatch.UseCardSend;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//删除对方的某张牌的事件，这是为了适配顺手牵羊
public class RemoveCardEvent extends BaseEvent {

    int idCard;
    int targetTag;

    public RemoveCardEvent(int idCard,int targetTag)
    {
        this.eventId = "RemoveCardEvent";
        this.idCard = idCard;
        this.targetTag = targetTag;
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        try
        {
            //编码目标玩家
            streamHandle.writeInt(targetTag);
            streamHandle.writeInt(idCard);
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
            int tempTag = streamHandle.readInt();
            if(tempTag != GlobalManager.myPlayerTag)
                return;
            int tempCardId = streamHandle.readInt();
            //消耗目标卡牌
            AbstractCard card = UseCardSend.getPlayerCardInstance(tempCardId);
            if(card!=null)
            {
                AbstractDungeon.actionManager.addToTop(new ExhaustDrawPileCard(card));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
