package pvp_in_the_spire.events;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.BaseEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//激发充能球的操作
public class EvokeOrbEvent extends BaseEvent {

    public EvokeOrbEvent()
    {
        this.eventId = "EvokeOrbEvent";
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        //记录本玩家的tag即可
        try
        {
            streamHandle.writeInt(GlobalManager.myPlayerTag);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void decode(DataInputStream streamHandle) {
        //获取对应的玩家
        try
        {
            int playerTag = streamHandle.readInt();
            GlobalManager.playerManager.getPlayerInfo(playerTag).playerMonster.evokeOrb();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
