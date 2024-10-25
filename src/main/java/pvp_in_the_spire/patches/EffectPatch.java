package pvp_in_the_spire.patches;

import pvp_in_the_spire.GlobalManager;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

//与特效传输相关的patch
public class EffectPatch {

    public static boolean enable = true;

    @SpirePatch(clz = VFXAction.class,method = SpirePatch.CONSTRUCTOR,
        paramtypez = {AbstractCreature.class, AbstractGameEffect.class, float.class})
    public static class EffectSendPatch
    {

        @SpirePostfixPatch
        public static void fix(VFXAction __instance,AbstractCreature source, AbstractGameEffect effect, float duration)
        {
            //试图去发送特效，能发就发，它这里面会自己去试
            if(enable)
                GlobalManager.effectManager.sendEffect(effect);
        }
    }

}
