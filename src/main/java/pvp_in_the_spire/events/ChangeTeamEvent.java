package pvp_in_the_spire.events;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.BaseEvent;
import pvp_in_the_spire.network.PlayerInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//更换队伍的事件
public class ChangeTeamEvent extends BaseEvent {

    //转换的目标队伍
    int targetTeam;

    public ChangeTeamEvent(int targetTeam)
    {
        this.eventId = "ChangeTeamEvent";
        this.targetTeam = targetTeam;
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        //编码自己的player信息
        GlobalManager.playerManager.encodePlayer(streamHandle);
        try
        {
            streamHandle.writeInt(targetTeam);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void decode(DataInputStream streamHandle) {
        //解码对应的player
        PlayerInfo info = GlobalManager.playerManager.decodePlayerInfo(streamHandle);
        if(info == null)
            return;
        try
        {
            int tempTeamId = streamHandle.readInt();
            GlobalManager.playerManager.assignTeam(
                info,GlobalManager.playerManager.teams[tempTeamId]);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
