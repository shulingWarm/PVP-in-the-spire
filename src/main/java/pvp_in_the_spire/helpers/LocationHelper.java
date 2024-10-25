package pvp_in_the_spire.helpers;

import com.megacrit.cardcrawl.core.Settings;

//对于位置的变换的常用操作
public class LocationHelper {

    //反转x
    public static float xInvert(float x)
    {
        return Settings.WIDTH - x;
    }

    //反转y,这是给以后的接口
    public static float yInvert(float y)
    {
        return y;
    }

}
