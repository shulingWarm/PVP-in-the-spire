package pvp_in_the_spire.events;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.BaseEvent;
import pvp_in_the_spire.orbs.MonsterOrb;
import pvp_in_the_spire.orbs.OrbMapping;
import pvp_in_the_spire.patches.ActionNetworkPatches;
import com.megacrit.cardcrawl.orbs.AbstractOrb;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//生成球的操作
public class ChannelOrbEvent extends BaseEvent {

    //准备用于生成的球
    public AbstractOrb orb;

    public ChannelOrbEvent(AbstractOrb orb)
    {
        this.eventId = "ChannelOrbEvent";
        this.orb = orb;
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        //判断是哪种球
        int orbType = ActionNetworkPatches.getOrbType(orb);
        //获取这个球的唯一id 这关键是相同的球的数值要一一对应
        int idOrb = OrbMapping.addPlayerOrb(orb);
        try
        {
            //写入玩家的tag
            streamHandle.writeInt(GlobalManager.myPlayerTag);
            //写入orb的id
            streamHandle.writeInt(idOrb);
            //写入orb的种类
            streamHandle.writeInt(orbType);
            //记录球的激发数值，这是给黑球用的
            streamHandle.writeInt(orb.evokeAmount);
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
            //记录玩家的tag
            int playerTag = streamHandle.readInt();
            //读取orb的id
            int idOrb = streamHandle.readInt();
            //读取orb的种类
            int orbType = streamHandle.readInt();
            //读取球的激发数值
            int evokeAmount = streamHandle.readInt();
            if(idOrb < 0)
                return;
            //根据球的种类生成对应的球
            MonsterOrb tempOrb = OrbMapping.generateOrbByType(orbType);
            if(tempOrb == null)
                return;
            //记录球的激发值
            tempOrb.evokeAmount = evokeAmount;
            //记录敌人的球
            OrbMapping.addMonsterOrb(tempOrb,idOrb);
            //调用玩家的tag,加入这个球
            GlobalManager.playerManager.getPlayerInfo(playerTag).playerMonster.channelOrb(tempOrb);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
