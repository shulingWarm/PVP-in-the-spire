package pvp_in_the_spire.events;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.BaseEvent;
import pvp_in_the_spire.character.PlayerMonster;

import java.io.DataInputStream;
import java.io.DataOutputStream;

//调用end of round时的事件
//这样才能让对应的monster指定使用end of round
public class EndOfRoundEvent extends BaseEvent {

    public EndOfRoundEvent()
    {
        this.eventId = "EndOfRoundEvent";

    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        //发送自己的tag
        GlobalManager.playerManager.encodePlayer(streamHandle);
    }

    @Override
    public void decode(DataInputStream streamHandle) {
        PlayerMonster monster = GlobalManager.playerManager.decodePlayer(streamHandle);
        if(monster != null)
            monster.endOfRoundTrigger();
    }
}
