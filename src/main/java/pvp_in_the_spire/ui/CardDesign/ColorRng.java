package pvp_in_the_spire.ui.CardDesign;

import java.util.Random;
import com.badlogic.gdx.graphics.Color;

//颜色的随机生成器
public class ColorRng {

    //基本的随机数
    Random baseRng;

    public ColorRng()
    {
        this.baseRng = new Random();
    }

    //获得一个随机的颜色
    //透明度永远是1
    public Color getRandColor()
    {
        return new Color(baseRng.nextFloat(),
            baseRng.nextFloat(),
            baseRng.nextFloat(), 1.f
        );
    }

}
