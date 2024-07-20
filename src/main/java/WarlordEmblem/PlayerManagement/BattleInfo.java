package WarlordEmblem.PlayerManagement;

import WarlordEmblem.Events.DeadEvent;
import WarlordEmblem.Events.EndTurnEvent;
import WarlordEmblem.GlobalManager;
import WarlordEmblem.PVPApi.Communication;
import WarlordEmblem.SocketServer;
import WarlordEmblem.actions.MultiPauseAction;
import WarlordEmblem.actions.PauseAction;
import WarlordEmblem.character.PlayerMonster;
import WarlordEmblem.helpers.FieldHelper;
import WarlordEmblem.network.PlayerInfo;
import WarlordEmblem.network.SelfPlayerInfo;
import WarlordEmblem.patches.ActionNetworkPatches;
import WarlordEmblem.patches.CharacterSelectScreenPatches;
import WarlordEmblem.relics.PVPTail;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.logging.FileHandler;

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

    //重新把player设置成活着的状态
    public void resetPlayerToAlive()
    {
        AbstractDungeon.player.img = this.aliveImg;
        FieldHelper.setPrivateFieldValue(AbstractDungeon.player,"renderCorpse",false);
    }

    //进入战斗的逻辑，主要是处理一些战斗前的等待
    public void enterBattle(boolean isFirstHand)
    {
        //标记先后手
        SocketServer.firstHandFlag = isFirstHand;
        //直接在这里调用旧版的结束等待状态的内容
        CharacterSelectScreenPatches.TestUpdateFading.endWaitStage(true);
        resetBattleInfo();
        MultiPauseAction.pauseStage = !isFirstHand;
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
    public void updateEndTurn(PlayerMonster monster)
    {
        //更新回合结束时的状态
        monster.endOfTurnTrigger();
        if(!monster.friendFlag && !selfDeadFlag && oppositeTeam.isAllEndTurn())
        {
            MultiPauseAction.pauseStage = false;
        }
    }

    //战斗胜利的逻辑
    public void battleWin()
    {
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
    }

    //战斗失败的逻辑
    public void battleLose()
    {
        //记录下一场战斗为先手
        SocketServer.firstHandFlag = true;
        //执行尾巴回复操作
        PVPTail.triggerFirstTail();
    }

    //记录死亡信息
    public void updateDeadInfo(PlayerInfo info)
    {
        //判断是不是和本机玩家一个team
        if(info.idTeam == oppositeTeam.idTeam)
        {
            if(oppositeTeam.isAllDead())
            {
                battleWin();
            }
            else {
                //检查
                updateEndTurn(info.playerMonster);
            }
        }
        else if(selfTeam.isAllDead()) {
            battleLose();
        }
    }

    //标记为自身已经死亡
    public void recordSelfDead()
    {
        //记录自身已经死亡
        selfDeadFlag = true;
        //先记录玩家生前的形象
        this.aliveImg = AbstractDungeon.player.img;
        //把玩家的贴图改成死亡状态
        AbstractDungeon.player.playDeathAnimation();
        //发送死亡信息
        Communication.sendEvent(new DeadEvent());
        updateDeadInfo(GlobalManager.playerManager.selfPlayerInfo);
    }

}
