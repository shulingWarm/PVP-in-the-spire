package pvp_in_the_spire.events;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.BaseEvent;

import java.io.DataInputStream;

//目前第一个不需要做任何编码的事件
public class EnterBattleEvent extends BaseEvent {

    public EnterBattleEvent()
    {
        this.eventId = "EnterBattleEvent";
    }

    @Override
    public void decode(DataInputStream streamHandle) {
        GlobalManager.playerManager.enterBattle();
    }
}
