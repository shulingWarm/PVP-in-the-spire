package pvp_in_the_spire.events;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.BaseEvent;
import pvp_in_the_spire.network.PlayerInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//移除对应的power的事件
public class RemovePowerEvent extends BaseEvent {

    int idPower;
    int playerTag;

    public RemovePowerEvent(int playerTag,int idPower)
    {
        this.idPower = idPower;
        this.eventId = "RemovePowerEvent";
        this.playerTag = playerTag;
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        try
        {
            streamHandle.writeInt(this.playerTag);
            //写入能量的id
            streamHandle.writeInt(idPower);
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
            PlayerInfo info = GlobalManager.playerManager.decodePlayerInfo(streamHandle);
            if(info == null)
                return;
            //读取要移除的power
            int tempPower = streamHandle.readInt();
            //调用对应的info来移除power
            info.powerManager.removePower(tempPower,false);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}
