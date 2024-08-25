package WarlordEmblem.Events;

import WarlordEmblem.GlobalManager;
import WarlordEmblem.PVPApi.BaseEvent;
import WarlordEmblem.network.PlayerInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//设置玩家的座位的事件
public class PlayerSeatEvent extends BaseEvent {

    //后面需要发送的信息内容
    public PlayerInfo info;
    public int idSeat;

    public PlayerSeatEvent(PlayerInfo info,int idSeat)
    {
        this.eventId = "PlayerSeatEvent";
        this.info = info;
        this.idSeat = idSeat;
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        try{
            streamHandle.writeInt(info.playerTag);
            streamHandle.writeInt(idSeat);
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
            //读取被分配座位的玩家
            int tempTag = streamHandle.readInt();
            int tempSeat = streamHandle.readInt();
            GlobalManager.playerManager.setPlayerSeat(tempTag,tempSeat);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
