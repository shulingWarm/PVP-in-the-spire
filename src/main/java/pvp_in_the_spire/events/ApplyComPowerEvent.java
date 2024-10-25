package pvp_in_the_spire.events;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.BaseEvent;
import pvp_in_the_spire.network.PlayerInfo;
import pvp_in_the_spire.powers.CommunicatePower;
import pvp_in_the_spire.powers.PowerMapping;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//施加通信版本的power
//这个东西相比之前更容易管理一些
public class ApplyComPowerEvent extends BaseEvent {

    //即将发送的power
    public CommunicatePower power;

    public ApplyComPowerEvent(CommunicatePower power)
    {
        this.eventId = "ApplyComPowerEvent";
        this.power = power;
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        //发送自己的数据头，这代表的是管理者
        try
        {
            //这里编码的是power的管理者，这和施加power的过程没关系
            GlobalManager.playerManager.encodePlayer(streamHandle);
            //获取power的map id
            int mapId = PowerMapping.getPowerId(power.getMapId());
            streamHandle.writeInt(mapId);
            //写入通信id
            streamHandle.writeInt(this.power.getCommunicateId());
            //对power内容进行编码
            this.power.encode(streamHandle);
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
            //解码出对应的info
            PlayerInfo info = GlobalManager.playerManager.decodePlayerInfo(streamHandle);
            if(info == null)
                return;
            //读取power的map id
            int mapId = streamHandle.readInt();
            //读取通信id
            int comId = streamHandle.readInt();
            //从power里面获取对应种类的id
            CommunicatePower tempPower = PowerMapping.getComPower(mapId);
            //从里面解码出真正的power
            CommunicatePower realPower = tempPower.decode(streamHandle);
            //如果解码出来的是null就算了
            if(realPower == null)
                return;
            //注册power
            realPower.setCommunicateId(comId);
            info.powerManager.registerPower(realPower);
            //通知施加power
            info.powerManager.applyPower(realPower,realPower.owner,
                realPower.getSourcePlayer(),
                realPower.getPowerAmount(),false);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
