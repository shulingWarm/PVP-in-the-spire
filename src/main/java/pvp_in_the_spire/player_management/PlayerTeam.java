package pvp_in_the_spire.player_management;

import pvp_in_the_spire.ui.GridPanel;
import pvp_in_the_spire.character.PlayerMonster;
import pvp_in_the_spire.network.PlayerInfo;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;
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

    //用于判断是否为地主的标志
    public boolean landlordFlag = false;

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
        playerInfo.idTeam = this.idTeam;
        playerInfos.add(playerInfo);
        //判断这是不是本地玩家
        if(playerInfo.isSelfPlayer())
        {
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

    //移除玩家
    public void removePlayer(PlayerInfo playerInfo)
    {
        //从set中移除
        this.playerInfos.remove(playerInfo);
        //判断是不是玩家
        if(!playerInfo.isSelfPlayer())
        {
            //从角色的panel里面移除它
            this.gridPanel.removePage(playerInfo.configPage);
        }
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

    //判断是不是所有玩家都死了
    public boolean isAllDead(PlayerInfo ignoreInfo)
    {
        for(PlayerInfo eachPlayer : playerInfos)
        {
            if(eachPlayer != ignoreInfo && (!eachPlayer.isDead()))
            {
                return false;
            }
        }
        return true;
    }

    //判断是否都结束回合了
    public boolean isAllEndTurn()
    {
        for(PlayerInfo eachPlayer : playerInfos)
        {
            if(!eachPlayer.isEndTurn())
            {
                System.out.printf("%d not end turn\n",eachPlayer.playerTag);
                return false;
            }
        }
        System.out.println("All end turn!");
        return true;
    }

    //把所有的角色标记为结束回合
    public void setAllEndTurn()
    {
        for(PlayerInfo info : playerInfos)
        {
            info.playerMonster.endTurnFlag = true;
        }
    }

    //判断是不是完全死了 这里指的是没有尾巴了
    public boolean isCompletelyDie()
    {
        for(PlayerInfo eachInfo : playerInfos)
        {
            if(eachInfo.tailNum > 0)
                return false;
        }
        return true;
    }

    //获取随机的monster
    public PlayerMonster getRandMonster()
    {
        ArrayList<PlayerMonster> monsterList = new ArrayList<>();
        for(PlayerInfo eachInfo : playerInfos)
        {
            if(eachInfo.playerMonster != null &&
                !eachInfo.playerMonster.isDead)
            {
                monsterList.add(eachInfo.playerMonster);
            }
        }
        if(monsterList.isEmpty())
            return null;
        return monsterList.get(MathUtils.random(0,monsterList.size()-1));
    }

    //移除所有玩家
    public void removeAllPlayer()
    {
        this.playerInfos.clear();
        //移除显示界面里面的玩家
        this.gridPanel.removeAllPages();
    }

    //判断是否为地主
    public boolean isLandlord()
    {
        return landlordFlag;
    }

}
