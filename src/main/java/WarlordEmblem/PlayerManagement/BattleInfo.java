package WarlordEmblem.PlayerManagement;

import WarlordEmblem.actions.MultiPauseAction;
import WarlordEmblem.patches.CharacterSelectScreenPatches;

//与战斗有关的信息
public class BattleInfo {

    //友军信息
    public FriendPlayerGroup friendPlayerGroup;

    //进入战斗的逻辑，主要是处理一些战斗前的等待
    public void enterBattle(boolean isFirstHand)
    {
        //直接在这里调用旧版的结束等待状态的内容
        CharacterSelectScreenPatches.TestUpdateFading.endWaitStage(true);
        //记录跳过下一次阻塞
        MultiPauseAction.jumpNextPause = isFirstHand;
    }

}
