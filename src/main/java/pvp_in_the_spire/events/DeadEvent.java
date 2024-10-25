package pvp_in_the_spire.events;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.BaseEvent;
import pvp_in_the_spire.network.PlayerInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;

//玩家死亡的信息
public class DeadEvent extends BaseEvent {

    public DeadEvent()
    {
        this.eventId = "DeadEvent";
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        GlobalManager.playerManager.encodePlayer(streamHandle);
    }

    @Override
    public void decode(DataInputStream streamHandle) {
        PlayerInfo info = GlobalManager.playerManager.decodePlayerInfo(streamHandle);
        if(info == null)
            return;
        //在battle信息里面记录角色死亡
        GlobalManager.getBattleInfo().updateDeadInfo(info);
    }
}
