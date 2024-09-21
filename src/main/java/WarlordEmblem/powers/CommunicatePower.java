package WarlordEmblem.powers;

import WarlordEmblem.Events.SetPowerAmountEvent;
import WarlordEmblem.PVPApi.Communication;
import com.megacrit.cardcrawl.powers.AbstractPower;

//带通信功能的power
//最初是为了给老头表设置的一个父类
public class CommunicatePower extends AbstractPower {

    //这是power在多端通信时用到的唯一标识符
    public int idPower;

    //设置power的通信id
    public void setCommunicateId(int idPower)
    {
        this.idPower = idPower;
    }

    public int getCommunicateId()
    {
        return this.idPower;
    }

    //设置power的amount
    public void setAmount(int newAmount,boolean sendFlag)
    {
        this.amount = newAmount;
        updateDescription();
        //判断是否需要发送消息
        if(sendFlag)
        {
            Communication.sendEvent(new SetPowerAmountEvent(
                this.idPower, this.amount
            ));
        }
    }
}
