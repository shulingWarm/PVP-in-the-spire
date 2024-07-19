package WarlordEmblem.Events;

import WarlordEmblem.GlobalManager;
import WarlordEmblem.PVPApi.BaseEvent;
import WarlordEmblem.character.PlayerMonster;

import java.io.DataInputStream;
import java.io.DataOutputStream;

//腾跃时跳过回合的操作
public class JumpTurnEvent extends BaseEvent {

    public JumpTurnEvent()
    {
        this.eventId = "JumpTurnEvent";
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        GlobalManager.playerManager.encodePlayer(streamHandle);
    }

    @Override
    public void decode(DataInputStream streamHandle) {
        PlayerMonster monster = GlobalManager.playerManager.decodePlayer(streamHandle);
        if(!monster.hasPower("Barricade"))
        {
            if(monster.hasCaliper)
                monster.loseBlock(15);
            else
                monster.loseBlock();
        }
        monster.applyStartOfTurnPostDrawPowers();
    }
}
