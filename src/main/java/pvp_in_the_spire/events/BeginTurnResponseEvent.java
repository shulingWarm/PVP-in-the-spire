package pvp_in_the_spire.events;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.BaseEvent;
import pvp_in_the_spire.player_management.SeatManager;
import pvp_in_the_spire.network.PlayerInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;

//开始回合的应答事件
public class BeginTurnResponseEvent extends BaseEvent {

    public BeginTurnResponseEvent()
    {
        this.eventId = "BeginTurnResponseEvent";
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        GlobalManager.playerManager.encodePlayer(streamHandle);
    }

    //解码开始回合的事件
    @Override
    public void decode(DataInputStream streamHandle) {
        PlayerInfo info = GlobalManager.playerManager.decodePlayerInfo(streamHandle);
        if(info == null)
            return;
        //在battle info里面更新玩家的状态
        GlobalManager.playerManager.battleInfo.updatePlayerTurnStage(info,
                SeatManager.ACTUAL_BEGIN);
    }
}
