package WarlordEmblem.Events;

import WarlordEmblem.GlobalManager;
import WarlordEmblem.PVPApi.BaseEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//结束回合的事件
public class EndTurnEvent extends BaseEvent {

    public EndTurnEvent()
    {
        this.eventId = "EndTurnEvent";
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        //发送自己的tag
        try
        {
            streamHandle.writeInt(GlobalManager.myPlayerTag);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void decode(DataInputStream streamHandle) {
        try
        {
            int playerTag = streamHandle.readInt();
            GlobalManager.getBattleInfo().updateEndTurn(playerTag);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
