package pvp_in_the_spire.effect_transport;

import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

//空构造函数
public class EmptyTransporter extends GeneralTransporter {

    //xy的通用构造器的解码
    @Override
    public void encode(DataOutputStream stream, AbstractGameEffect effect) {
        try
        {
            Class<?> clazz = effect.getClass();
            //写入类名
            stream.writeUTF(clazz.getName());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractGameEffect decode(DataInputStream stream) {
        try
        {
            //读取类名
            String className = stream.readUTF();
            //调用构造函数
            Class<?> clazz = Class.forName(className);
            //获得class的构造函数
            Constructor<?> constructor = clazz.getConstructor();
            //返回构造出的结果
            return (AbstractGameEffect) constructor.newInstance();
        }
        catch (IOException |
               ClassNotFoundException |
               NoSuchMethodException |
               IllegalAccessException |
               InstantiationException |
               InvocationTargetException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean judgeFit(Class<?> clazz) {
        Constructor<?> constructor = null;
        //获得构造函数
        try
        {
            constructor = clazz.getConstructor();
        }
        catch (NoSuchMethodException e)
        {
            return false;
        }
        return true;
    }

}
