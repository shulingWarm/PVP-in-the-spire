package pvp_in_the_spire.events;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.BaseEvent;
import pvp_in_the_spire.character.PlayerMonster;

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
