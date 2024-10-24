package pvp_in_the_spire.generator.Generators;

import pvp_in_the_spire.generator.Generator;

import java.lang.reflect.InvocationTargetException;

//生成xy数据的generator
public class GeneratorXY<T> extends Generator<T> {

    float x;
    float y;

    //自身的构造函数
    public GeneratorXY(float x,float y,
       Class<?> targetClass //具体的构造的类
    )
    {
        try
        {
            //获取构造函数
            this.constructor = targetClass.getConstructor(float.class,float.class);
        }
        catch (NoSuchMethodException e)
        {
            //e.printStackTrace();
        }
    }

    @Override
    public T generate() {
        try
        {
            if(this.constructor != null)
                return (T)this.constructor.newInstance(x,y);
        }
        catch (InstantiationException |
               IllegalAccessException |
               InvocationTargetException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
