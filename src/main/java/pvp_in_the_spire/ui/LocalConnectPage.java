package pvp_in_the_spire.ui;

import pvp_in_the_spire.ui.Button.BackButton;
import pvp_in_the_spire.ui.Events.ConnectOkEvent;
import pvp_in_the_spire.ui.Lobby.IpLobbyConfig;
import pvp_in_the_spire.screens.Buttons.AsServerButton;
import pvp_in_the_spire.screens.Buttons.ConnectButton;
import pvp_in_the_spire.screens.Buttons.MultiConnectButton;
import pvp_in_the_spire.screens.Buttons.MultiServerButton;
import pvp_in_the_spire.patches.connection.InputIpBox;
import pvp_in_the_spire.patches.connection.IpInputProcessor;
import pvp_in_the_spire.patches.connection.MeunScreenFadeout;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;

//通过局域网联机的界面
public class LocalConnectPage extends AbstractPage implements ConnectOkEvent {

    public Color background;
    //标题字体的颜色
    public Color fontColor;
    //输入框里面的颜色
    public Color textColor;
    //标题的字体
    public static BitmapFont titleFont;
    //ip输入框
    public IpInputProcessor ipInputProcessor=new IpInputProcessor();
    //作为server的按钮
    public AsServerButton button;
    //作为连接对方的按钮
    public ConnectButton buttonForConnection;

    //返回按钮
    public BackButton backButton;
    //下一层的自定义面板
    public AbstractPage configPage = null;

    //完整的黑色背景
    public PlainBox plainBackground = new PlainBox();

    //用于测试的一些界面，这里仅仅是搞一些debug
    public AbstractPage testPage;

    public LocalConnectPage()
    {
        background = new Color(1.0F, 0.965F, 0.886F,0.95F);
        //金色的字体，按照输入名字的框那样抄来的
        fontColor = Settings.GOLD_COLOR.cpy();
        textColor = new Color(1,1,1,1);
        //令它等于牌面上的字体，但放大一些
        if(titleFont==null)
        {
            titleFont = InputIpBox.generateFont(60);
        }
        //初始化连接至对方的按钮 注意这里把按钮改成了多人通信的版本
        buttonForConnection = new MultiConnectButton(0.52F*Settings.WIDTH,
                0.35F*Settings.HEIGHT,0.22F*Settings.WIDTH,0.13F*Settings.HEIGHT,
                InputIpBox.generateFont(40),ipInputProcessor.inputResult);
        //初始化用于实验的按钮
        //在这里换成了多server的连接器，为以后的多人连接做准备
        button = new MultiServerButton(0.27F*Settings.WIDTH,0.35F*Settings.HEIGHT,
                0.22F*Settings.WIDTH,0.13F*Settings.HEIGHT,
                InputIpBox.generateFont(40),ipInputProcessor.inputResult,buttonForConnection);
        //初始化返回按钮
        this.backButton = new BackButton(BackButton.getFont());
        //初始化的时候就是打开的时候
        Gdx.input.setInputProcessor(ipInputProcessor);
        //设置两个按钮的回调函数
        button.callbackEvent = this;
        buttonForConnection.callbackEvent = this;
    }

    //连接成功时的回调函数
    @Override
    public void connectOk(boolean isOwner) {
        //进入自定义面板的配置页面
        this.configPage = new IpLobbyConfig(isOwner);
    }

    //判断这个页面是否可以被关闭了
    @Override
    public boolean judgeIsClosed() {
        return backButton.isJustClicked();
    }

    public void renderInputBox(SpriteBatch sb)
    {
        sb.draw(ImageMaster.RENAME_BOX,
                0.25F*(float) Settings.WIDTH,
                0.35F*(float)Settings.HEIGHT,
                0.5F*(float)Settings.WIDTH,
                0.3F*(float)Settings.HEIGHT);
        String ipString=ipInputProcessor.inputResult.toString();
        //用于做输入域的工具
        FontHelper.renderSmartText(sb,titleFont,
                ipString,
                0.35F*(float) Settings.WIDTH,0.5F*(float)Settings.HEIGHT,
                1000.0F,0.0F,textColor,0.8F);
        //输入框里面的闪烁光标
        float tmpAlpha = (MathUtils.cosDeg((float)(System.currentTimeMillis() / 3L % 360L)) + 1.25F) / 3.0F * this.fontColor.a;
        FontHelper.renderSmartText(sb, titleFont,
                "_",
                0.35F*(float) Settings.WIDTH - 12.0F +
                        FontHelper.getSmartWidth(titleFont,
                                ipString,
                                1000.0F, 0.0F, 0.8F),
                0.5F*(float)Settings.HEIGHT,
                1000.0F, 0.0F,
                new Color(1.0F, 1.0F, 1.0F, tmpAlpha));
    }

    @Override
    public void update() {
        //如果下面有自定义面板，就调用一下自定义面板的更新
        if(configPage!=null)
        {
            configPage.update();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        this.plainBackground.render(sb);
        //如果有下一层的自定义面板，就渲染面板，不用再渲染这个了
        if(configPage!=null)
        {
            configPage.render(sb);
            //如果页面已经可以关了，那就重新换成空指针
            if(configPage.judgeIsClosed())
            {
                configPage = null;
            }
            return;
        }
        //设置颜色
        sb.setColor(background);
        //设置对应的窗口
        sb.draw(ImageMaster.OPTION_CONFIRM,0.2F*(float) Settings.WIDTH,
                0.3F*(float)Settings.HEIGHT,0.6F*(float)Settings.WIDTH,
                0.4F*(float)Settings.HEIGHT);
        //写标题的颜色
        FontHelper.renderFontCentered(sb,titleFont,"Input your friend's IP",
                (float)Settings.WIDTH/2.0F,Settings.HEIGHT*0.6F,this.fontColor);
        //渲染输入框
        renderInputBox(sb);
        //渲染按钮
        button.render(sb);
        buttonForConnection.render(sb);
        //对返回按钮的渲染
        backButton.render(sb);
        //判断返回是不是刚刚被点击过
        if(backButton.isJustClicked())
        {
            CardCrawlGame.startOver = true;
            CardCrawlGame.fadeToBlack(0.5f);
            MeunScreenFadeout.allowForExit = true;
        }
    }
}
