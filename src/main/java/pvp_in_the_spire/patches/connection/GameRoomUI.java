package pvp_in_the_spire.patches.connection;

import pvp_in_the_spire.ui.*;
import pvp_in_the_spire.ui.Button.BackButton;
import pvp_in_the_spire.ui.Button.ConnectInLocal;
import pvp_in_the_spire.ui.Button.ConnectInSteam;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;

//游戏房间模式的连接界面，将来可以依靠这个东西做更多丰富的设置
public class GameRoomUI {

    //完整的黑色背景
    public PlainBox background = new PlainBox();

    //通过局域网进行连接的按钮
    public ConnectInLocal localConnectButton;

    //通过steam连接的按钮
    public ConnectInSteam steamConnectButton;

    //需要使用的字体资源
    public BitmapFont font;

    //目前的核心界面
    public AbstractPage mainPage = null;

    //返回按钮
    public BackButton backButton;

    //用于测试多选按钮的界面
    public TogglePanel testPage;

    public GameRoomUI()
    {
        //初始化纹理资源
        TextureManager.initTexture();
        //初始化需要被使用的字体
        this.font = InputIpBox.generateFont(22);
        //初始化进行局域网连接的按钮
        localConnectButton = new ConnectInLocal(
            Settings.WIDTH*0.1F,Settings.HEIGHT*0.4F,Settings.WIDTH*0.2F,
                Settings.HEIGHT*0.2F,this.font
        );
        //初始化通过steam连接的按钮
        this.steamConnectButton = new ConnectInSteam(
            Settings.WIDTH*0.7F,Settings.HEIGHT*0.4F,Settings.WIDTH*0.2F,
            Settings.HEIGHT*0.2F,this.font
        );
        //初始化返回按钮
        this.backButton = new BackButton(BackButton.getFont());
        //this.testPage = new TogglePanel();
    }

    //页面更新相关的逻辑
    public void update()
    {
        //判断目前是否有主界面
        if(mainPage!=null)
        {
            mainPage.update();
        }
        //testPage.update();
    }

    //对返回按钮的渲染，这里面包含如果用户已经点击过了，就直接退出的逻辑
    public void renderBackButton(SpriteBatch sb)
    {
        //判断返回是不是刚刚被点击过
        if(backButton.isJustClicked())
        {
            CardCrawlGame.startOver = true;
            CardCrawlGame.fadeToBlack(0.5f);
            MeunScreenFadeout.allowForExit = true;
        }
        backButton.render(sb);
    }

    //对场景的渲染
    public void render(SpriteBatch sb)
    {
        //渲染背景框
        background.render(sb);
        //testPage.render(sb);
        //判断目前是否有主界面
        if(mainPage!=null)
        {
            //判断这个界面是否已经可以被删除
            if(mainPage.judgeIsClosed())
            {
                mainPage = null;
            }
            else {
                mainPage.render(sb);
                return;
            }
        }
        else {
            //没有主界面的时候才渲染返回按钮
            this.renderBackButton(sb);
        }
        //渲染进行局域网连接的按钮
        localConnectButton.render(sb);
        //渲染steam连接的按钮
        steamConnectButton.render(sb);
        //如果刚刚点击了steam连接的按钮，则进入steam连接的界面
        if(steamConnectButton.isJustClicked())
        {
            this.mainPage = new SteamFriendSelectPage();
        }
        //判断是否选择了通过局域网联机
        if(localConnectButton.isJustClicked())
        {
            this.mainPage = new LocalConnectPage();
        }
    }

}
