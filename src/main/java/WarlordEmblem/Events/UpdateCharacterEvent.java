package WarlordEmblem.Events;

import WarlordEmblem.GlobalManager;
import WarlordEmblem.PVPApi.BaseEvent;
import WarlordEmblem.character.CharacterInfo;
import WarlordEmblem.network.PlayerInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//更新角色的事件
public class UpdateCharacterEvent extends BaseEvent {

    AbstractPlayer.PlayerClass playerClass;

    public UpdateCharacterEvent(AbstractPlayer.PlayerClass playerClass)
    {
        this.eventId = "UpdateCharacterEvent";
        this.playerClass = playerClass;
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        //编码自己的tag
        GlobalManager.playerManager.encodePlayer(streamHandle);
        try
        {
            streamHandle.writeUTF(playerClass.name());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void decode(DataInputStream streamHandle) {
        PlayerInfo info = GlobalManager.playerManager.decodePlayerInfo(streamHandle);
        try
        {
            String className = streamHandle.readUTF();
            AbstractPlayer.PlayerClass tempClass = AbstractPlayer.PlayerClass.valueOf(className);
            info.updateCharacter(tempClass);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
