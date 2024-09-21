package WarlordEmblem.Events;

import WarlordEmblem.GlobalManager;
import WarlordEmblem.PVPApi.BaseEvent;
import WarlordEmblem.network.PlayerInfo;
import WarlordEmblem.powers.CommunicatePower;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//设置power的amount的事件
public class SetPowerAmountEvent extends BaseEvent {

    public int idPower;
    public int amount;

    public SetPowerAmountEvent(int idPower,int amount)
    {
        this.eventId = "SetPowerAmountEvent";
        this.idPower = idPower;
        this.amount = amount;
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        try
        {
            GlobalManager.playerManager.encodePlayer(streamHandle);
            streamHandle.writeInt(idPower);
            streamHandle.writeInt(this.amount);
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
            int tempPower = streamHandle.readInt();
            int tempAmount = streamHandle.readInt();
            CommunicatePower power = info.powerManager.getPower(tempPower);
            if(power != null)
            {
                power.setAmount(tempAmount,false);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
