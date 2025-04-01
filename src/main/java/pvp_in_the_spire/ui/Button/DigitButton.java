package pvp_in_the_spire.ui.Button;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import pvp_in_the_spire.helpers.FontLibrary;
import pvp_in_the_spire.ui.AbstractPage;
import pvp_in_the_spire.ui.Button.WithUpdate.BaseUpdateButton;
import pvp_in_the_spire.ui.ClickableInputBox;
import pvp_in_the_spire.ui.Events.ClickCallback;

import java.util.Set;

//用于控制数字加减的按钮
public class DigitButton extends AbstractPage implements ClickCallback {

    //左边的按钮
    public BaseUpdateButton leftButton;
    //右边的加号按钮
    public BaseUpdateButton rightButton;
    //中间的数字输入框
    public ClickableInputBox inputBox;
    //加减按钮的大小
    public static final float BUTTON_WIDTH = Settings.WIDTH*0.03f;

    public DigitButton(float x, float y)
    {
        this.x = x;
        this.y = y;
        //初始化本体的宽度
        this.width = Settings.WIDTH*0.2f;
        this.height = Settings.HEIGHT*0.07f;
        leftButton = new BaseUpdateButton(
            this.x,
            this.y,
            BUTTON_WIDTH,
            this.height*0.6f,"-", FontLibrary.getBaseFont(),
                ImageMaster.PROFILE_SLOT,this
        );
        //右边的加号按钮
        this.rightButton = new BaseUpdateButton(
                this.x + this.width*0.34f,
                this.y,
                BUTTON_WIDTH,
                this.height*0.6f,"+", FontLibrary.getBaseFont(),
                ImageMaster.PROFILE_SLOT,this
        );
        //中间的文本输入框
        this.inputBox = new ClickableInputBox(this.x + this.width*0.16f,
                this.y + this.height*0.23f,this.width*0.15f,this.height*0.24f,FontLibrary.getBaseFont());
    }

    @Override
    public void move(float xChange, float yChange) {
        super.move(xChange, yChange);
        this.leftButton.move(xChange,yChange);
        this.rightButton.move(xChange,yChange);
        this.inputBox.move(xChange,yChange);
    }

    @Override
    public void clickEvent(BaseUpdateButton button) {

    }

    @Override
    public void render(SpriteBatch sb) {
        this.leftButton.render(sb);
        this.inputBox.render(sb);
        this.rightButton.render(sb);
    }

    @Override
    public void update() {
        this.leftButton.update();
        this.inputBox.update();
        this.rightButton.update();
    }
}
