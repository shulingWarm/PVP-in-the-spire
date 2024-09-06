package WarlordEmblem.PlayerManagement;

import WarlordEmblem.GlobalManager;
import WarlordEmblem.SocketServer;
import WarlordEmblem.network.PlayerInfo;
import WarlordEmblem.patches.steamConnect.SteamManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

//轮次管理器
public class TurnManager {

    //每个座次位置的玩家
    public ArrayList<SeatManager> seatList;

    public TurnManager(int seatNum)
    {
        seatList = new ArrayList<>();
        //初始化每个位置的玩家
        for(int i=0;i<seatNum;++i)
        {
            seatList.add(new SeatManager());
        }
    }

    //切换到下一下座位的玩家
    public void startNextTurn(int idSeat)
    {
        //循环判断接下来可以开始回合的玩家
        for(int i=0;i<seatList.size();++i)
        {
            //计算下一个座位
            idSeat = (idSeat + 1) % seatList.size();
            //调用座位的启动逻辑
            if(this.seatList.get(idSeat).isPlayerAlive())
            {
                this.seatList.get(idSeat).launchSeatStart();
                break;
            }
        }
    }

    //更新玩家的回合内状态
    public void updatePlayerTurn(PlayerInfo info,int stageTag)
    {
        int idSeat = info.getIdSeat();
        this.seatList.get(idSeat).setInfoStage(info,stageTag);
        //如果是结束回合的操作，就另外再判断一下是不是当前回合已经完全回合结束了
        if(stageTag == SeatManager.TURN_END)
        {
            //检查当前的seat是不是已经完全结束了
            if(this.seatList.get(idSeat).isAllPlayerEndTurn())
            {
                //启动下一个回合开始
                startNextTurn(idSeat);
            }
        }
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
        seatList.get(idSeat).registerPlayer(info);
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
            PlayerInfo existPlayer = seatList.get(0).getFirstPlayer();
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
        boolean teamSame = info.idTeam == GlobalManager.playerManager.selfPlayerInfo.idTeam;
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
