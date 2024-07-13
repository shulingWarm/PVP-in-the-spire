package WarlordEmblem.Events;

import WarlordEmblem.GlobalManager;
import WarlordEmblem.PVPApi.BaseEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//这是用于在config房间里面注册用户的事件
public class RegisterPlayerEvent extends BaseEvent {

    @Override
    public void encode(DataOutputStream streamHandle) {
        try
        {
            //发送自己的player tag
            streamHandle.writeInt(GlobalManager.myPlayerTag);
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void decode(DataInputStream streamHandle) {
        try
        {
            int playerTag = streamHandle.readInt();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
