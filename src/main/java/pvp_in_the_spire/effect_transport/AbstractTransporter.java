package pvp_in_the_spire.effect_transport;

import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import java.io.DataInputStream;
import java.io.DataOutputStream;

//最基础的effect传输器，里面只有编码和解码
//至于子类的一些事，由子类去处理
public abstract class AbstractTransporter {

    //传输器在列表里面的id
    public int listId;

    //对传入的特效做编码
    public abstract void encode(DataOutputStream stream, AbstractGameEffect effect);

    //对传入的特效做解码
    public abstract AbstractGameEffect decode(DataInputStream stream);

}
