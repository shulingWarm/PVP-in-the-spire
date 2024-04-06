package WarlordEmblem.EffectTransport;

import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import java.io.DataInputStream;
import java.io.DataOutputStream;

//基础的传输特效的操作
public abstract class BaseTransporter extends AbstractTransporter {

    //获取传输器的id,用于在管理器里面注册
    public abstract String getId();

}
