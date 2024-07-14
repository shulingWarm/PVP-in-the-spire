package UI.ConfigPageModules;

import UI.*;
import UI.Button.ReadyButton;
import UI.Button.ReadyButtonCallback;
import UI.Button.WithUpdate.BaseUpdateButton;
import UI.Chat.ChatFoldPage;
import UI.Events.*;
import UI.configOptions.*;
import WarlordEmblem.AutomaticSocketServer;
import WarlordEmblem.Events.RegisterPlayerEvent;
import WarlordEmblem.GameManager;
import WarlordEmblem.GlobalManager;
import WarlordEmblem.PVPApi.Communication;
import WarlordEmblem.PlayerManagement.PlayerJoinInterface;
import WarlordEmblem.Room.FriendManager;
import WarlordEmblem.SocketServer;
import WarlordEmblem.actions.ConfigProtocol;
import WarlordEmblem.character.CharacterInfo;
import WarlordEmblem.helpers.FontLibrary;
import WarlordEmblem.network.Lobby.LobbyManager;
import WarlordEmblem.network.PlayerInfo;
import WarlordEmblem.network.SteamConnector;
import WarlordEmblem.patches.RenderPatch;
import WarlordEmblem.patches.connection.InputIpBox;
import WarlordEmblem.patches.connection.MeunScreenFadeout;
import WarlordEmblem.patches.steamConnect.SteamManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamMatchmaking;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

