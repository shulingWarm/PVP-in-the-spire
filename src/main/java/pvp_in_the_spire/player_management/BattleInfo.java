package pvp_in_the_spire.player_management;

import pvp_in_the_spire.events.DeadEvent;
import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.Communication;
import pvp_in_the_spire.SocketServer;
import pvp_in_the_spire.actions.MultiPauseAction;
import pvp_in_the_spire.character.PlayerMonster;
import pvp_in_the_spire.helpers.FieldHelper;
import pvp_in_the_spire.network.PlayerInfo;
import pvp_in_the_spire.patches.ActionNetworkPatches;
import pvp_in_the_spire.patches.CharacterSelectScreenPatches;
import pvp_in_the_spire.relics.PVPTail;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

//与战斗有关的信息
public class BattleInfo {

    //友军信息
    public FriendPlayerGroup friendPlayerGroup;
    //敌方的team
    public PlayerTeam oppositeTeam;
    //我方的team
    public PlayerTeam selfTeam;
    //记录自身已经死亡
    public boolean selfDeadFlag = false;
    //本地玩家活着时的贴图
    public Texture aliveImg = null;
    //轮次管理器
    public TurnManager turnManager;

    //重新把player设置成活着的状态
    public void resetPlayerToAlive()
    {
        AbstractDungeon.player.img = this.aliveImg;
        FieldHelper.setPrivateFieldValue(AbstractDungeon.player,"renderCorpse",false);
    }

    //进入战斗的准备
    public void enterBattle(TurnManager turnManager)
    {
        this.turnManager = turnManager;
        //标记先后手
        SocketServer.firstHandFlag = (GlobalManager.playerManager.selfPlayerInfo.getIdSeat() == 0);
        System.out.printf("First hand flag %b\n",SocketServer.firstHandFlag);
        CharacterSelectScreenPatches.TestUpdateFading.endWaitStage(true);
        resetBattleInfo();
        MultiPauseAction.pauseStage = !SocketServer.firstHandFlag;
    }

    //初始化战斗信息
    public void resetBattleInfo()
    {
        selfDeadFlag = false;
    }

    //获取当前的id turn
    public int getIdTurn()
    {
        //目前就简单先用旧的id turn
        return CharacterSelectScreenPatches.EndTurnOnBegin.idTurn;
    }

    //更新玩家结束回合的信息
    public void updateEndTurn(PlayerInfo info)
    {
        if(info.playerMonster == null)
            return;
        //更新回合结束时的状态
        info.playerMonster.endOfTurnTrigger();
        //更新维护玩家信息的操作
        //但这个事只有房主玩家需要去做
        updateTurnInfo(info);
    }

    //更新回合的结束时的操作
    public void updateTurnInfo(PlayerInfo info)
    {
        if(GlobalManager.playerManager.selfPlayerInfo.isLobbyOwner)
            turnManager.updatePlayerTurn(info,SeatManager.TURN_END);
    }

    //战斗胜利的逻辑
    public void battleWin()
    {
        ActionNetworkPatches.disableCombatTrigger = true;
        //判断是不是完全胜利
        if(oppositeTeam.isCompletelyDie())
        {
            GlobalManager.prepareWin = true;
        }
        //判断自己是不是死亡过
        if(selfDeadFlag)
        {
            //回复到1血
            ActionNetworkPatches.HealEventSend.disableSend = true;
            resetPlayerToAlive();
            AbstractDungeon.player.heal(1);
            ActionNetworkPatches.HealEventSend.disableSend = false;
        }
        //记录下一场战斗为后手
        SocketServer.firstHandFlag = false;
        System.out.println("Battle win !!!!!");
    }

    //战斗失败的逻辑
    public void battleLose()
    {
        ActionNetworkPatches.disableCombatTrigger = true;
        //记录下一场战斗为先手
        SocketServer.firstHandFlag = true;
        //执行尾巴回复操作
        PVPTail.triggerFirstTail();
        System.out.println("Battle lose !!!!!");
    }

    //记录死亡信息
    public void updateDeadInfo(PlayerInfo info)
    {
        System.out.printf("%s dead !!!\n",info.getName());
        //如果info是其它玩家，那就强制它死亡一下
        if(info.playerMonster!= null && info.playerMonster.currentHealth > 0)
        {
            ActionNetworkPatches.instantKill(info.playerMonster);
        }
        //判断是不是和本机玩家一个team
        if(info.idTeam == oppositeTeam.idTeam)
        {
            if(oppositeTeam.isAllDead(info))
            {
                battleWin();
                return;
            }
        }
        else if(selfTeam.isAllDead(info)) {
            battleLose();
            return;
        }
        //更新玩家的座次信息
        updatePlayerTurnStage(info,SeatManager.TURN_END);
    }

    //标记为自身已经死亡
    public void recordSelfDead()
    {
        //如果自身目前已经是死亡状态了，就不需要再记录一次了
        if(this.selfDeadFlag)
            return;
        //记录自身已经死亡
        selfDeadFlag = true;
        //先记录玩家生前的形象
        this.aliveImg = AbstractDungeon.player.img;
        //把玩家的贴图改成死亡状态
        AbstractDungeon.player.playDeathAnimation();
        //发送死亡信息
        Communication.sendEvent(new DeadEvent());
        //如果还是在自己的回合，就强制结束一下回合
        if(!MultiPauseAction.pauseStage)
        {
            MultiPauseAction.pauseStage = true;
            //强制回合提前结束
            AbstractDungeon.actionManager.callEndTurnEarlySequence();
        }
        updateDeadInfo(GlobalManager.playerManager.selfPlayerInfo);
    }

    //更新玩家的准备状态
    public void updatePlayerTurnStage(PlayerInfo info,int newStage)
    {
        //只有我方玩家是房主的时候才会走这个分支
        if(GlobalManager.playerManager.selfPlayerInfo.isLobbyOwner)
        {
            //调用turn管理器，去更新里面的状态
            turnManager.updatePlayerTurn(info,newStage);
        }
    }

    //获得随机的敌人
    public PlayerMonster getRandEnemy()
    {
        return oppositeTeam.getRandMonster();
    }

}
