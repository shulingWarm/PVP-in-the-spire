package pvp_in_the_spire.ui;

import pvp_in_the_spire.ui.Button.WithUpdate.BaseUpdateButton;
import pvp_in_the_spire.ui.Events.ClickCallback;
import pvp_in_the_spire.ui.Events.ClosePageEvent;
import pvp_in_the_spire.helpers.FontLibrary;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;

//这是一个通用的等待页面
//这个类只需要一个实例
public class WaitPage extends AbstractPage implements ClickCallback {

    //大部分情况下只使用这个示例就可以了
    private static WaitPage instance = null;

    public static WaitPage getInstance()
    {
        if(instance == null)
        {
            instance = new WaitPage();
        }
        return instance;
    }

    //显示在最中间的背景
    public PlainBox background;

    //显示在中间位置的文本
    public TextLabel textLabel;

    //关闭页面时的回调函数
    public ClosePageEvent closePageEvent;

    //取消等待的按钮
    public BaseUpdateButton cancelButton;

    public static final UIStrings uiStrings =
            CardCrawlGame.languagePack.getUIString("WaitPage");

    public WaitPage()
    {
        //初始化中间位置的背景
        this.background = new PlainBox(
                Settings.WIDTH * 0.9f,
                Settings.HEIGHT * 0.9f,
                Color.valueOf("366D67AA")
        );
        this.background.x = Settings.WIDTH * 0.05f;
        this.background.y = Settings.HEIGHT * 0.05f;
        //初始化文本框
        this.textLabel = new TextLabel(
            Settings.WIDTH * 0.5f,
                Settings.HEIGHT * 0.5f,
                0,
                0,
                "",
                FontLibrary.getFontWithSize(45)
        );
        //初始化取消用的按钮
        this.cancelButton = new BaseUpdateButton(
            Settings.WIDTH * 0.4f,
                Settings.HEIGHT * 0.3f,
                Settings.WIDTH * 0.2f,
                Settings.HEIGHT*0.1f,
                uiStrings.TEXT[0],
                FontLibrary.getBaseFont(),
                ImageMaster.PROFILE_SLOT,
                this
        );
    }

    //点击事件
    @Override
    public void clickEvent(BaseUpdateButton button) {
        //调用关闭页面时的回调函数
        if(this.closePageEvent != null)
            this.closePageEvent.closePageEvent(this);
    }

    //另一种形式，同时指定按钮上的文本
    public void init(String text,ClosePageEvent closeCallback,String buttonText)
    {
        //改变文本框
        this.textLabel.text = text;
        //记录关闭页面时的回调函数
        this.closePageEvent = closeCallback;
        //更改按钮上的文本
        this.cancelButton.text = buttonText;
    }

    //对这个类操作真正的初始化
    public void init(String text, ClosePageEvent closeCallback)
    {
        this.init(text,closeCallback,uiStrings.TEXT[0]);
    }

    @Override
    public void render(SpriteBatch sb) {
        //渲染背景
        this.background.render(sb);
        this.textLabel.render(sb);
        //取消用的按钮
        this.cancelButton.render(sb);
    }

    @Override
    public void update() {
        this.background.update();
        this.textLabel.update();
        this.cancelButton.update();
    }
}
