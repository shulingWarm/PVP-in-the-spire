package pvp_in_the_spire.events;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.BaseEvent;
import pvp_in_the_spire.network.PlayerInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.GainPennyEffect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//失去金币的事件
public class LoseGoldEvent extends BaseEvent {

    public int source;
    public int target;
    public int amount;

    //处理钱的来源，目标和数量
    public LoseGoldEvent(int source,int target,int amount)
    {
        this.eventId = "LoseGoldEvent";
        this.source = source;
        this.target = target;
        this.amount = amount;
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        try
        {
            streamHandle.writeInt(this.source);
            streamHandle.writeInt(this.target);
            streamHandle.writeInt(this.amount);
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
            PlayerInfo sourceInfo = GlobalManager.playerManager.decodePlayerInfo(streamHandle);
            PlayerInfo targetInfo = GlobalManager.playerManager.decodePlayerInfo(streamHandle);
            int tempAmount = streamHandle.readInt();
            //如果两个info都不是null，就播放一下动画
            if(sourceInfo != null && targetInfo != null &&
                sourceInfo.getCreature() != null &&
                targetInfo.getCreature() != null)
            {
                AbstractCreature sourceCreature = sourceInfo.getCreature();
                AbstractCreature targetCreature = targetInfo.getCreature();
                for(int i=0;i<tempAmount;++i)
                {
                    AbstractDungeon.effectList.add(new GainPennyEffect(
                        targetCreature,sourceCreature.hb.cX,
                            sourceCreature.hb.cY,
                            targetCreature.hb.cX,
                            targetCreature.hb.cY,false
                    ));
                }
            }
            //如果目标玩家是本机玩家，就执行获得金币
            if(targetInfo!=null && targetInfo.isSelfPlayer())
                AbstractDungeon.player.gainGold(tempAmount);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
