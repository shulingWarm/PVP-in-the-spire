package WarlordEmblem.EffectTransport;

import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import java.io.DataInputStream;
import java.io.DataOutputStream;

//通用的特效转换器
public abstract class GeneralTransporter extends AbstractTransporter {

    public abstract boolean judgeFit(Class<?> clazz);
}
