package WarlordEmblem.Events;

import WarlordEmblem.GlobalManager;
import WarlordEmblem.PVPApi.BaseEvent;
import WarlordEmblem.character.PlayerMonster;
import WarlordEmblem.patches.CardShowPatch.UseCardSend;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//玩家使用牌时的事件
public class UseCardEvent extends BaseEvent {

    public int idCard;

    public UseCardEvent(int idCard)
    {
        this.eventId = "UseCardEvent";
        this.idCard = idCard;
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        try
        {
            GlobalManager.playerManager.encodePlayer(streamHandle);
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
            PlayerMonster monster = GlobalManager.playerManager.decodePlayer(streamHandle);
            if(monster == null)
            {
                return;
            }
            int cardCode = streamHandle.readInt();
            AbstractCard tempCard = monster.playerCardManager.getCard(cardCode);
            if(tempCard != null)
            {
                tempCard.unfadeOut();
                AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(tempCard));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