//有多个玩家的config页面
public class MultiplayerConfigPage extends AbstractPage
        implements UpdateCharacter,
        ConfigChangeEvent,
        ReadyButtonCallback,
        ClickCallback,
        MemberChangeEvent,
        PlayerJoinInterface
{

    public static final UIStrings uiStrings =
            CardCrawlGame.languagePack.getUIString("LobbyScreen");

    //显示各种配置选项的面板
    BasePanel configPanel;
    //配置选项的背景板
    PlainBox panelBackground;

    //x位置的偏移量
    //这个表示的是两边的角色显示时对应的位置
    public static final float X_PADDING = 0.15f;
    public static final float Y_PADDING = 0.5f;
    //准备按钮显示的x的位置
    public static final float READY_Y_PADDING = 0.4f;
    public static final float VERSION_PADDING = 0.35f;
    public static final float NAME_PADDING = 0.8f;
    //准备按钮的宽度
    public static final float READY_BUTTON_WIDTH = Settings.WIDTH * 0.1f;
    //准备按钮的高度
    public static final float READY_BUTTON_HEIGHT = Settings.HEIGHT * 0.1f;

    //用来渲染角色的控件
    public CharacterPanel characterPanel;

    //返回按钮
    public BaseUpdateButton backButton;
    //关闭页面时的回调函数
    public ClosePageEvent closePageEvent = null;

    //所有需要被添加的config列表
    public ArrayList<AbstractConfigOption> optionList = new ArrayList<>();

    //当前是否为发送hello阶段的标志 这里直接就用int来代表了
    //-1 是房间里只有自己时的状态
    //0 是房间里刚刚进来人，双方互相发送hello信息，这样才能确认通信正常
    //1 是双方都已经发送好了角色信息，可以开始处理配置信息了
    public int networkStage = 0;
    //房主的标志
    public boolean ownerFlag = false;
    //发送hello信息的计数，每过一段时间再发送一次
    public int sendHelloFrame = 10;

    @Override
    public void updateCharacter(AbstractPlayer.PlayerClass playerClass,String versionInfo) {
        System.out.printf("update: %s\n",playerClass.name());
        //记录敌方的角色信息
        SocketServer.oppositeCharacter = new CharacterInfo(playerClass);
        //播放选择角色时的音效
        SocketServer.oppositeCharacter.playPlayerSound();
    }

    public void receiveConfigChange(DataInputStream streamHandle)
    {
        //接收option的标号
        try
        {
            int idOption = streamHandle.readInt();
            //交给目标option来处理后续内容
            optionList.get(idOption).receiveConfigChange(streamHandle);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //接收所有配置信息
    @Override
    public void receiveAllConfig(DataInputStream streamHandle) {
        try{
            //option的个数
            int optionNum = streamHandle.readInt();
            //如果超过自己的option数量，就把它弄成较小的那个
            if(optionNum > optionList.size())
                optionNum = optionList.size();
            //遍历每个option
            for(int idOption = 0;idOption<optionNum;++idOption)
            {
                //当前的option
                AbstractConfigOption currentOption = optionList.get(idOption);
                currentOption.receiveConfigChange(streamHandle);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //更新准备状态的接口，当对面的准备状态变化时，在这里记录
    public void updateReadyStage(DataInputStream streamHandle)
    {
        try
        {
            //读取对方是否准备好了
            int readInfo = streamHandle.readInt();
            //更新双方都准备的消息
            judgeAllReady();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //判断是否有mod需要被开启
    public void checkGlobalMods()
    {
        if(!GlobalManager.enabledMods.isEmpty())
        {
            //添加mod
            ArrayList<String> modStrList = new ArrayList<>(GlobalManager.enabledMods);
            ModHelper.setMods(modStrList);
            //标记到时候会使用到mod
            GlobalManager.useModFlag = true;
        }
    }

    //即将进入游戏的逻辑
    public void enterGame()
    {
        //判断是否有mod需要使用
        this.checkGlobalMods();
        MeunScreenFadeout.connectOk = true;
        //初始化友军管理器
        FriendManager.initGlobalManager();
        //这次是真的可以了，准备进入游戏
        RenderPatch.delayBox = new DelayBox();
        //判断自己是不是房主，如果是房主的话，就把房间标记成消失
        if(this.ownerFlag)
        {
            LobbyManager.destroyRoom(LobbyManager.currentLobby.lobbyId);
        }
        //准备进入游戏
        GameManager.prepareEnterGame();
    }

    //判断是否两边都准备了
    public void judgeAllReady()
    {
    }

    //点击准备按钮时的回调函数
    public void pressReady(boolean readyFlag)
    {
        //遍历所有的option,把它设置成可交互或不可交互
        for(AbstractConfigOption eachOption : optionList)
        {
            eachOption.setEnable(!readyFlag);
        }
        judgeAllReady();
    }

    @Override
    public void receiveCharacterRequest(DataInputStream streamHandle) {

    }

    //发送获取对方角色的请求
    public void requestOppositeCharacter(DataOutputStream stream)
    {
        //发送请求信息的数据头
        try
        {
            stream.writeInt(ConfigProtocol.REQUEST_CHARACTER);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //获取我方的名字
    public String getMyName()
    {
        return SteamManager.getMyName();
    }

    //给对方发送自己的形象
    public void sendMyCharacter(DataOutputStream streamHandle,
                                AbstractPlayer.PlayerClass playerClass)
    {
        try
        {
            //发送数据头
            streamHandle.writeInt(ConfigProtocol.UPDATE_CHARACTER);
            streamHandle.writeUTF(playerClass.name());
            //发送自己的名字
            streamHandle.writeUTF(this.getMyName());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //发送自己的配置信息
    //遍历的过程中也就改成可以发消息了
    public void sendMyConfig(DataOutputStream stream)
    {
        try
        {
            //添加数据头
            stream.writeInt(ConfigProtocol.ALL_CONFIG_INFO);
            //添加当前的选择项的个数
            stream.writeInt(this.optionList.size());
            //遍历option里面的每一项
            for(AbstractConfigOption eachOption : optionList)
            {
                //获取当前的选择项
                stream.writeInt(eachOption.getCurrentSelect());
                //把当前选项设置为可更新
                eachOption.sendConfigChangeFlag = true;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //初始化page选项
    public void initConfigOption()
    {
        optionList.clear();
        //能用的option宽度
        float optionWidth = configPanel.width*0.8f;
        optionList.add(new TailNumSelect(optionWidth));
        optionList.add(new InvincibleRate(optionWidth));
        optionList.add(new AddConstConfig(optionWidth));
        //设置格挡增益比率
        optionList.add(new BlockGainConfig(optionWidth));
        //设置掉落金钱的比例
        optionList.add(new LoseGoldConfig(optionWidth));
        //选择初始牌组
        optionList.add(new StartDeckConfig(optionWidth));
        //设置初始金币
        optionList.add(new StartGoldConfig(optionWidth));
        //设置卡池
        optionList.add(new CardPoolOption(optionWidth));
        //设置地图资源层数
        optionList.add(new MapRowNumConfig(optionWidth));
        //注册添加小怪的选项
        optionList.add(new FriendMonsterConfig(optionWidth));
        //添加败者奖励配置
        optionList.add((new LoserRewardOption(optionWidth)));
        //遍历每个需要被添加的page
        for(int idPage=0;idPage<optionList.size();++idPage)
        {
            //当前要添加的page
            AbstractConfigOption currOption = optionList.get(idPage);
            //设置option的标号
            currOption.setOptionId(idPage);
            //添加到panel里面
            configPanel.addNewPage(currOption);
            //这里临时先禁止发送信息
            currOption.sendConfigChangeFlag = false;
        }
    }

    //点击事件
    @Override
    public void clickEvent(BaseUpdateButton button) {
        if(button == this.backButton && this.closePageEvent != null)
        {
            //调用离开房间
            LobbyManager.leaveRoom();
            //取消p2p连接
            AutomaticSocketServer.globalServer = null;
            //调用关闭页面
            this.closePageEvent.closePageEvent(this);
        }

    }


    //把准备按钮重置成不可用的状态
    public void resetReadyButton()
    {
    }

    //处理房间人员变化的回调
    @Override
    public void onMemberChanged(SteamID personId, SteamMatchmaking.ChatMemberStateChange memberStage) {
        //判断现在的状态是不是-1
        if(this.networkStage == -1 &&
                memberStage == SteamMatchmaking.ChatMemberStateChange.Entered)
        {
            System.out.println("member enter!!");
            //更新pvp的连接状态
            LobbyManager.initP2PConnection(personId);
            //把当前的状态更新成零，后面开始准备初始化pvp了
            this.networkStage = 0;
        }
        //如果是有玩家退出了，就移交房主
        else if(memberStage != SteamMatchmaking.ChatMemberStateChange.Entered)
        {
            //回退到刚加入房间的状态
            this.initNetworkStage(true,this.closePageEvent);
            //关闭准备按钮
            resetReadyButton();
        }
    }

    //初始化所有角色的列表
    public void initPlayerClassList()
    {

    }

    //申请team座位的逻辑
    public void requestSait()
    {
        //如果是房主的话，就直接给自己分配房间就可以了
        if(ownerFlag)
        {
            GlobalManager.playerManager.assignTeam(
                GlobalManager.myPlayerTag,0
            );
        }
        else {
            //申请自己的座位
            Communication.sendEvent(new RegisterPlayerEvent());
        }
    }

    public MultiplayerConfigPage(boolean isOwner)
    {
        this.ownerFlag = isOwner;
        //初始化所有角色的列表
        initPlayerClassList();
        configPanel = new BasePanel(
                Settings.WIDTH*0.3F,Settings.HEIGHT*0.1f,
                Settings.WIDTH*0.4F,Settings.HEIGHT*0.8f
        );
        panelBackground = new PlainBox(configPanel.width,
                configPanel.height, Color.valueOf("363D6799"));
        panelBackground.x = configPanel.x;
        panelBackground.y = configPanel.y;
        panelBackground.texture = ImageMaster.WHITE_SQUARE_IMG;
        //设置自己的回调结果
        ConfigProtocol.characterCallback = this;
        ConfigProtocol.configChangeCallback = this;
        //初始化返回按钮
        this.backButton = new BaseUpdateButton(
                0,
                Settings.HEIGHT * 0.2f,
                Settings.WIDTH * 0.1f,
                Settings.HEIGHT * 0.06f,
                uiStrings.TEXT[0],
                FontLibrary.getBaseFont(),
                TextureManager.BACK_BUTTON,
                this
        );
        //注册回调信息
        GlobalManager.playerManager.playerJoinInterface = this;
        //初始化渲染角色的控件
        this.characterPanel = new CharacterPanel();
        //给玩家管理器传递用于显示角色的两个grid
        GlobalManager.playerManager.initCharacterLayout(
            this.characterPanel.leftCharacters,
            this.characterPanel.rightCharacters
        );
        initConfigOption();
        InputHelper.initialize();
        //给本地玩家申请座位，或者说是直接注册座位
        requestSait();
    }

    //初始化网络状态
    public void initNetworkStage(boolean isOwner,
                                 ClosePageEvent closeCallback
    )
    {
        //如果是房主的话，一开始是不发送hello信息的
        if(isOwner)
            this.networkStage = -1;
        else
        {
            this.networkStage = 0;
        }
        //记录关闭页面的回调函数
        this.closePageEvent = closeCallback;
        SocketServer.oppositeCharacter = null;
        //记录是否为房主
        this.ownerFlag = isOwner;
        //在steam的回调函数里注册人员变化时的操作
        LobbyManager.callback.memberChangeEvent = this;
    }

    //网络状态更新
    public void networkUpdate()
    {
        --sendHelloFrame;
        //判断现在是不是等待打招呼的状态
        if(networkStage == 0 && sendHelloFrame <= 0)
        {
            sendHelloFrame = 50;
            //判断是否需要发送hello
            if(SteamConnector.sendSteamHello())
            {
                //将网络状态置为下一个状态，也就是监听状态
                this.networkStage = 1;
                //加快加载角色数据的过程
                this.sendHelloFrame = 10;
                //把自己的信息置为配置页面的回调
                ConfigProtocol.configChangeCallback = this;
                ConfigProtocol.characterCallback = this;
                System.out.println("sending my character");
                //发送我方角色
                AutomaticSocketServer server = AutomaticSocketServer.getServer();
                server.send();
                //判断自己是不是房主，如果是房主的话就同步给对方自己的所有配置信息
                if(this.ownerFlag)
                {
                    this.sendMyConfig(server.streamHandle);
                    server.send();
                }
            }
        }
        //判断网络状态是不是监听状态
        else if(this.networkStage == 1)
        {
            //调用config阶段的监听工作
            ConfigProtocol.readData(AutomaticSocketServer.getServer());
            //判断是不是到了一个重复发送hello的周期
            if(sendHelloFrame <= 0)
            {
                sendHelloFrame = 100;
                //发送一次hello信息，防止始终没有初始化连接成功
                SteamConnector.onlySendHello();
            }
        }
    }


    @Override
    public void update() {
        //调用渲染列表的更新
        configPanel.update();
        //更新网络信息
        this.networkUpdate();
        //更新返回按钮
        this.backButton.update();
        this.characterPanel.update();
        //更新聊天框
        ChatFoldPage.getInstance().update();
    }

    //渲染配置选项
    @Override
    public void render(SpriteBatch sb) {
        //渲染背景
        panelBackground.copyLocation(this);
        panelBackground.render(sb);
        //渲染列表
        configPanel.render(sb);
        this.backButton.render(sb);
        this.characterPanel.render(sb);
        //渲染聊天框
        ChatFoldPage.getInstance().render(sb);
    }

    //有玩家加入时的注册信息
    @Override
    public void registerPlayer(PlayerInfo player) {
        //如果我是房主，就安排分配一下这个player所属的team
        if(this.ownerFlag)
        {
            GlobalManager.playerManager.assignTeam(player);
        }
    }

    @Override
    public void setMainCharacter(AbstractPage page) {
        this.characterPanel.setMainCharacter(page);
    }
}
