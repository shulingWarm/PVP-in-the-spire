package WarlordEmblem.PlayerManagement;

import WarlordEmblem.GlobalManager;
import WarlordEmblem.SocketServer;
import WarlordEmblem.network.PlayerInfo;

import java.util.ArrayList;
import java.util.HashSet;

//轮次管理器
public class TurnManager {

    //每个座次位置的玩家
    public ArrayList<HashSet<PlayerInfo>> seatList;

    public TurnManager(int seatNum)
    {
        seatList = new ArrayList<>();
        //初始化每个位置的玩家
        for(int i=0;i<seatNum;++i)
        {
            seatList.add(new HashSet<>());
        }
    }

    //遍历某个范围内的座次数据，判断是否全都一样
    public boolean isIdTurnSame(int idBegin,int idEnd)
    {
        //目前找到的轮次
        int idTurn = -1;
        for(int i=idBegin;i<idEnd;++i)
        {
            //获取当前位置的哈希表
            HashSet<PlayerInfo> seatPlayers = this.seatList.get(i);
            for(PlayerInfo eachPlayer : seatPlayers)
            {
                int tempTurn = eachPlayer.getIdTurn();
                if(tempTurn >= 0)
                {
                    if(idTurn >= 0 && idTurn != tempTurn)
                        return false;
                    else
                        idTurn = tempTurn;
                }
            }
        }
        return true;
    }

    //判断某个位置的前置轮次是大于后置轮次的
    public boolean isPreTurnBigger(int idSeat)
    {
        if(idSeat == 0 || idSeat == (seatList.size() - 1))
        {
            return true;
        }
        int preTurn = seatList.get(idSeat - 1).iterator().next().getIdTurn();
        int postTurn = seatList.get(idSeat + 1).iterator().next().getIdTurn();
        return preTurn > postTurn;
    }

    //判断是否可以开始回合
    public boolean canBeginTurn()
    {
        //获取我方玩家所在的seat
        int idSeat = GlobalManager.playerManager.selfPlayerInfo.getIdSeat();
        if(idSeat > this.seatList.size())
            idSeat = this.seatList.size();
        //遍历所有的前置座位
        return isPreTurnBigger(idSeat) &&
            isIdTurnSame(0,idSeat) &&
            isIdTurnSame(idSeat + 1,this.seatList.size());
    }

    //设置玩家的seat
    public void setPlayerSeat(PlayerInfo info,int idSeat)
    {
        System.out.printf("Set %s at %d !!!\n",info.getName(),idSeat);
        if(idSeat < 0 || idSeat >= this.seatList.size())
        {
            System.out.println("Seat out of range");
            return;
        }
        seatList.get(idSeat).add(info);
        info.setIdSeat(idSeat);
    }

    //首次分配座位的逻辑
    public int firstAssignment(PlayerInfo info)
    {
        //判断第一个座位是否已经有人了
        if(seatList.get(0).isEmpty())
        {
            System.out.println("Assign 0 team by empty\n");
            return 0;
        }
        else {
            //判断它属于哪个队伍
            PlayerInfo existPlayer = seatList.get(0).iterator().next();
            //判断是不是相同的队伍
            if(existPlayer.idTeam == info.idTeam)
            {
                System.out.println("Assign seat 0 by same team\n");
                return 0;
            }
        }
        return 1;
    }

    //第二场战斗之后的分配
    public int secondAssignment(PlayerInfo info)
    {
        boolean teamSame = info.idTeam == GlobalManager.playerManager.selfPlayerInfo.idSeat;
        boolean firstHand = SocketServer.firstHandFlag;
        if(teamSame == firstHand) return 0;
        return 1;
    }

    //给玩家分配座位
    public int assignPlayerSeat(PlayerInfo info)
    {
        if(SocketServer.battleNum > 0)
            return secondAssignment(info);
        return firstAssignment(info);
    }

}
