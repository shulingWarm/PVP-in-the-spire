package pvp_in_the_spire.events;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.BaseEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//这是用于在config房间里面注册用户的事件
public class RegisterPlayerEvent extends BaseEvent {

    public RegisterPlayerEvent()
    {
        eventId = "RegisterPlayerEvent";
    }

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
            GlobalManager.playerManager.registerPlayer(playerTag);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
