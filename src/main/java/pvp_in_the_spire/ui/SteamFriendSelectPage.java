package pvp_in_the_spire.ui;

import pvp_in_the_spire.ui.Button.BackButton;
import pvp_in_the_spire.ui.Button.SteamFriendButton;
import pvp_in_the_spire.ui.Button.SteamFriendInterface;
import pvp_in_the_spire.ui.Steam.SteamFriendInfo;
import pvp_in_the_spire.AutomaticSocketServer;
import pvp_in_the_spire.SocketServer;
import pvp_in_the_spire.SteamSocketServer;
import pvp_in_the_spire.network.SteamConnector;
import pvp_in_the_spire.patches.connection.InputIpBox;
import pvp_in_the_spire.patches.steamConnect.SteamManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.codedisaster.steamworks.SteamFriends;
import com.codedisaster.steamworks.SteamID;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;

import java.util.ArrayList;

//选择steam好友的界面
public class SteamFriendSelectPage extends AbstractPage implements SteamFriendInterface {

    //一个用于显示steam好友的列表
    public BasePanel steamFriendPanel = null;

    public PlainBox panelBackground;

    public BitmapFont steamFriendFont;

    //目前正在等待的好友的信息
    public SteamFriendInfo waitingFriend = null;
    //目前已经注册过的所有按钮
    ArrayList<SteamFriendButton> buttonArrayList =
            new ArrayList<>();

    //返回上一层的按钮
    public BackButton backButton;

    //与好友联机成功后的config页面
    public ConfigPage configPage=null;

    public SteamFriendSelectPage()
    {
        //把整个page初始化为全屏
        this.x = 0;
        this.y = 0;
        this.width = Settings.WIDTH;
        this.height = Settings.HEIGHT;
        //初始化用于显示好友列表的panel
        steamFriendPanel = new BasePanel(
            Settings.WIDTH*0.35F,Settings.HEIGHT*0.1f,
                Settings.WIDTH*0.3F,Settings.HEIGHT*0.8f
        );
        //准备返回按钮
        backButton = new BackButton(BackButton.getFont());
        //添加一个相同大小的纯色色块
        //取色工具 gpick
        panelBackground = new PlainBox(steamFriendPanel.width,
                steamFriendPanel.height, Color.valueOf("363D6799"));
        panelBackground.x = steamFriendPanel.x;
        panelBackground.y = steamFriendPanel.y;
        panelBackground.texture = ImageMaster.WHITE_SQUARE_IMG;
        //初始化steam选好友的界面
        initSteamFriendButtons();
    }

    //初始化steam 好友的按钮
    public void initSteamFriendButtons()
    {
        //准备steam的连接工具
        SteamManager.prepareNetworking();
        //获取自己的steam名字
        SocketServer.myName = SteamManager.getMyName();
        //获取好友的数量
        int friendNum = SteamManager.steamFriends.getFriendCount(
                SteamFriends.FriendFlags.All
        );
        //初始化好友界面按钮上的字体
        if(steamFriendFont==null)
        {
            steamFriendFont = InputIpBox.generateFont(24);
        }
        //遍历所有的steam好友
        for(int idFriend=0;idFriend<friendNum;++idFriend)
        {
            //获得steam好友的Id
            SteamID steamID = SteamManager.steamFriends.getFriendByIndex(idFriend,SteamFriends.FriendFlags.All);
            //获取好友的name
            String friendName = SteamManager.steamFriends.getFriendPersonaName(steamID);
            //初始化一个steam好友的按钮放到page里面
            SteamFriendButton friendButton = new SteamFriendButton(
                new SteamFriendInfo(steamID,friendName),0,0,
        Settings.HEIGHT*0.05f, Settings.WIDTH*0.27F,steamFriendFont,
        this
            );
            friendButton.buttonTexture = ImageMaster.PROFILE_SLOT;
            //把选这个好友的按钮添加到列表里面
            steamFriendPanel.addNewPage(friendButton);
            //记录注册过的按钮
            this.buttonArrayList.add(friendButton);
        }
    }

    public void update()
    {
        //更新的时候如果有设置页面就只处理设置页面
        if(configPage!=null)
        {
            configPage.update();
            return;
        }
        steamFriendPanel.update();
    }

    //更新所有的按钮，把所有的按钮重置成没有被点击
    public void resetFriendButtons()
    {
        //遍历所有的按钮，把它恢复默认
        for(SteamFriendButton eachButton : this.buttonArrayList)
        {
            if(eachButton.friendInfo != this.waitingFriend)
            {
                eachButton.waitingFlag = false;
            }
        }
    }

    //发生steam好友的点击事件时的回调函数
    public void friendSelectTrigger(SteamFriendInfo info)
    {
        //判断是否更新了连接对象
        if(this.waitingFriend != info)
        {
            //说明有新的人连接了
            this.waitingFriend = info;
            resetFriendButtons();
            //初始化steam的连接名柄
            AutomaticSocketServer.globalServer = new SteamSocketServer(info.steamID);
            //把上次发送的时间设置成0,确保一进去就发送一次
            SteamConnector.lastHelloTime = 0;
        }
        else {
            //如果是相同的好友重复被点击了，那说明这个连接被取消了
            this.waitingFriend = null;
            //把联机的server置空
            AutomaticSocketServer.globalServer = null;
        }
    }

    //用于判断是否已经被关闭了
    @Override
    public boolean judgeIsClosed() {
        return this.backButton.isJustClicked();
    }

    //渲染的时候显示这个基本的panel
    public void render(SpriteBatch sb)
    {
        //有配置页面的时候直接渲染配置页面
        if(this.configPage != null)
        {
            configPage.render(sb);
            //对于steam连接的情况，在自定义界面也要持续发送steam hello
            SteamConnector.onlySendHello();
            return;
        }
        //先渲染背景再渲染panel
        panelBackground.render(sb);
        steamFriendPanel.render(sb);
        //渲染返回按钮
        backButton.render(sb);
        //如果现在有要连接的对象，就执行连接过程的更新逻辑
        if(this.waitingFriend!=null &&
                SteamConnector.sendSteamHello())
        {
            //这个时候说明连接成功了，退出等待
            //MeunScreenFadeout.connectOk = true;
            this.configPage = new ConfigPage();
            //取消临时退出的按钮
            this.backButton.disabled = true;
        }
    }

}
