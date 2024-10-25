package pvp_in_the_spire.events;

import pvp_in_the_spire.pvp_api.BaseEvent;
import pvp_in_the_spire.actions.ConfigProtocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//toggle按钮的触发事件
public class ToggleTriggerEvent extends BaseEvent {

    public int idToggle;
    public boolean stage;

    public ToggleTriggerEvent(int idToggle,boolean stage)
    {
        this.eventId = "ToggleTriggerEvent";
        this.idToggle = idToggle;
        this.stage = stage;
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        try
        {
            streamHandle.writeInt(idToggle);
            streamHandle.writeBoolean(stage);
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
            int tempId = streamHandle.readInt();
            boolean newStage = streamHandle.readBoolean();
            //调用config页面里面的状态更新
            if(ConfigProtocol.configChangeCallback != null)
            {
                ConfigProtocol.configChangeCallback.receiveToggleChange(
                    tempId,newStage
                );
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
