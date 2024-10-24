package pvp_in_the_spire.ui.configOptions;

import pvp_in_the_spire.ui.AbstractPage;
import pvp_in_the_spire.ui.TextLabel;
import pvp_in_the_spire.ui.ToggleInterface;
import pvp_in_the_spire.ui.UserToggle;
import pvp_in_the_spire.helpers.FontLibrary;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;

//这是专门用于显示option的地方
//这里面包含文本和一个toggle按钮
public class ToggleOption extends AbstractPage {

    //文本框
    public TextLabel label;

    public UserToggle userToggle;

    public static final float HEIGHT = Settings.HEIGHT * 0.04f;

    public ToggleOption(float x,float y,
        String text,float width,
        ToggleInterface toggleInterface,
        int idToggle
    )
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = HEIGHT;
        //初始化文本框
        this.label = new TextLabel(x,y,
            width,HEIGHT,text, FontLibrary.getBaseFont());
        this.label.isLeftAlign = true;
        //初始化按钮
        this.userToggle = new UserToggle(this.x + this.width,y+this.height * 0.5f,toggleInterface,idToggle);
    }

    @Override
    public void move(float xChange, float yChange) {
        super.move(xChange,yChange);
        label.move(xChange,yChange);
        userToggle.move(xChange,yChange);
    }

    @Override
    public void update() {
        label.update();
        this.userToggle.update();
    }

    @Override
    public void render(SpriteBatch sb) {
        this.label.render(sb);
        this.userToggle.render(sb);
    }

    //更新option里面的状态
    public void setStage(boolean stage)
    {
        this.userToggle.enabled = stage;
    }
}
