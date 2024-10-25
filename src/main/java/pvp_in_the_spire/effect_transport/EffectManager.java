package pvp_in_the_spire.effect_transport;

import pvp_in_the_spire.events.VFXEffectEvent;
import pvp_in_the_spire.pvp_api.Communication;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import java.util.ArrayList;
import java.util.HashMap;

//与特效传输相关的管理器
public class EffectManager {

    //特效传输工具的列表
    public ArrayList<BaseTransporter> transporterList = new ArrayList<>();
    //从power的名字到id的映射
    public HashMap<String,Integer> name2IdMap = new HashMap<>();
    //通用的传输工具的列表
    public ArrayList<GeneralTransporter> generalList = new ArrayList<>();

    //注册新的传输器
    public void registerNewTransporter(BaseTransporter transporter)
    {
        //判断是否已有有了这个effect
        if(name2IdMap.containsKey(transporter.getId()))
            return;
        //记录它被存储的id
        name2IdMap.put(transporter.getId(),transporterList.size());
        //设置它的列表id
        transporter.listId = transporterList.size();
        //添加到传输工具列表中
        transporterList.add(transporter);
    }

    //注册通用的传输器
    public void registerNewTransporter(GeneralTransporter transporter)
    {
        //添加通用的列表
        transporter.listId = this.generalList.size();
        this.generalList.add(transporter);
    }

    //传输某个特效，会先判断有没有对应的特效传输，如果有的话就传出去
    public void sendEffect(AbstractGameEffect effect)
    {
        String className = effect.getClass().getName();
        //判断有没有这个特效
        if(name2IdMap.containsKey(className))
        {
            int idTransporter = name2IdMap.get(className);
            //发送特效
            Communication.sendEvent(
                new VFXEffectEvent(effect,
                    transporterList.get(idTransporter),false)
            );
        }
        else {
            Class<?> clazz = effect.getClass();
            //遍历每种通用转换器，看有没有符合情况的
            for(GeneralTransporter eachTransporter : generalList)
            {
                if(eachTransporter.judgeFit(clazz))
                {
                    //发送通用特效
                    Communication.sendEvent(
                        new VFXEffectEvent(effect,
                        eachTransporter,true)
                    );
                    break;
                }
            }
        }
    }

}
