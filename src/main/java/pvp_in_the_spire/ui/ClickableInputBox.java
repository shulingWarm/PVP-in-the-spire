package pvp_in_the_spire.ui;

import pvp_in_the_spire.ui.Button.WithUpdate.BaseUpdateButton;
import pvp_in_the_spire.ui.Events.ClickCallback;
import pvp_in_the_spire.helpers.FontLibrary;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

//可以点击的输入框，不同位置的输入框显示不同的内容
public class ClickableInputBox extends InputBox implements ClickCallback {

    //透明的按钮，用于处理点击事件
    public BaseUpdateButton boxButton;

    //构造函数直接调用上层构造
    public ClickableInputBox(float x,float y,float width,float height,
                    BitmapFont font)
    {
        super(x,y,width,height,font);
        //初始化点击事件
        this.boxButton = new BaseUpdateButton(
            this.x,this.y - this.height,this.width,this.height * 2,"",
                FontLibrary.getBaseFont(),null,this
        );
    }

    //点击事件代表当前输入框被选中
    @Override
    public void clickEvent(BaseUpdateButton button) {
        this.open();
    }

    @Override
    public void update() {
        super.update();
        this.boxButton.update();
    }
}
