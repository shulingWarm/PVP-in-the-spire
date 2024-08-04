package WarlordEmblem.Events;

import WarlordEmblem.GlobalManager;
import WarlordEmblem.PVPApi.BaseEvent;
import WarlordEmblem.character.PlayerMonster;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class HealEvent extends BaseEvent {

    int healAmount;

    public HealEvent(int healAmount)
    {
        this.healAmount = healAmount;
        this.eventId = "HealEvent";
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        try{
            GlobalManager.playerManager.encodePlayer(streamHandle);
            streamHandle.writeInt(healAmount);
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
            PlayerMonster playerMonster = GlobalManager.playerManager.decodePlayer(streamHandle);
            int tempAmount = streamHandle.readInt();
            if(playerMonster != null)
                playerMonster.heal(tempAmount);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
