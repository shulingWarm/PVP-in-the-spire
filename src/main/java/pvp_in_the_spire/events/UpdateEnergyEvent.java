package pvp_in_the_spire.events;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.BaseEvent;
import pvp_in_the_spire.character.PlayerMonster;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//更新玩家当前能量的事件
public class UpdateEnergyEvent extends BaseEvent {

    public int currEnergy;

    public UpdateEnergyEvent(int currEnergy)
    {
        this.currEnergy = currEnergy;
        this.eventId = "UpdateEnergyEvent";
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        //编码自己的信息
        GlobalManager.playerManager.encodePlayer(streamHandle);
        //发送自己当前的能量
        try
        {
            streamHandle.writeInt(currEnergy);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void decode(DataInputStream streamHandle) {
        //解码对应的monster
        PlayerMonster monster = GlobalManager.playerManager.decodePlayer(streamHandle);
        if(monster == null)
            return;
        try
        {
            int tempEnergy = streamHandle.readInt();
            monster.setCurrEnergy(tempEnergy);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
