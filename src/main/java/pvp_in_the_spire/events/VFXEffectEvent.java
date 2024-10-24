package pvp_in_the_spire.events;

import pvp_in_the_spire.effect_transport.AbstractTransporter;
import pvp_in_the_spire.effect_transport.EffectManager;
import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.BaseEvent;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//视觉特效的传输事件
public class VFXEffectEvent extends BaseEvent {

    //需要被传输的effect
    public AbstractGameEffect effect;
    public AbstractTransporter transporter;
    //是否为general的effect
    public boolean generalFlag;

    public VFXEffectEvent(AbstractGameEffect effect,
          AbstractTransporter transporter,
          boolean generalFlag)
    {
        //记录事件id
        this.eventId = "VFXEffectEvent";
        this.effect = effect;
        this.transporter = transporter;
        this.generalFlag = generalFlag;
    }

    //空构造函数，这是给注册表用的
    public VFXEffectEvent()
    {
        this(null,null,false);
    }

    //编码操作
    @Override
    public void encode(DataOutputStream streamHandle) {
        //写入传输器的id
        try
        {
            streamHandle.writeInt(this.transporter.listId);
            //记录它是不是通用的转换器
            streamHandle.writeBoolean(this.generalFlag);
            transporter.encode(streamHandle,this.effect);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //解码操作
    @Override
    public void decode(DataInputStream streamHandle) {
        try
        {
            //读取该用哪个传输器
            int idTransporter = streamHandle.readInt();
            //读取是不是generalFlag
            boolean tempGeneralFlag = streamHandle.readBoolean();
            //特效管理器
            EffectManager manager = GlobalManager.effectManager;
            //队列的size
            int listSize = tempGeneralFlag ? manager.generalList.size() :
                    manager.transporterList.size();
            //判断是否有这个转换器
            if(listSize <= idTransporter)
            {
                System.out.println("Invalid id Transporter");
                return;
            }
            //effect的解码器
            AbstractTransporter tempTransporter = tempGeneralFlag ?
                manager.generalList.get(idTransporter) :
                manager.transporterList.get(idTransporter);
            AbstractGameEffect effectResult = tempTransporter.decode(streamHandle);
            //把effect添加到效果队列中
            if(effectResult != null)
                AbstractDungeon.effectList.add(effectResult);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
