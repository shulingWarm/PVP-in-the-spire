package pvp_in_the_spire.generator;

import java.lang.reflect.Constructor;

//各种数据类型的生成器
//以后双端通信映射时尽量用这种方式来将其标准化
public class Generator<T> {

    //构造器
    public Constructor<?> constructor = null;

    //这个类需要子类来实现
    public T generate()
    {
        return null;
    }

    //判断是否有效
    public boolean isValid()
    {
        return constructor != null;
    }

}
