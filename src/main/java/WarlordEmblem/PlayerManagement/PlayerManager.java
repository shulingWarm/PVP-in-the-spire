package WarlordEmblem.PlayerManagement;

import UI.ConfigPageModules.CharacterPanel;
import UI.GridPanel;
import WarlordEmblem.Events.AssignTeamEvent;
import WarlordEmblem.Events.BattleInfoEvent;
import WarlordEmblem.Events.ExecuteAssignTeamEvent;
import WarlordEmblem.PVPApi.Communication;
import WarlordEmblem.network.PlayerInfo;
import WarlordEmblem.network.SelfPlayerInfo;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

import java.util.HashMap;

//玩家信息的管理器
//主要是用于管理多个玩家
public class PlayerManager implements TeamCallback {

    //注意，这个player信息也包括本机的玩家
    public PlayerTeam[] teams = new PlayerTeam[2];

    //有玩家加入时的回调函数
    public PlayerJoinInterface playerJoinInterface = null;

    //所有玩家的tag映射关系
    public HashMap<Integer, PlayerInfo> playerInfoMap;

    //本地玩家的Player信息
    public SelfPlayerInfo selfPlayerInfo;

    //与战斗有关的信息
    public BattleInfo battleInfo;

    //目前总共的统计数量
    //目前进入到战斗房间的数量用的也是这个
    public int readyNum = 0;

    //开始游戏的时间
    public long beginGameTime = 0;

    public PlayerManager()
    {
        playerInfoMap = new HashMap<>();
        //给每个team设置id
        teams[0] = new PlayerTeam(0, Color.RED,true,this);
        teams[1] = new PlayerTeam(1,Color.BLUE,false,this);
        //初始化本地玩家的player信息
        selfPlayerInfo = new SelfPlayerInfo();
        playerInfoMap.put(selfPlayerInfo.playerTag,selfPlayerInfo);
        //战斗相关的信息
        battleInfo = new BattleInfo();
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
            //判断我方角色是否已经分配过所在的team了
            if(selfPlayerInfo.idTeam >= 0)
            {
                //给对方发送我方角色所在房间的信息
                Communication.sendEvent(
                    new ExecuteAssignTeamEvent(selfPlayerInfo.idTeam));
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
        //如果是本地玩家并且还没有分配过显示位，那就把它放到主位
        if(info.isSelfPlayer() && info.idTeam < 0)
        {
            playerJoinInterface.setMainCharacter(info.configPage);
        }
        //如果它已经在这个队伍里了，就不用操作了
        if(info.idTeam != team.idTeam)
        {
            info.idTeam = team.idTeam;
            team.addPlayer(info);
        }
    }

    //给当前的player安排队伍
    //这里仅仅是发送信息，不做实质性的执行
    public void assignTeam(PlayerInfo info)
    {
        //获取人数最少的team
        PlayerTeam minTeam = getMinTeam();
        Communication.sendEvent(new AssignTeamEvent(info.playerTag, minTeam.idTeam));
    }

    @Override
    public void exchangeLayout() {
        //两个team里面的layout
        GridPanel panel1 = teams[0].gridPanel;
        GridPanel panel2 = teams[1].gridPanel;
        GridPanel.exchangePanel(panel1,panel2);
        //然后交换两个panel的指针
        teams[0].gridPanel = panel2;
        teams[1].gridPanel = panel1;
        //重置两个panel的选边
        teams[0].resetSide(!teams[0].isLeft);
        teams[1].resetSide(!teams[1].isLeft);
    }

    public void updateReadyFlag(int playerTag,boolean readyFlag)
    {
        updateReadyFlag(this.playerInfoMap.get(playerTag),readyFlag);
    }

    //获取与player相反的team
    public PlayerTeam getOppositeTeam()
    {
        if(selfPlayerInfo.idTeam == 0)
            return teams[1];
        return teams[0];
    }

    public PlayerTeam getSelfTeam()
    {
        return teams[selfPlayerInfo.idTeam];
    }

    //获取怪物列表
    public MonsterGroup getMonsterGroup()
    {
        //获取我方的team
        PlayerTeam selfTeam = getSelfTeam();
        //同时初始化我方的友军
        this.battleInfo.friendPlayerGroup = selfTeam.getFriendPlayerGroup();
        //获取与player相反的team
        PlayerTeam oppositeTeam = getOppositeTeam();
        //从team里面获取对方的group
        return oppositeTeam.getMonsterGroup();
    }

    //重置所有角色的贴图 把它们的大小重置回正常状态
    public void resetPlayerTexture()
    {
        for(PlayerInfo eachPlayer : playerInfoMap.values())
        {
            eachPlayer.resetPlayerTexture();
        }
    }

    //更新我方player的准备状态
    public void updateReadyFlag(PlayerInfo info,boolean readyFlag)
    {
        //设置准备状态
        info.setReadyFlag(readyFlag);
        if(readyFlag)
            readyNum++;
        else
            readyNum--;
        if(readyNum == playerInfoMap.size() && teams[0].getPlayerNum() > 0
                && teams[1].getPlayerNum() > 0
        )
        {
            //初始化开始游戏的时间
            beginGameTime = System.currentTimeMillis();
            playerJoinInterface.enterGame();
            this.readyNum = 0;
            resetPlayerTexture();
        }
    }

    //把每个玩家的信息load到自己的monster里面
    public void loadInfoToMonster()
    {
        //遍历每个player
        for(PlayerInfo eachPlayer : this.playerInfoMap.values())
        {
            eachPlayer.loadInfoToMonster();
        }
    }

    //判断我方是否为先手
    public boolean isSelfFirstHand()
    {
        return getSelfTeam().enterTime <
                getOppositeTeam().enterTime;
    }

    //更新玩家进入的时间
    public void updateEnterTime(PlayerInfo playerInfo,long enterTime)
    {
        //记录玩家的进入时间，虽然这个可能不是很重要
        playerInfo.enterTime = enterTime;
        //获取对应的team
        PlayerTeam team = teams[playerInfo.idTeam];
        team.updateEnterTime(enterTime);
        ++this.readyNum;
        System.out.printf("ready update: %d\n",this.readyNum);
        //如果进入到战斗房间的总数达标了，就调用进入战斗的流程
        if(this.readyNum == this.playerInfoMap.size())
        {
            loadInfoToMonster();
            this.battleInfo.enterBattle(isSelfFirstHand());
        }
    }
}
