package pvp_in_the_spire.screens.midExit;

import pvp_in_the_spire.screens.UserButton;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

//取消结束游戏
public class ExitCancelButton extends UserButton {

    //是否已经被点击过
    public boolean clicked = false;

    public ExitCancelButton(float x, float y, float width, float height,
        BitmapFont font)
    {
        super(x,y,width,height,"cancel",font);
    }

    //按钮的点击事件
    @Override
    public void clickEvent()
    {
        clicked = true;
    }

}
