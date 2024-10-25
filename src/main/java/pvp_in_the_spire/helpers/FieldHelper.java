package pvp_in_the_spire.helpers;

import java.lang.reflect.Field;

//要从类里面强行获取私有成员时用这个class
public class FieldHelper {

    //获得一个类的私有静态变量
    public static <RetType> RetType getPrivateStaticValue(Class<?> clazz,
                  String fieldName)
    {
        try
        {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (RetType) field.get(null);
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 模板函数，通过反射获取指定对象的（包括父类）私有成员变量的值
     * @param obj 需要获取成员变量的对象实例
     * @param fieldName 需要获取的私有成员变量的名字
     * @param <RetType> 成员变量的类型
     * @return 成员变量的值
     */
    public static <RetType> RetType getPrivateFieldValue(Object obj, String fieldName)
        {
        // 获取对象的Class对象
        Class<?> clazz = obj.getClass();

        // 循环向上搜索直到找到包含该字段的类
        while (clazz != null) {
            try {
                // 尝试获取指定名字的私有成员变量
                Field field = clazz.getDeclaredField(fieldName);

                // 设置此字段可访问，以便能获取私有变量
                field.setAccessible(true);

                // 获取并转换为指定的泛型类型
                RetType fieldValue = (RetType) field.get(obj);

                return fieldValue;
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // 如果当前类找不到，则尝试其父类
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    //设置某个私有成员的值
    public static <RetType> void setPrivateFieldValue(Object obj, String fieldName, RetType newValue)
    {
        // 获取对象的Class对象
        Class<?> clazz = obj.getClass();

        // 循环向上搜索直到找到包含该字段的类
        while (clazz != null) {
            try {
                // 尝试获取指定名字的私有成员变量
                Field field = clazz.getDeclaredField(fieldName);

                // 设置此字段可访问，以便能获取私有变量
                field.setAccessible(true);

                //设置它的目标值
                field.set(obj,newValue);

                return;
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // 如果当前类找不到，则尝试其父类
                clazz = clazz.getSuperclass();
            }
        }
    }

}
