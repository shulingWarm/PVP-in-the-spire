package pvp_in_the_spire.events;

import pvp_in_the_spire.pvp_api.BaseEvent;
import pvp_in_the_spire.ui.CardFilter.CardFilterScreen;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//卡牌禁用信息被修改时的事件
public class BanCardStageChangeEvent extends BaseEvent {

    public String cardId;
    public boolean banStage;

    public BanCardStageChangeEvent(String cardId, boolean banStage)
    {
        this.eventId = "BanCardStageChangeEvent";
        this.cardId = cardId;
        this.banStage = banStage;
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        try
        {
            streamHandle.writeUTF(this.cardId);
            streamHandle.writeBoolean(this.banStage);
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
            String tempCardId = streamHandle.readUTF();
            boolean tempStage = streamHandle.readBoolean();
            CardFilterScreen.instance.changeCardBanStage(tempCardId,tempStage);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
