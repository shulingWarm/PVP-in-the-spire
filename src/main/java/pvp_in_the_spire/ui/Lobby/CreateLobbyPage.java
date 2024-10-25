package pvp_in_the_spire.ui.Lobby;

import pvp_in_the_spire.ui.*;
import pvp_in_the_spire.ui.Button.WithUpdate.BaseUpdateButton;
import pvp_in_the_spire.ui.Events.ClickCallback;
import pvp_in_the_spire.ui.Events.ClosePageEvent;
import pvp_in_the_spire.ui.Events.ConnectOkEvent;
import pvp_in_the_spire.ui.Events.CreateSuccessEvent;
import pvp_in_the_spire.helpers.FontLibrary;
import pvp_in_the_spire.network.Lobby.LobbyManager;
import pvp_in_the_spire.network.Lobby.PVPLobby;
import pvp_in_the_spire.patches.steamConnect.SteamManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.codedisaster.steamworks.SteamMatchmaking;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;

//创建房间用的按钮
public class CreateLobbyPage extends AbstractPage
        implements ClickCallback, ConnectOkEvent, ClosePageEvent {

    //带有标签的输入框
    public InputBoxWithLabel labelInputBox;
    //密码标签
    public InputBoxWithLabel passwordBox;
    //背景图
    public PlainBox background;

    //创建房间的取消的按钮
    public BaseUpdateButton okButton;
    public BaseUpdateButton cancelButton;

    //关闭页面的回调函数 这个东西需要调用者来赋值
    public ClosePageEvent closeCallback;

    //创建房间成功时的回调
    public CreateSuccessEvent createSuccessEvent;

    //显示在上层的页面
    public AbstractPage overlapPage = null;

    public static final UIStrings uiStrings =
            CardCrawlGame.languagePack.getUIString("CreateLobbyPage");

    public CreateLobbyPage(CreateSuccessEvent successCallback)
    {
        //初始化带有标签的输入框
        labelInputBox = new InputBoxWithLabel(
                (float) Settings.WIDTH * 0.1f,
                (float) Settings.HEIGHT * 0.65f,
                (float) Settings.WIDTH * 0.8f,
                (float) Settings.HEIGHT * 0.04f,
                uiStrings.TEXT[0],
                FontLibrary.getFontWithSize(64),
                true
        );
        //初始化密码输入框
        passwordBox = new InputBoxWithLabel(
                (float) Settings.WIDTH * 0.1f,
                (float) Settings.HEIGHT * 0.45f,
                (float) Settings.WIDTH * 0.8f,
                (float) Settings.HEIGHT * 0.04f,
                uiStrings.TEXT[1],
                FontLibrary.getFontWithSize(64),
                true
        );
        //初始化背景图
        this.background = new PlainBox(
                (float) Settings.WIDTH * 0.9f,
                (float) Settings.HEIGHT * 0.6f,
                new Color(1.0F, 0.965F, 0.886F, 0.74F)
        );
        //指定背景图的位置
        this.background.x = Settings.WIDTH * 0.05f;
        this.background.y = Settings.HEIGHT * 0.2f;
        //初始化ok用的那个按钮
        this.okButton = new BaseUpdateButton(
            Settings.WIDTH * 0.2f,
            Settings.HEIGHT * 0.3f,
            Settings.WIDTH*0.2f,
            Settings.HEIGHT*0.1f,
            uiStrings.TEXT[2],
            FontLibrary.getBaseFont(),
            ImageMaster.PROFILE_SLOT,
            this
        );
        //初始化取消按钮
        this.cancelButton = new BaseUpdateButton(
            Settings.WIDTH * 0.6f,
            Settings.HEIGHT * 0.3f,
            Settings.WIDTH*0.2f,
            Settings.HEIGHT*0.1f,
            uiStrings.TEXT[3],
            FontLibrary.getBaseFont(),
            ImageMaster.PROFILE_SLOT,
            this
        );
        //指定创建房间成功时的回调
        this.createSuccessEvent = successCallback;
    }

    //初始化页面
    public void initPage()
    {
        labelInputBox.triggerEdit();
        //初始化第二个输入框
        passwordBox.triggerEdit();
        //在steam的管理界面里面注册回调函数
        //创建房间成功时会收到这个信号
        LobbyManager.callback.lobbyCreateCallback = this;
        //准备创建房间里面的数据
        SteamManager.prepareNetworking();
        //把房间名初始化成玩家的名字
        labelInputBox.inputBox.textField =
            SteamManager.steamFriends.getPersonaName() + uiStrings.TEXT[4];
    }

    @Override
    public void closePageEvent(AbstractPage page) {
        //把等待页面关掉
        this.overlapPage = null;
    }

    //这个主要是创建房间成功时的回调
    @Override
    public void connectOk(boolean isOwner) {
        //移除overlap房间
        this.overlapPage = null;
        //初始化房间属性
        LobbyManager.initLobbyProperty(
            LobbyManager.callback.lobbyId,
                this.labelInputBox.getText(),
                this.passwordBox.getText()
        );
        //记录当前的lobby
        LobbyManager.currentLobby = new PVPLobby(
                this.labelInputBox.getText(),
                this.passwordBox.getText(),
                LobbyManager.callback.lobbyId
        );
        //调用创建房间成功时的回调
        this.createSuccessEvent.createSuccessEvent(0);
    }

    //按钮点击事件的回调函数
    @Override
    public void clickEvent(BaseUpdateButton button) {
        //判断是不是关闭页面
        if(button == cancelButton)
        {
            if(closeCallback != null)
                closeCallback.closePageEvent(this);
        }
        else {
            //显示上层渲染
            WaitPage.getInstance().init(uiStrings.TEXT[5],this);
            this.overlapPage = WaitPage.getInstance();
            //如果点击的是创建房间，那么就执行创建房间的逻辑
            LobbyManager.matchmaking.createLobby(SteamMatchmaking.LobbyType.Public,4);
        }

    }

    @Override
    public void render(SpriteBatch sb) {
        //渲染背景
        this.background.render(sb);
        this.labelInputBox.render(sb);
        //渲染密码输入框
        this.passwordBox.render(sb);
        //渲染按钮
        this.okButton.render(sb);
        this.cancelButton.render(sb);
        //判断是否需要渲染上层等待框
        if(this.overlapPage != null)
            this.overlapPage.render(sb);
    }

    @Override
    public void update() {
        //如果有上层输入框，就不再更新内层了
        if(this.overlapPage != null)
        {
            this.overlapPage.update();
            return;
        }
        this.background.update();
        this.labelInputBox.update();
        //更新密码输入框
        this.passwordBox.update();
        this.okButton.update();
        this.cancelButton.update();
    }
}
