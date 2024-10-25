package pvp_in_the_spire.events;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.BaseEvent;
import pvp_in_the_spire.pvp_api.Communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//延迟测试的请求消息
public class DelayRequestEvent extends BaseEvent {

    //要请求测试的目标玩家
    public int playerTag;
    public int requestTag;

    //请求测试延迟的信号
    public DelayRequestEvent(int playerTag,int requestTag)
    {
        this.eventId = "DelayRequestEvent";
        this.playerTag = playerTag;
        this.requestTag = requestTag;
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        try
        {
            streamHandle.writeInt(playerTag);
            streamHandle.writeInt(GlobalManager.myPlayerTag);
            streamHandle.writeInt(this.requestTag);
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
            int receiver = streamHandle.readInt();
            if(receiver != GlobalManager.myPlayerTag)
                return;
            int sender = streamHandle.readInt();
            int tempRequest = streamHandle.readInt();
            //给目标回传响应delay的信号
            Communication.advanceSendEvent(new DelayResponseEvent(sender,tempRequest),
                sender);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
