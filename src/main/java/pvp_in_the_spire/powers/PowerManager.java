package pvp_in_the_spire.powers;

import pvp_in_the_spire.events.ApplyComPowerEvent;
import pvp_in_the_spire.events.RemovePowerEvent;
import pvp_in_the_spire.pvp_api.Communication;
import pvp_in_the_spire.patches.ActionNetworkPatches;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.HashMap;

//这里面放的是power的管理器
//双方做power通信的时候，都需要把power注册进来
public class PowerManager {

    //目前本地正在维护的所有power
    public HashMap<Integer,CommunicatePower> powerMap = new HashMap<>();

    //目前已经分配过的power id
    public int nextPowerId = 0;

    //判断自己是属于哪个玩家的tag
    public int playerTag;

    public PowerManager(int playerTag)
    {
        this.playerTag = playerTag;
    }


    //在map里面注册power
    public void registerPower(CommunicatePower power)
    {
        //判断是否本来就有power id
        //本来就有id的情况一般是power管理者发过来的
        if(power.getCommunicateId() == -1)
        {
            power.setCommunicateId(nextPowerId);
            ++nextPowerId;
        }
        //记录power
        powerMap.put(power.getCommunicateId(),power);
        power.powerManager = this;
    }

    //对特定目标应用power
    public void applyPower(CommunicatePower power,
       AbstractCreature target,
       AbstractCreature source,
       int amount,
       boolean sendFlag
    )
    {
        //一般是由本机发起的power注册才需要用这种形式
        if(power.getCommunicateId() == -1)
            registerPower(power);
        //禁用action里面那种发送power的方法
        ActionNetworkPatches.BuffInfoSend.stopTrigger = true;
        AbstractDungeon.actionManager.addToBottom(
            new ApplyPowerAction(target,source,power,amount)
        );
        ActionNetworkPatches.BuffInfoSend.stopTrigger = false;
        //根据情况判断是否需要广播这个事件
        if(sendFlag)
        {
            //处理发送power的事件
            Communication.sendEvent(new ApplyComPowerEvent(power));
        }
    }

    //移除power
    public void removePower(int idPower,boolean sendFlag)
    {
        //如果没有这个power就直接跳过了
        if(!powerMap.containsKey(idPower))
        {
            System.out.println("Cannot find id power\n");
            return;
        }
        //获取对应的power
        CommunicatePower power = powerMap.get(idPower);
        //执行power的移除
        ActionNetworkPatches.RemovePowerInfoSend.stopTrigger = true;
        AbstractDungeon.actionManager.addToBottom(
            new RemoveSpecificPowerAction(power.owner, power.owner, power));
        ActionNetworkPatches.RemovePowerInfoSend.stopTrigger = false;
        powerMap.remove(idPower);
        if(sendFlag)
        {
            Communication.sendEvent(new RemovePowerEvent(this.playerTag,idPower));
        }
    }

    public CommunicatePower getPower(int idPower)
    {
        if(powerMap.containsKey(idPower))
            return powerMap.get(idPower);
        return null;
    }

}
