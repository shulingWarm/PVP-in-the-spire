package pvp_in_the_spire.ui.Lobby;

import pvp_in_the_spire.ui.*;
import pvp_in_the_spire.ui.Button.WithUpdate.BaseUpdateButton;
import pvp_in_the_spire.ui.Button.WithUpdate.LobbyButton;
import pvp_in_the_spire.ui.ConfigPageModules.MultiplayerConfigPage;
import pvp_in_the_spire.ui.Events.*;
import pvp_in_the_spire.helpers.FontLibrary;
import pvp_in_the_spire.network.Lobby.LobbyManager;
import pvp_in_the_spire.network.Lobby.PVPLobby;
import pvp_in_the_spire.patches.PanelScreenPatch;
import pvp_in_the_spire.patches.steamConnect.SteamManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;

import java.util.ArrayList;

//游戏大厅的主界面
public class LobbyScreen extends AbstractPage
    implements ClickCallback, ClosePageEvent,
        CreateSuccessEvent, LobbyListCallback,
        LobbyButtonCallback, RequestEvent,
        PasswordCorrect
{

    public static LobbyScreen instance;

    //返回按钮
    public BaseUpdateButton backButton;

    //背景板
    public PlainBox background;

    //用于显示各种房间的panel
    public BasePanel lobbyPanel;

    //创建房间的界面
    public CreateLobbyPage createPage = null;

    //在主界面上方的遮挡主页
    public AbstractPage overlapPage = null;

    //用来进入创建房间界面的按钮
    public BaseUpdateButton createEnterButton;

    //UI界面的字符串
    public static final UIStrings uiStrings =
            CardCrawlGame.languagePack.getUIString("LobbyScreen");

    //当前正想加入的房间
    public PVPLobby goingLobby = null;

    //当前的配置页面
    public MultiplayerConfigPage configPage = null;

    public LobbyScreen()
    {
        //初始化返回按钮
        backButton = new BaseUpdateButton(
                (float) Settings.WIDTH*0.8f,
                (float) Settings.HEIGHT*0.2f,
                (float)Settings.WIDTH*0.1f,
                (float)Settings.HEIGHT*0.06f,
                uiStrings.TEXT[0],
                FontLibrary.getBaseFont(),
                ImageMaster.PROFILE_SLOT,
                this
        );
        //新建用于创建房间的按钮
        this.createEnterButton = new BaseUpdateButton(
            (float) Settings.WIDTH*0.8f,
            (float) Settings.HEIGHT*0.3f,
            (float)Settings.WIDTH*0.1f,
            (float)Settings.HEIGHT*0.06f,
            uiStrings.TEXT[1],
            FontLibrary.getBaseFont(),
            ImageMaster.PROFILE_SLOT,
            this
        );
        //添加背景板
        background = new PlainBox(Settings.WIDTH*0.5f,
                Settings.HEIGHT*0.9f, Color.valueOf("366D6799"));
        background.x = Settings.WIDTH*0.25F;
        background.y = Settings.HEIGHT*0.05f;
        background.texture = ImageMaster.WHITE_SQUARE_IMG;
        //初始化用于显示房间的panel
        lobbyPanel = new BasePanel(background.x,background.y,
                background.width,background.height);
        //调用对按钮的初始化
        this.initPanelButtons();
        //初始化steam连接 后面转换为p2p连接的时候也会用到
        SteamManager.prepareNetworking();
        //初始化config page
        this.configPage = new MultiplayerConfigPage(false);
    }

    //初始化按钮，这里的按钮仅仅是用来测试按钮UI的
    public void initPanelButtons()
    {
        //初始化可选的房间
        LobbyManager.refreshAccessibleLobby(this);
    }

    //打开创建房间时用的页面
    public void openCreateLobbyPage()
    {
        //判断创建房间的页面是不是null
        if(this.createPage == null)
        {
            this.createPage = new CreateLobbyPage(this);
            //指定关闭页面时的回调函数
            this.createPage.closeCallback = this;
        }
        //把当前的主页面改成这个新的page
        this.overlapPage = this.createPage;
        //进入编辑状态 主要是打开页面的操作
        this.createPage.initPage();
    }

    //创建房间成功时的回调函数
    @Override
    public void createSuccessEvent(int infoFlag) {
        System.out.println("getting success message!!");
        //初始化通信协议
        LobbyManager.initLobbyChatServer();
        this.configPage.initNetworkStage(true,this);
        configPage.open();
        this.overlapPage = configPage;
    }

    //准备进入目标房间
    public void requestEnterRoom()
    {
        //打开进入房间的等待界面
        WaitPage.getInstance().init(uiStrings.TEXT[2],this);
        //将悬浮页面指定为等待页面
        this.overlapPage = WaitPage.getInstance();
        //指定回调函数 加入房间成功时也会调用这个回调
        LobbyManager.callback.requestEvent = this;
        //添加steam操作，准备添加房间
        LobbyManager.matchmaking.joinLobby(this.goingLobby.lobbyId);
    }

    //输入密码的回调函数
    @Override
    public void onPasswordCorrect() {
        //这说明输入的密码正确了，准备调用进入房间
        requestEnterRoom();
    }

    @Override
    public void onLobbyButtonClicked(PVPLobby lobby) {
        //指定当前想进入的房间
        this.goingLobby = lobby;
        //判断房间是否有密码
        String password = lobby.getPassword();
        if(password.isEmpty())
        {
            requestEnterRoom();
        }
        else {
            //打开输入密码的界面
            InputPassword passwordPage = new InputPassword(lobby.getPassword());
            //设置回调
            passwordPage.correctEvent = this;
            passwordPage.closePageEvent = this;
            //打开进入编辑状态
            passwordPage.open();
            //开始显示输入密码的界面
            this.overlapPage = passwordPage;
        }
    }

    //当steam回传所有的房间列表时的回调函数
    @Override
    public void receiveLobbyList(ArrayList<PVPLobby> lobbyList) {
        //清空列表
        this.lobbyPanel.clearPanel();
        //遍历所有的房间
        for(PVPLobby eachLobby : lobbyList)
        {
            //根据当前的pvp房间新建点击按钮
            LobbyButton tempButton = new LobbyButton(
                0,0,this.lobbyPanel.width*0.95f,this.lobbyPanel.height/12,
                eachLobby,this
            );
            //把按钮添加到页面中
            this.lobbyPanel.addNewPage(tempButton);
        }
    }

    //进入房间失败相关的逻辑
    public void enterLobbyFailEvent()
    {
        //把等待页面改成进入失败
        WaitPage.getInstance().init(uiStrings.TEXT[3],this,uiStrings.TEXT[4]);
        //取消正在进入的房间
        this.goingLobby = null;
    }

    @Override
    public void requestCallback(int indexFlag) {
        //判断进入房间是否成功
        if(indexFlag == 0)
        {
            enterLobbyFailEvent();
            return;
        }
        //回调到这里说明可以成功进入房间了
        if(this.overlapPage == WaitPage.getInstance() &&
                this.goingLobby != null
        )
        {
            System.out.println("Enter room init p2p");
            //调用初始化加入房间的函数
            LobbyManager.onLobbyEnter(this.goingLobby);
            this.overlapPage = this.configPage;
            //删除当前正在进入的房间
            this.goingLobby = null;
            //调用open操作
            configPage.initNetworkStage(false,this);
            configPage.open();
        }
    }

    @Override
    public void closePageEvent(AbstractPage page) {
        //取消覆盖页面
        this.overlapPage = null;
        this.goingLobby = null;
    }

    @Override
    public void clickEvent(BaseUpdateButton button) {
        //判断按钮是不是返回按钮
        if(button == this.backButton)
        {
            //标记返回，现在只有返回这一种情况
            PanelScreenPatch.lobbyFlag = false;
            //主界面改成大厅
            CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.MAIN_MENU;
            CardCrawlGame.mainMenuScreen.lighten();
        }
        //另外判断是不是准备创建房间
        else if(button == this.createEnterButton)
        {
            openCreateLobbyPage();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        //判断是否有遮挡页面
        if(this.overlapPage != null)
        {
            this.overlapPage.render(sb);
            return;
        }
        //渲染背景板
        this.background.render(sb);
        //渲染返回按钮
        backButton.render(sb);
        //渲染创建房间用的按钮
        this.createEnterButton.render(sb);
        //渲染panel
        lobbyPanel.render(sb);
    }

    @Override
    public void update() {
        //判断是否需要更新遮挡页面
        if(this.overlapPage != null)
        {
            this.overlapPage.update();
            return;
        }
        backButton.update();
        this.createEnterButton.update();
        //更新显示按钮的列表
        lobbyPanel.update();
    }
}
