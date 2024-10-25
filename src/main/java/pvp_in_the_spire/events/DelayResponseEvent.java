package pvp_in_the_spire.events;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.BaseEvent;
import pvp_in_the_spire.patches.RenderPatch;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//延迟信息响应的事件
public class DelayResponseEvent extends BaseEvent {

    public int receiver;
    public int responseTag;

    public DelayResponseEvent(int receiver,int responseTag)
    {
        this.eventId = "DelayResponseEvent";
        this.receiver = receiver;
        this.responseTag = responseTag;
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        try
        {
            //发送receiver
            streamHandle.writeInt(this.receiver);
            //发送自己的tag
            streamHandle.writeInt(GlobalManager.myPlayerTag);
            streamHandle.writeInt(responseTag);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //接收player tag的响应
    @Override
    public void decode(DataInputStream streamHandle) {
        try
        {
            int tempReceiver = streamHandle.readInt();
            //如果不是发给自己的就直接退出
            if(tempReceiver != GlobalManager.myPlayerTag)
                return;
            //接收发送者的tag
            int sender = streamHandle.readInt();
            //接收请求的tag
            int tempTag = streamHandle.readInt();
            if(RenderPatch.delayBox != null)
                RenderPatch.delayBox.receiveResponse(sender,tempTag);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
