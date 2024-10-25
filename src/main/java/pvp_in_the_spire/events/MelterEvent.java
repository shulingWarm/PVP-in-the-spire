package pvp_in_the_spire.events;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.BaseEvent;
import pvp_in_the_spire.character.PlayerMonster;
import pvp_in_the_spire.network.PlayerInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//移除对方所有格挡的事件
public class MelterEvent extends BaseEvent {

    public PlayerMonster target;

    public MelterEvent(PlayerMonster target)
    {
        this.eventId = "MelterEvent";
        this.target = target;
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        try
        {
            streamHandle.writeInt(target.playerTag);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void decode(DataInputStream streamHandle) {
        PlayerInfo info = GlobalManager.playerManager.decodePlayerInfo(streamHandle);
        if(info == null)
            return;
        if(info.isSelfPlayer())
        {
            //判断是否存在玩家
            if(AbstractDungeon.player != null)
            {
                AbstractDungeon.player.loseBlock();
            }
        }
        else if(info.playerMonster != null) {
            //令目标玩家强制失去生命
            info.playerMonster.forceLoseBlock(false);
        }
    }
}
