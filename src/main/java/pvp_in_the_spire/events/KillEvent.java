package pvp_in_the_spire.events;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.BaseEvent;
import pvp_in_the_spire.network.PlayerInfo;
import pvp_in_the_spire.patches.ActionNetworkPatches;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//斩杀事件
public class KillEvent extends BaseEvent {

    //通知死亡的目标
    public int playerTag;

    public KillEvent(int playerTag)
    {
        this.eventId = "KillEvent";
        this.playerTag = playerTag;
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        //编码死亡的目标
        try
        {
            streamHandle.writeInt(this.playerTag);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void decode(DataInputStream streamHandle) {
        PlayerInfo info = GlobalManager.playerManager.decodePlayerInfo(streamHandle);
        //如果没有找到有效的Info就直接结束
        if(info == null)
            return;
        //只有当info是本地玩家的时候才处理
        if(!info.isSelfPlayer())
            return;
        //对玩家进行斩杀
        ActionNetworkPatches.instantKill(AbstractDungeon.player);
    }
}
