package pvp_in_the_spire.actions.EffectMapping;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.LightningEffect;

import java.util.HashMap;

//对各种特效的映射
public abstract class EffectMapping {

    //各种map的哈希表
    public static HashMap<Integer,MappingBase> mapper;

    //各种效果的表格
    public static final int LIGHTNING_EFFECT = 1;

    //这是另一个特殊的操作，用于把特效反编码，找到它具体的实例
    public static int getEffectId(AbstractGameEffect effect)
    {
        //就目前的形势来看，它只能一个一个去试
        if(effect instanceof LightningEffect)
            return LIGHTNING_EFFECT;
        return -1;
    }

    //对mapper的初始化的函数
    public static void initMapper()
    {
        //如果mapper初始化过就不用了
        if(mapper!= null)
            return;
        mapper = new HashMap<Integer,MappingBase>();
        mapper.put(LIGHTNING_EFFECT,new LightingGenerator());
    }

    //通过映射表对各种信息做解码
    public static AbstractGameEffect effectIdDecode(int effectId,
        AbstractCreature target)
    {
        //初始化映射表
        initMapper();
        //判断映射表里面有没有这个代码
        if(mapper.containsKey(effectId))
        {
            return mapper.get(effectId).getEffect(target);
        }
        return null;
    }
}
