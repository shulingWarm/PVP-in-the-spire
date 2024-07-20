package WarlordEmblem.Events;

import WarlordEmblem.GlobalManager;
import WarlordEmblem.PVPApi.BaseEvent;
import WarlordEmblem.character.PlayerMonster;

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
        GlobalManager.playerManager.encodePlayer(streamHandle);
    }

    @Override
    public void decode(DataInputStream streamHandle) {
        PlayerMonster monster = GlobalManager.playerManager.decodePlayer(streamHandle);
        if(monster != null)
        {
            GlobalManager.getBattleInfo().updateEndTurn(monster);
        }
    }
}
