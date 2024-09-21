package WarlordEmblem.powers;

import WarlordEmblem.Events.RemovePowerEvent;
import WarlordEmblem.PVPApi.Communication;
import WarlordEmblem.patches.ActionNetworkPatches;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.HashMap;

//这里面放的是power的管理器
//双方做power通信的时候，都需要把power注册进来
public class PowerManager {

    public static PowerManager instance;

    //目前本地正在维护的所有power
    public HashMap<Integer,CommunicatePower> powerMap = new HashMap<>();

    //目前已经分配过的power id
    public static int nextPowerId = 0;

    //在map里面注册power
    public void registerPower(CommunicatePower power)
    {
        //记录power
        powerMap.put(nextPowerId,power);
        //设置power的id
        power.setCommunicateId(nextPowerId);
        ++nextPowerId;
    }

    //移除power
    public void removePower(int idPower,boolean sendFlag)
    {
        //如果没有这个power就直接跳过了
        if(!powerMap.containsKey(idPower))
        {
            return;
        }
        //获取对应的power
        CommunicatePower power = powerMap.get(idPower);
        //执行power的移除
        ActionNetworkPatches.ReducePowerInfoSend.stopTrigger = true;
        AbstractDungeon.actionManager.addToBottom(
            new RemoveSpecificPowerAction(power.owner, power.owner, power));
        ActionNetworkPatches.ReducePowerInfoSend.stopTrigger = false;
        powerMap.remove(idPower);
        if(sendFlag)
        {
            Communication.sendEvent(new RemovePowerEvent(idPower));
        }
    }

    public CommunicatePower getPower(int idPower)
    {
        if(powerMap.containsKey(idPower))
            return powerMap.get(idPower);
        return null;
    }

}
