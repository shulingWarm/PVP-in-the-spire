package pvp_in_the_spire.ui;

import com.megacrit.cardcrawl.core.AbstractCreature;

//敌人的box和角色的box的共同父类
public class CreatureBox extends AbstractPage {

    //获取creature的位置
    public float[] getLocation()
    {
        return new float[]{0,0};
    }


    //获得creature的实体
    public AbstractCreature getCreature()
    {
        return null;
    }
}
