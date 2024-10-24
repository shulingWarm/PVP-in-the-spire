package pvp_in_the_spire.events;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.BaseEvent;
import pvp_in_the_spire.stance.CalmStanceEnemy;
import pvp_in_the_spire.stance.DivinityStanceEnemy;
import pvp_in_the_spire.stance.WrathStanceEnemy;
import pvp_in_the_spire.character.PlayerMonster;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.stances.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//更改姿态的事件
public class ChangeStanceEvent extends BaseEvent {

    String idStance;

    public ChangeStanceEvent(String idStance)
    {
        this.eventId = "ChangeStanceEvent";
        this.idStance = idStance;
    }

    //从姿态名字中生成姿态
    public static AbstractStance generateStance(String stanceName, AbstractCreature creature)
    {
        //要改变的新的姿态
        AbstractStance newStance;
        //根据姿态的id确定一下要转换成哪个姿态
        switch (stanceName)
        {
            case WrathStance.STANCE_ID:
                newStance = new WrathStanceEnemy(creature);
                break;
            case CalmStance.STANCE_ID:
                newStance = new CalmStanceEnemy(creature);
                break;
            case DivinityStance.STANCE_ID:
                newStance = new DivinityStanceEnemy(creature);
                break;
            default:
                newStance = new NeutralStance();
        }
        return newStance;
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        try
        {
            GlobalManager.playerManager.encodePlayer(streamHandle);
            streamHandle.writeUTF(this.idStance);
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
            PlayerMonster playerMonster = GlobalManager.playerManager.decodePlayer(streamHandle);
            if(playerMonster == null)
                return;
            String tempId = streamHandle.readUTF();
            playerMonster.changeStance(generateStance(tempId,playerMonster));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
