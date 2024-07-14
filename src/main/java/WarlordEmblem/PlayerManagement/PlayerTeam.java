package WarlordEmblem.PlayerManagement;

import UI.GridPanel;
import WarlordEmblem.Events.ExecuteAssignTeamEvent;
import WarlordEmblem.PVPApi.Communication;
import WarlordEmblem.network.PlayerInfo;

import java.util.HashSet;

//玩家队伍，当进入游戏房间时会往里面加入玩家
public class PlayerTeam {

    public HashSet<PlayerInfo> playerInfos;

    //这东西是用来管理每个角色的渲染位置的
    public GridPanel gridPanel = null;

    //team的id
    public int idTeam = 0;

    public PlayerTeam()
    {
        playerInfos = new HashSet<>();
    }

    //获取队伍中的玩家数
    public int getPlayerNum()
    {
        return playerInfos.size();
    }

    //添加玩家
    public void addPlayer(PlayerInfo playerInfo)
    {
        playerInfos.add(playerInfo);
        //判断这是不是本地玩家
        if(playerInfo.isSelfPlayer())
        {
            //发送加入房间的消息
            Communication.sendEvent(new ExecuteAssignTeamEvent(this.idTeam));
        }
        gridPanel.addPage(playerInfo.configPage);
    }

    public void setGridPanel(GridPanel gridPanel) {
        this.gridPanel = gridPanel;
    }
}
