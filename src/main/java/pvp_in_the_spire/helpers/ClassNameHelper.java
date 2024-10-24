package pvp_in_the_spire.helpers;

import pvp_in_the_spire.generator.Generator;
import pvp_in_the_spire.generator.Generators.GeneratorEmpty;
import pvp_in_the_spire.generator.Generators.GeneratorXY;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

//获取各种类名的接口
//当初写这个是为了获取怪物的类名
public class ClassNameHelper {

    //获取class的名称，也就是一个抽象的class
    public static String getCreatureClassName(AbstractCreature creature)
    {
        return creature.getClass().getName();
    }

    //根据类名获得class的数据类型
    public static Class<?> getClassType(String className)
    {
        Class<?> classType;
        try
        {
            classType = Class.forName(className);
        }
        catch (ClassNotFoundException e)
        {
            System.out.printf("%s not found\n",className);
            return null;
        }
        return classType;
    }

    //根据monster的name随便构造一个monster
    public static AbstractMonster createMonster(String name)
    {
        //从name转换成Class
        Class<?> targetClass = getClassType(name);
        if(targetClass == null)
            return null;
        //依次判断可能的构造函数
        Generator<AbstractMonster> abstractMonsterGenerator;
        abstractMonsterGenerator = new GeneratorEmpty<AbstractMonster>(targetClass);
        if(abstractMonsterGenerator.isValid())
            return abstractMonsterGenerator.generate();
        //传入xy形式的构造函数
        abstractMonsterGenerator = new GeneratorXY<>((float) Settings.WIDTH/3,
            (float)Settings.HEIGHT/3,
            targetClass);
        if(abstractMonsterGenerator.isValid())
            return abstractMonsterGenerator.generate();
        return null;
    }

}
