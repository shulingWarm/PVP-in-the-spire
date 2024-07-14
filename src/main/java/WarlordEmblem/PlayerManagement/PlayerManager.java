package WarlordEmblem.PlayerManagement;

import UI.ConfigPageModules.CharacterPanel;
import UI.GridPanel;
import WarlordEmblem.Events.AssignTeamEvent;
import WarlordEmblem.PVPApi.Communication;
import WarlordEmblem.network.PlayerInfo;
import WarlordEmblem.network.SelfPlayerInfo;

import java.util.HashMap;

//玩家信息的管理器
//主要是用于管理多个玩家
public class PlayerManager {

    //注意，这个player信息也包括本机的玩家
    public PlayerTeam[] teams = new PlayerTeam[2];

    //有玩家加入时的回调函数
    public PlayerJoinInterface playerJoinInterface = null;

    //所有玩家的tag映射关系
    public HashMap<Integer, PlayerInfo> playerInfoMap;

    //本地玩家的Player信息
    public SelfPlayerInfo selfPlayerInfo;

    public PlayerManager()
    {
        playerInfoMap = new HashMap<>();
        //给每个team设置id
        for(int idTeam=0;idTeam<teams.length;++idTeam)
        {
            teams[idTeam].idTeam = idTeam;
        }
        //初始化本地玩家的player信息
        selfPlayerInfo = new SelfPlayerInfo();
        playerInfoMap.put(selfPlayerInfo.playerTag,selfPlayerInfo);
    }

    //注册新的玩家
    public void registerPlayer(int playerTag)
    {
        if(!playerInfoMap.containsKey(playerTag))
        {
            PlayerInfo tempInfo = new PlayerInfo(playerTag);
            playerInfoMap.put(playerTag,tempInfo);
            //调用config页面
            if(playerJoinInterface != null)
            {
                playerJoinInterface.registerPlayer(tempInfo);
            }
        }
    }

    //获取人数最少的team
    public PlayerTeam getMinTeam()
    {
        PlayerTeam minTeam = null;
        for(PlayerTeam eachTeam : teams)
        {
            int tempNum = eachTeam.getPlayerNum();
            if(minTeam == null || tempNum < minTeam.getPlayerNum())
            {
                minTeam = eachTeam;
            }
        }
        return minTeam;
    }

    //获取指定的player info
    public PlayerInfo getPlayerInfo(int playerTag)
    {
        return playerInfoMap.get(playerTag);
    }

    //这是真的在执行分配操作
    public void assignTeam(int playerTag,int idTeam)
    {
        PlayerInfo tempInfo = playerInfoMap.get(playerTag);
        assignTeam(tempInfo,teams[idTeam]);
    }

    //初始化显示角色的layout
    //这个东西主要是用于角色显示页面
    public void initCharacterLayout(GridPanel leftPanel,
                GridPanel rightPanel)
    {
        teams[0].setGridPanel(leftPanel);
        teams[1].setGridPanel(rightPanel);
    }


    public void assignTeam(PlayerInfo info,PlayerTeam team){
        info.idTeam = team.idTeam;
        team.addPlayer(info);
    }

    //给当前的player安排队伍
    //这里仅仅是发送信息，不做实质性的执行
    public void assignTeam(PlayerInfo info)
    {
        //获取人数最少的team
        PlayerTeam minTeam = getMinTeam();
        Communication.sendEvent(new AssignTeamEvent(info.playerTag, minTeam.idTeam));
    }


}
