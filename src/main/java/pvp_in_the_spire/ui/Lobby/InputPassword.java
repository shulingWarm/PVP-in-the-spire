package pvp_in_the_spire.ui.Lobby;

import pvp_in_the_spire.ui.AbstractPage;
import pvp_in_the_spire.ui.Button.WithUpdate.BaseUpdateButton;
import pvp_in_the_spire.ui.Events.ClickCallback;
import pvp_in_the_spire.ui.Events.ClosePageEvent;
import pvp_in_the_spire.ui.Events.PasswordCorrect;
import pvp_in_the_spire.ui.InputBoxWithLabel;
import pvp_in_the_spire.ui.PlainBox;
import pvp_in_the_spire.ui.TextLabel;
import pvp_in_the_spire.screens.WarningText;
import pvp_in_the_spire.helpers.FontLibrary;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.util.Objects;

//输入密码的界面
public class InputPassword extends AbstractPage
    implements ClickCallback
{

    //密码的答案
    public String password;

    //输入密码的界面
    public InputBoxWithLabel inputBox;

    //在标题位置的文字label
    public TextLabel label;

    //背景框
    public PlainBox plainBox;

    //确定按钮
    public BaseUpdateButton confirmButton;

    public BaseUpdateButton cancelButton;

    //输入密码错误时的警告信息
    public WarningText warningText;

    //输入正确的密码时的回调函数
    public PasswordCorrect correctEvent = null;

    //当页面被关闭时的回调函数
    public ClosePageEvent closePageEvent = null;

    //按钮的半径
    public static final float BUTTON_RAID = Settings.WIDTH * 0.05f;
    //按钮的中心点相对于中间位置的偏移量
    public static final float BUTTON_OFFSET = Settings.WIDTH * 0.1f;
    //按钮的高度
    public static final float BUTTON_Y = Settings.WIDTH * 0.2f;
    //按钮的高度
    public static final float BUTTON_HEIGHT = Settings.HEIGHT * 0.08f;

    public static final UIStrings uiStrings =
            CardCrawlGame.languagePack.getUIString("InputPassword");

    public InputPassword(String password)
    {
        this.password = password;
        //初始化输入密码的界面
        inputBox = new InputBoxWithLabel(
                Settings.WIDTH * 0.2f,
                Settings.HEIGHT * 0.5f,
                Settings.WIDTH * 0.6f,
                Settings.HEIGHT * 0.025f,
                uiStrings.TEXT[0],
                FontLibrary.getFontWithSize(33),
                false
        );
        //初始化显示在最上面的标题
        this.label = new TextLabel(
            Settings.WIDTH * 0.25f,
                Settings.HEIGHT * 0.6f,
                Settings.WIDTH * 0.5f,
                Settings.HEIGHT*0.04f,
                uiStrings.TEXT[1],
                FontLibrary.getFontWithSize(44)
        );
        //初始化背景框
        this.plainBox = new PlainBox(
                Settings.WIDTH * 0.8f,
                Settings.HEIGHT * 0.5f,
                Color.valueOf("509D97AA")
        );
        plainBox.x = Settings.WIDTH * 0.1f;
        plainBox.y = Settings.HEIGHT * 0.25f;
        //初始化确定按钮
        this.confirmButton = new BaseUpdateButton(
            Settings.WIDTH * 0.5f - BUTTON_OFFSET - BUTTON_RAID,
                BUTTON_Y,
                BUTTON_RAID * 2,
                BUTTON_HEIGHT,
                uiStrings.TEXT[2],
                FontLibrary.getBaseFont(),
                ImageMaster.PROFILE_SLOT,
                this
        );
        //初始化取消按钮
        this.cancelButton = new BaseUpdateButton(
                Settings.WIDTH * 0.5f + BUTTON_OFFSET - BUTTON_RAID,
                BUTTON_Y,
                BUTTON_RAID * 2,
                BUTTON_HEIGHT,
                uiStrings.TEXT[3],
                FontLibrary.getBaseFont(),
                ImageMaster.PROFILE_SLOT,
                this
        );
        //初始化警告信息
        this.warningText = new WarningText(
                uiStrings.TEXT[4],
                FontLibrary.getFontWithSize(33),
                Settings.WIDTH * 0.5f,
                Settings.HEIGHT * 0.4f,
                Color.RED
        );
    }

    @Override
    public void clickEvent(BaseUpdateButton button) {
        //判断是不是确定按钮
        if(button == this.confirmButton)
        {
            //判断密码是否正确
            if(Objects.equals(inputBox.getText(), this.password) &&
                    correctEvent != null
            )
            {
                //调用输入密码成功时的回调函数
                this.correctEvent.onPasswordCorrect();
            }
            else {
                //显示警告信息
                this.warningText.idFrame = 0;
            }
        }
        //否则判断是否有关闭页面的回调函数
        else if(this.closePageEvent != null)
        {
            this.closePageEvent.closePageEvent(this);
        }
    }

    //触发进入编辑状态
    public void open()
    {
        this.inputBox.triggerEdit();
    }


    @Override
    public void render(SpriteBatch sb) {
        //渲染背景
        this.plainBox.render(sb);
        //渲染标题
        this.label.render(sb);
        //渲染密码输入框
        this.inputBox.render(sb);
        //渲染按钮
        this.confirmButton.render(sb);
        this.cancelButton.render(sb);
        //渲染警告信息
        this.warningText.render(sb);
    }

    //更新密码输入内容
    @Override
    public void update() {
        this.plainBox.update();
        this.label.update();
        this.inputBox.update();
        this.confirmButton.update();
        this.cancelButton.update();
    }
}
