package pvp_in_the_spire.effect_transport;

import pvp_in_the_spire.helpers.LocationHelper;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

//只有xy数据的effect
public class XYTransporter extends GeneralTransporter {

    //xy的通用构造器的解码
    @Override
    public void encode(DataOutputStream stream, AbstractGameEffect effect) {
        try
        {
            Class<?> clazz = effect.getClass();
            Field xField = clazz.getDeclaredField("x");
            Field yField = clazz.getDeclaredField("y");
            //设置为可读
            xField.setAccessible(true);
            yField.setAccessible(true);
            //读取x,y的数据
            float x = xField.getFloat(effect);
            float y = yField.getFloat(effect);
            //写入类名
            stream.writeUTF(clazz.getName());
            //写入x,y,但在这里执行对称
            stream.writeFloat(LocationHelper.xInvert(x));
            stream.writeFloat(LocationHelper.yInvert(y));
        }
        catch (NoSuchFieldException | IOException | IllegalAccessException e)
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
            //读取x,y
            float x = stream.readFloat();
            float y = stream.readFloat();
            //调用构造函数
            Class<?> clazz = Class.forName(className);
            //获得class的构造函数
            Constructor<?> constructor = clazz.getConstructor(float.class,float.class);
            //返回构造出的结果
            return (AbstractGameEffect) constructor.newInstance(x,y);
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
        //x,y的数据
        Field xField = null, yField = null;
        try
        {
            xField = clazz.getDeclaredField("x");
            yField = clazz.getDeclaredField("y");
        }
        catch (NoSuchFieldException e)
        {
            return false;
        }
        Constructor<?> constructor = null;
        //获得构造函数
        try
        {
            constructor = clazz.getConstructor(float.class,float.class);
        }
        catch (NoSuchMethodException e)
        {
            return false;
        }
        return true;
    }
}
