package pvp_in_the_spire.events;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.BaseEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ConfigReadyEvent extends BaseEvent {

    public boolean readyFlag;

    public ConfigReadyEvent(boolean readyFlag)
    {
        this.readyFlag = readyFlag;
        this.eventId = "ConfigReadyEvent";
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        try
        {
            streamHandle.writeInt(GlobalManager.myPlayerTag);
            streamHandle.writeBoolean(this.readyFlag);
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
            //读取player tag
            int tempTag = streamHandle.readInt();
            //读取准备状态
            boolean tempFlag = streamHandle.readBoolean();
            //更新准备状态
            GlobalManager.playerManager.updateReadyFlag(
                tempTag,tempFlag
            );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}
