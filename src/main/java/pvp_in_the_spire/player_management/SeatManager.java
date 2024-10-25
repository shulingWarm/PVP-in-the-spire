package pvp_in_the_spire.player_management;

import pvp_in_the_spire.events.BeginTurnEvent;
import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.Communication;
import pvp_in_the_spire.network.PlayerInfo;

import java.util.HashMap;

//玩家的座位管理器
//其实就是管理一下当前玩家的状态
public class SeatManager {

    public static final int WAIT_BEGIN = 0; //等待回合开始的状态
    public static final int INSTRUCT_BEGIN = 1; //命令它回合开始，但目前还没开始
    public static final int ACTUAL_BEGIN = 2; //目前正在进行回合
    public static final int TURN_END = 3; //回合已经结束

    //座位中每个玩家以及对应的状态
    public HashMap<PlayerInfo,Integer> infoStageMap = new HashMap<>();

    //添加新的玩家
    public void registerPlayer(PlayerInfo info)
    {
        infoStageMap.put(info,WAIT_BEGIN);
    }

    //判断是不是空座位
    public boolean isEmpty()
    {
        return infoStageMap.isEmpty();
    }

    //获取座位里面第一个玩家
    public PlayerInfo getFirstPlayer()
    {
        for(PlayerInfo eachInfo : infoStageMap.keySet())
            return eachInfo;
        return null;
    }

    //更新info的状态
    public void setInfoStage(PlayerInfo info,int stage)
    {
        this.infoStageMap.put(info,stage);
    }

    //检查当前座位中的玩家是不是都已经出完牌了
    public boolean isAllPlayerEndTurn()
    {
        //遍历每个玩家
        for(int eachStage : infoStageMap.values())
        {
            if(eachStage != TURN_END)
                return false;
        }
        return true;
    }

    //判断这个座位里面是否有存活的玩家
    public boolean isPlayerAlive()
    {
        for(PlayerInfo eachPlayer : infoStageMap.keySet())
        {
            //判断玩家是否存活
            if(!eachPlayer.isDead())
                return true;
        }
        return false;
    }

    //调用玩家逻辑的启动过程
    public void launchSeatStart()
    {
        //循环启动每个玩家的状态
        for(PlayerInfo eachInfo : infoStageMap.keySet())
        {
            //如果玩家已经死了，就直接跳过它
            if(eachInfo.isDead())
                continue;
            //查看当前玩家是否为本地玩家
            if(eachInfo.isSelfPlayer())
            {
                //调用本地的启动流程
                GlobalManager.playerManager.startSelfPlayerTurn();
            }
            else {
                //通知玩家启动回合
                Communication.advanceSendEvent(new BeginTurnEvent(eachInfo.playerTag),
                        eachInfo.playerTag);
            }
        }
    }

}
