package WarlordEmblem.Generator.Generators;

import WarlordEmblem.Generator.Generator;

import java.lang.reflect.InvocationTargetException;

//没有传入参数的generator构造函数
public class GeneratorEmpty<T> extends Generator<T> {

    //空构造函数
    public GeneratorEmpty(Class<?> targetClass)
    {
        try
        {
            //获取构造函数
            this.constructor = targetClass.getConstructor();
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
                return (T)this.constructor.newInstance();
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
