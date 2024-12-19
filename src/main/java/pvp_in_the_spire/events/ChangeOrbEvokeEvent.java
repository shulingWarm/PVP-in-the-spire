package pvp_in_the_spire.events;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import pvp_in_the_spire.orbs.OrbMapping;
import pvp_in_the_spire.pvp_api.BaseEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//更改充能球的触发事件
public class ChangeOrbEvokeEvent extends BaseEvent {

    public int idOrb;
    public int evokeAmount;

    public ChangeOrbEvokeEvent(int idOrb, int evokeAmount)
    {
        this.evokeAmount = evokeAmount;
        this.idOrb = idOrb;
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        try
        {
            streamHandle.writeInt(this.idOrb);
            streamHandle.writeInt(this.evokeAmount);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void decode(DataInputStream streamHandle) {
        try
        {
            int tempIdOrb = streamHandle.readInt();
            int tempEvokeAmount = streamHandle.readInt();
            //寻找对应的orb实体
            AbstractOrb tempOrb = OrbMapping.getMonsterOrb(tempIdOrb);
            if(tempOrb == null)
                return;
            tempOrb.evokeAmount = tempEvokeAmount;
            tempOrb.updateDescription();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
