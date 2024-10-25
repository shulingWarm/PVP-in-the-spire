package pvp_in_the_spire.events;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.BaseEvent;
import pvp_in_the_spire.pvp_api.Communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BeginTurnEvent extends BaseEvent {

    public int playerTag;

    public BeginTurnEvent(int playerTag)
    {
        this.eventId = "BeginTurnEvent";
        this.playerTag = playerTag;
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        try
        {
            streamHandle.writeInt(playerTag);
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
            int tempTag = streamHandle.readInt();
            //判断是不是发给自己的
            if(tempTag == GlobalManager.myPlayerTag)
            {
                //调用本地的tag启动
                GlobalManager.playerManager.startSelfPlayerTurn();
                //给主机反馈自己的准备状态
                Communication.sendEvent(new BeginTurnResponseEvent());
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
