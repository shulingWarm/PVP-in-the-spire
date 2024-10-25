package pvp_in_the_spire.ui;

import pvp_in_the_spire.ui.Button.WithUpdate.BaseUpdateButton;
import pvp_in_the_spire.ui.Button.WithUpdate.TwinkleButton;
import pvp_in_the_spire.ui.Events.ClickCallback;
import pvp_in_the_spire.helpers.FontLibrary;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;

//一个可以被折叠的页面 可以点击它把它展开，展开后还可以关上
public class FoldPage extends AbstractPage
    implements ClickCallback
{

    //用于把页面打开的按钮
    public TwinkleButton openButton;
    //用于把页面关闭的按钮
    public BaseUpdateButton closeButton;
    //打开时显示的页面
    public AbstractPage mainPage;

    //判断主界面现在是否已经打开了
    public boolean mainPageFlag = false;

    public FoldPage(){}

    public FoldPage(AbstractPage mainPage)
    {
        //初始化用于把页面打开的按钮
        openButton = new TwinkleButton(Settings.WIDTH*0.01f, Settings.HEIGHT / 2.f,
            Settings.WIDTH * 0.1f,Settings.HEIGHT * 0.1f,
                "打开", FontLibrary.getBaseFont(), ImageMaster.PROFILE_SLOT,
                this, null);
        //初始化用于关闭页面的按钮
        //先临时把它显示在右上角
        closeButton = new BaseUpdateButton(Settings.WIDTH * 0.3f,
                Settings.HEIGHT * 0.8f,Settings.WIDTH * 0.1f,
                Settings.HEIGHT*0.1f,"",FontLibrary.getBaseFont(),
                ImageMaster.PROFILE_SLOT,this);
        //初始化核心用于开关的页面
        this.mainPage = mainPage;
    }

    @Override
    public void open() {
        this.mainPageFlag = true;
        this.mainPage.open();
    }

    @Override
    public void close() {
        this.mainPageFlag = false;
        this.mainPage.close();
        this.openButton.setTwinkle(false);
    }

    //反转状态，开着的情况下就关了，关着的情况下就打开
    public void invertStage()
    {
        if(mainPageFlag)
            this.close();
        else
            this.open();
    }

    @Override
    public void clickEvent(BaseUpdateButton button) {
        if(button == this.openButton)
        {
            this.open();
        }
        else if(button == this.closeButton)
        {
            this.close();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        //判断是否渲染主界面
        if(this.mainPageFlag)
        {
            this.mainPage.render(sb);
            //渲染关闭按钮
            this.closeButton.render(sb);
        }
        else {
            //渲染用于打开页面的按钮
            this.openButton.render(sb);
        }
    }

    @Override
    public void update() {
        if(this.mainPageFlag)
        {
            this.mainPage.update();
            this.closeButton.update();
        }
        else {
            this.openButton.update();
        }
    }
}
