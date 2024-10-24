package pvp_in_the_spire.powers;

import pvp_in_the_spire.events.SetPowerAmountEvent;
import pvp_in_the_spire.pvp_api.Communication;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.io.DataInputStream;
import java.io.DataOutputStream;

//带通信功能的power
//最初是为了给老头表设置的一个父类
public abstract class CommunicatePower extends AbstractPower {

    //这是power在多端通信时用到的唯一标识符
    private int idPower = -1;

    //power的管理者
    public PowerManager powerManager = null;

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
                this.powerManager.playerTag,this.idPower, this.amount
            ));
        }
    }

    //获取用于映射的map id
    public String getMapId()
    {
        return this.ID;
    }

    //获取apply power时的amount
    public int getPowerAmount()
    {
        return this.amount;
    }

    //获取power的source
    //一般来说这里返回owner就行
    public AbstractCreature getSourcePlayer()
    {
        return this.owner;
    }

    //对power进行编码
    public abstract void encode(DataOutputStream stream);

    //对power做解码操作
    public abstract CommunicatePower decode(DataInputStream stream);
}
