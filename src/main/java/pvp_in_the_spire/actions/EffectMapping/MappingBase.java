package pvp_in_the_spire.actions.EffectMapping;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

//对各种特效的映射的基类
public abstract class MappingBase {

    //基本的生成effect的函数，但可能有不一样的接口
    public abstract AbstractGameEffect getEffect(AbstractCreature target);

}
