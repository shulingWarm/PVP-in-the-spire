package pvp_in_the_spire.events;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.BaseEvent;
import pvp_in_the_spire.network.PlayerInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;

//腾跃事件 告诉房主下一局还是我的回合
public class VaultEvent extends BaseEvent {

    public VaultEvent()
    {
        this.eventId = "VaultEvent";
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        //编码自己的信息，告诉对面下次还是我的回合
        GlobalManager.playerManager.encodePlayer(streamHandle);
    }

    //解码对应的info
    @Override
    public void decode(DataInputStream streamHandle) {
        //解码出来玩家的目标信息
        PlayerInfo info = GlobalManager.playerManager.decodePlayerInfo(streamHandle);
        //如果信息是无效的，就直接结束了
        if(info == null)
        {
            return;
        }
        //告诉battle信息的管理器，下次这个玩家结束回合的时候，让他重新开始一个回合

    }
}
