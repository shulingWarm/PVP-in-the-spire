package WarlordEmblem.PlayerManagement;

import UI.GridPanel;
import WarlordEmblem.Events.ExecuteAssignTeamEvent;
import WarlordEmblem.PVPApi.Communication;
import WarlordEmblem.network.PlayerInfo;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

import java.util.HashSet;

//玩家队伍，当进入游戏房间时会往里面加入玩家
public class PlayerTeam {

    public HashSet<PlayerInfo> playerInfos;

    //这东西是用来管理每个角色的渲染位置的
    public GridPanel gridPanel = null;

    //当前颜色的边框颜色
    public Color teamColor;

    //是否为config中的左边阵营
    public boolean isLeft = true;

    //team的id
    public int idTeam = 0;

    public TeamCallback teamCallback;

    //这个team最早的进入时间
    public long enterTime = -1;

    public PlayerTeam(int idTeam,Color color,boolean isLeft,
                      TeamCallback teamCallback
      )
    {
        this.idTeam = idTeam;
        this.teamColor = color;
        playerInfos = new HashSet<>();
        this.isLeft = isLeft;
        this.teamCallback = teamCallback;
    }

    //获取队伍中的玩家数
    public int getPlayerNum()
    {
        return playerInfos.size();
    }

    //重置team所在的边
    public void resetSide(boolean isLeft)
    {
        this.isLeft = isLeft;
        for(PlayerInfo eachInfo : playerInfos)
        {
            eachInfo.configPage.setHorizontalFlip(!this.isLeft);
        }
    }

    //根据当前的玩家内容获取怪物列表
    public MonsterGroupManager getMonsterGroup()
    {
        AbstractMonster[] monsterList = new AbstractMonster[this.getPlayerNum()];
        int idMonster = 0;
        //遍历每个player
        for(PlayerInfo eachPlayer : playerInfos)
        {
            monsterList[idMonster] = eachPlayer.generateMonster(idMonster);
            ++idMonster;
        }
        return new MonsterGroupManager(monsterList);
    }

    //初始化友军monster group
    public FriendPlayerGroup getFriendPlayerGroup()
    {
        FriendPlayerGroup friendPlayerGroup = new FriendPlayerGroup();
        //遍历所有的玩家
        for(PlayerInfo eachInfo : this.playerInfos)
        {
            if(!eachInfo.isSelfPlayer())
                friendPlayerGroup.addPlayer(eachInfo.getFriendMonster());
        }
        return friendPlayerGroup;
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
            //如果自己不是左边队伍，就交换两个grid
            if(!isLeft)
                teamCallback.exchangeLayout();
        }
        else {
            //设置角色是否需要flip
            playerInfo.configPage.setHorizontalFlip(!this.isLeft);
            gridPanel.addPage(playerInfo.configPage);
        }
        //把颜色设置成本地颜色
        playerInfo.configPage.setBoxColor(this.teamColor);
    }

    public void setGridPanel(GridPanel gridPanel) {
        this.gridPanel = gridPanel;
    }

    //更新这个team进入游戏的时间
    public void updateEnterTime(long enterTime)
    {
        if(this.enterTime < 0 || this.enterTime > enterTime)
            this.enterTime = enterTime;
    }
}
