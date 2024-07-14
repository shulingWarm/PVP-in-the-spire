package WarlordEmblem.Events;

import WarlordEmblem.GlobalManager;
import WarlordEmblem.PVPApi.BaseEvent;
import WarlordEmblem.network.PlayerInfo;
import WarlordEmblem.network.SelfPlayerInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//真正去执行分配座位的事件
//只有分配本机玩家的时候才会触发这个事件
public class ExecuteAssignTeamEvent extends BaseEvent {

    //即将加入的目标team
    public int idTeam;

    public ExecuteAssignTeamEvent(int idTeam)
    {
        this.idTeam = idTeam;
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        //记录team的id
        try
        {
            //我方的player
            SelfPlayerInfo playerInfo = GlobalManager.playerManager.selfPlayerInfo;
            //发送自己的tag
            streamHandle.writeInt(playerInfo.playerTag);
            streamHandle.writeInt(idTeam);
            //发送自己的名字
            streamHandle.writeUTF(playerInfo.getName());
            streamHandle.writeUTF(playerInfo.getVersion());
            //发送选择玩家的类型
            streamHandle.writeUTF(playerInfo.getPlayerClass().name());
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
            int tempTag = streamHandle.readInt();
            //读取team的id
            int tempIdTeam = streamHandle.readInt();
            //执行team分配
            GlobalManager.playerManager.assignTeam(tempTag,tempIdTeam);
            //读取名字
            String name = streamHandle.readUTF();
            String tempVersion = streamHandle.readUTF();
            //选择的角色
            String character = streamHandle.readUTF();
            //指定目标player的信息
            GlobalManager.playerManager.getPlayerInfo(tempTag).setCharacterInfo(
                name,tempVersion, AbstractPlayer.PlayerClass.valueOf(character)
            );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
