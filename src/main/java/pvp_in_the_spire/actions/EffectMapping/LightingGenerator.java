package pvp_in_the_spire.actions.EffectMapping;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.LightningEffect;

//闪电特效
public class LightingGenerator extends MappingBase {

    public AbstractGameEffect getEffect(AbstractCreature target)
    {
        return new LightningEffect(target.drawX,target.drawY);
    }

}
