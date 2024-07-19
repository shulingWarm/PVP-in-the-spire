package WarlordEmblem.PlayerManagement;

import WarlordEmblem.Events.EndTurnEvent;
import WarlordEmblem.GlobalManager;
import WarlordEmblem.PVPApi.Communication;
import WarlordEmblem.SocketServer;
import WarlordEmblem.actions.MultiPauseAction;
import WarlordEmblem.actions.PauseAction;
import WarlordEmblem.network.PlayerInfo;
import WarlordEmblem.patches.CharacterSelectScreenPatches;

//与战斗有关的信息
public class BattleInfo {

    //友军信息
    public FriendPlayerGroup friendPlayerGroup;
    //敌方的team
    public PlayerTeam oppositeTeam;
    //记录已经准备好的玩家
    int endTurnPlayerNum = 0;

    //进入战斗的逻辑，主要是处理一些战斗前的等待
    public void enterBattle(boolean isFirstHand)
    {
        //标记先后手
        SocketServer.firstHandFlag = isFirstHand;
        //直接在这里调用旧版的结束等待状态的内容
        CharacterSelectScreenPatches.TestUpdateFading.endWaitStage(true);
    }

    //更新玩家结束回合的信息
    public void updateEndTurn(int playerTag)
    {
        PlayerInfo playerInfo = GlobalManager.playerManager.getPlayerInfo(playerTag);
        //判断对方team中是否有这个玩家
        if(oppositeTeam.playerInfos.contains(playerInfo))
        {
            ++endTurnPlayerNum;
            if(endTurnPlayerNum >= oppositeTeam.getPlayerNum())
            {
                //结束回合阻塞
                MultiPauseAction.pauseStage = false;
                endTurnPlayerNum = 0;
            }
        }
    }

}
