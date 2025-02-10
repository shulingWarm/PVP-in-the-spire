package pvp_in_the_spire.ui.ConfigPageModules;

import pvp_in_the_spire.ui.*;
import pvp_in_the_spire.ui.Button.WithUpdate.BaseUpdateButton;
import pvp_in_the_spire.ui.CardFilter.CardFilterScreen;
import pvp_in_the_spire.ui.Chat.ChatFoldPage;
import pvp_in_the_spire.ui.Events.*;
import pvp_in_the_spire.ui.configOptions.*;
import pvp_in_the_spire.*;
import pvp_in_the_spire.events.RegisterPlayerEvent;
import pvp_in_the_spire.events.ToggleTriggerEvent;
import pvp_in_the_spire.util.Pair;
import pvp_in_the_spire.pvp_api.Communication;
import pvp_in_the_spire.player_management.PlayerJoinInterface;
import pvp_in_the_spire.room.FriendManager;
import pvp_in_the_spire.actions.ConfigProtocol;
import pvp_in_the_spire.actions.FightProtocol;
import pvp_in_the_spire.helpers.FontLibrary;
import pvp_in_the_spire.network.Lobby.LobbyManager;
import pvp_in_the_spire.network.PlayerInfo;
import pvp_in_the_spire.patches.RenderPatch;
import pvp_in_the_spire.patches.connection.MeunScreenFadeout;
import pvp_in_the_spire.patches.steamConnect.SteamManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamMatchmaking;
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
        implements
        ConfigChangeEvent,
        ClickCallback,
        MemberChangeEvent,
        PlayerJoinInterface,
        ToggleInterface,
        ClosePageEvent
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
    //禁卡页面的按钮
    public BaseUpdateButton banCardButton;
    //关闭页面时的回调函数
    public ClosePageEvent closePageEvent = null;

    //子页面
    public AbstractPage subPage = null;

    //所有需要被添加的config列表
    public ArrayList<AbstractConfigOption> optionList = new ArrayList<>();
    //所有的toggle option的列表
    public ArrayList<Pair<ToggleOption,ToggleInterface>> toggleOptionList = new ArrayList<>();

    //当前是否为发送hello阶段的标志 这里直接就用int来代表了
    //-1 是房间里只有自己时的状态
    //0 是房间里刚刚进来人，双方互相发送hello信息，这样才能确认通信正常
    //1 是双方都已经发送好了角色信息，可以开始处理配置信息了
    public int networkStage = 0;
    //房主的标志
    public boolean ownerFlag = false;
    //发送hello信息的计数，每过一段时间再发送一次
    public int sendHelloFrame = 10;

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
            //Read toggle options
            int toggleNum = streamHandle.readInt();
            for(int idToggle=0;idToggle<toggleNum;++idToggle)
            {
                //Get current toggle option
                ToggleOption option = this.toggleOptionList.get(idToggle).first;
                boolean tempStage = streamHandle.readBoolean();
                option.setStage(tempStage);
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
        //指定当前选中的角色
        CardCrawlGame.chosenCharacter = GlobalManager.playerManager.selfPlayerInfo.getPlayerClass();
        //初始化友军管理器
        FriendManager.initGlobalManager();
        //判断自己是不是房主，如果是房主的话，就把房间标记成消失
        if(this.ownerFlag)
        {
            LobbyManager.destroyRoom(LobbyManager.currentLobby.lobbyId);
        }
        //准备战斗相关的协议
        GlobalManager.messageTriggerInterface = new FightProtocol();
        //准备进入游戏
        GameManager.prepareEnterGame();
        RenderPatch.delayBox = new DelayBox();
    }

    //判断是否两边都准备了
    public void judgeAllReady()
    {
    }

    @Override
    public void receiveCharacterRequest(DataInputStream streamHandle) {

    }

    @Override
    public void receiveToggleChange(int idToggle, boolean stage) {
        if(idToggle >= 0 && idToggle < toggleOptionList.size())
        {
            //获取toggle,更新它的状态
            ToggleOption toggleOption = toggleOptionList.get(idToggle).first;
            toggleOption.setStage(stage);
            //执行toggle的触发事件
            toggleOptionList.get(idToggle).second.triggerToggleButton(
                toggleOption.userToggle,idToggle,stage
            );
        }
    }

    //获取我方的名字
    public String getMyName()
    {
        return SteamManager.getMyName();
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
            //Send my toggle option number
            stream.writeInt(this.toggleOptionList.size());
            //Send all toggle option to new player
            for(Pair<ToggleOption,ToggleInterface> eachToggleOption : this.toggleOptionList)
            {
                //Record this option flag
                stream.writeBoolean(eachToggleOption.first.userToggle.enabled);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //注册新的toggle option
    public void registerToggleOption(String text,
         float optionWidth,
         ToggleInterface toggleInterface,
         boolean initStage
    )
    {
        ToggleOption option = new ToggleOption(0,0,text,optionWidth,
this,this.toggleOptionList.size());
        toggleOptionList.add(
            new Pair<>(option,toggleInterface)
        );
        configPanel.addNewPage(option);
        option.setStage(initStage);
    }

    //注册新的config选项
    public void registerConfigOption(AbstractConfigOption option){
        option.setOptionId(optionList.size());
        optionList.add(option);
        configPanel.addNewPage(option);
    }

    //添加文本
    public void addTextTitle(String title,float optionWidth)
    {
        //地主增益选项的标题文本
        configPanel.addNewPage(
                new TextLabel(0,0,optionWidth,
                        Settings.HEIGHT*0.04f,
                        title,FontLibrary.getBaseFont())
        );
    }

    //初始化page选项
    public void initConfigOption()
    {
        optionList.clear();
        //能用的option宽度
        float optionWidth = configPanel.width*0.8f;
        //添加用于显示禁卡页面的按钮
        this.banCardButton = new BaseUpdateButton(0,0,
                optionWidth/2,Settings.HEIGHT*0.06f,uiStrings.TEXT[12],
                FontLibrary.getBaseFont(),ImageMaster.PROFILE_SLOT,this);
        //把这个按钮添加进配置页面
        this.configPanel.addNewPage(banCardButton);
        registerConfigOption(new TailNumSelect(optionWidth));
        registerConfigOption(new InvincibleRate(optionWidth));
        registerConfigOption(new AddConstConfig(optionWidth));
        registerConfigOption(new BlockGainConfig(optionWidth));
        registerConfigOption(new LoseGoldConfig(optionWidth));
        registerConfigOption(new StartDeckConfig(optionWidth));
        registerConfigOption(new StartGoldConfig(optionWidth));
        registerConfigOption(new CardPoolOption(optionWidth));
        registerConfigOption(new MapRowNumConfig(optionWidth));
        registerConfigOption(new ConsoleEnableOption(optionWidth));
        //败者奖励的文本
        addTextTitle(uiStrings.TEXT[10],optionWidth);
        //败者boss遗物
        registerConfigOption(new LoserRewardOption(optionWidth));
        //金币奖励
        registerConfigOption(new LoserGoldOption(optionWidth));
        //地主的金卡选项
        registerToggleOption(uiStrings.TEXT[11],optionWidth,
            new LoserCardOption(),GlobalManager.loserCardFlag);

        //地主增益选项的标题
        //Title of benefit for team with less players
        addTextTitle(uiStrings.TEXT[5],optionWidth);

        //注册option
        registerToggleOption(uiStrings.TEXT[6],optionWidth,new FirstHandOption(),
            GlobalManager.landlordFirstHandFlag);
        //地主没有先手惩罚
        registerToggleOption(uiStrings.TEXT[7],optionWidth,
            new NoFirstPunishment(),GlobalManager.landlordNoPunishment);
        //令地主开局多获得一费
        registerToggleOption(uiStrings.TEXT[8],optionWidth,
                new LandlordMoreEnergy(),GlobalManager.landlordEnergyFlag);
        registerToggleOption(uiStrings.TEXT[9],optionWidth,
                new LandlordMoreTail(),GlobalManager.landlordMoreTail);
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
        else if(button == this.banCardButton)
        {
            CardFilterScreen.instance.setCloseCallback(this);
            CardFilterScreen.instance.button.show(uiStrings.TEXT[0]);
            this.subPage = CardFilterScreen.instance;
        }
    }


    //把准备按钮重置成不可用的状态
    public void resetReadyButton()
    {
    }

    //处理房间人员变化的回调
    @Override
    public void onMemberChanged(SteamID personId, SteamMatchmaking.ChatMemberStateChange memberStage) {
        if (memberStage == SteamMatchmaking.ChatMemberStateChange.Entered) {
            //在通信内容里面注册这个玩家
            LobbyChatServer.instance.registerPlayer(personId);
        } else if (memberStage == SteamMatchmaking.ChatMemberStateChange.Disconnected)
        // 如果只是断连了，那就不管，靠steamNetwork自己重连
        {
        } else
        //如果是有玩家退出了，就移交房主
        {
            //回退到刚加入房间的状态
            this.initNetworkStage(LobbyManager.amIOwner(),this.closePageEvent);
            //移除这个玩家
            LobbyChatServer.instance.removePlayer(personId);
            //关闭准备按钮
            resetReadyButton();
            //移除该玩家
            GlobalManager.playerManager.onPlayerLeave(personId.getAccountID());
        }
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
        configPanel = new BasePanel(
                Settings.WIDTH*0.3F,Settings.HEIGHT*0.1f,
                Settings.WIDTH*0.4F,Settings.HEIGHT*0.8f
        );
        panelBackground = new PlainBox(configPanel.width,
                configPanel.height, Color.valueOf("363D6799"));
        panelBackground.x = configPanel.x;
        panelBackground.y = configPanel.y;
        panelBackground.texture = ImageMaster.WHITE_SQUARE_IMG;
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
        GlobalManager.playerManager.selfPlayerInfo.setLobbyOwner(isOwner);
        //初始化渲染角色的控件
        this.characterPanel = new CharacterPanel();
        initConfigOption();
    }

    //刚刚打开页面时的操作
    @Override
    public void open() {
        //给玩家管理器传递用于显示角色的两个grid
        GlobalManager.playerManager.initCharacterLayout(
                this.characterPanel.leftCharacters,
                this.characterPanel.rightCharacters
        );
        InputHelper.initialize();
        //登记config信息的处理
        GlobalManager.messageTriggerInterface = new ConfigProtocol();
        //给本地玩家申请座位，或者说是直接注册座位
        requestSait();
    }

    //初始化网络状态
    public void initNetworkStage(boolean isOwner,
                                 ClosePageEvent closeCallback
    )
    {
        //记录关闭页面的回调函数
        this.closePageEvent = closeCallback;
        //把自身设置为owner
        GlobalManager.playerManager.selfPlayerInfo.setLobbyOwner(isOwner);
        //记录是否为房主
        this.ownerFlag = isOwner;
        //在steam的回调函数里注册人员变化时的操作
        LobbyManager.callback.memberChangeEvent = this;
    }

    //网络状态更新
    public void networkUpdate()
    {
        ConfigProtocol.readData(AutomaticSocketServer.getServer());
    }

    @Override
    public void update() {
        //更新网络信息
        this.networkUpdate();
        if(this.subPage == null)
        {
            //调用渲染列表的更新
            configPanel.update();
            //更新返回按钮
            this.backButton.update();
            this.characterPanel.update();
        }
        else {
            this.subPage.update();
        }
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
        if(this.subPage != null)
        {
            this.subPage.render(sb);
        }
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
            //发送自己的全部配置信息
            AutomaticSocketServer server = AutomaticSocketServer.getServer();
            sendMyConfig(server.streamHandle);
            server.send();
            CardFilterScreen.instance.cardFilter.sendBanCardStage();
        }
    }

    @Override
    public void setMainCharacter(AbstractPage page) {
        this.characterPanel.setMainCharacter(page);
    }

    @Override
    public void triggerToggleButton(UserToggle toggle, int id, boolean stage) {
        if(id >= 0 && id<toggleOptionList.size())
        {
            //如果是通过点击触发的，就调用一下通信逻辑
            Communication.sendEvent(new ToggleTriggerEvent(id,stage));
            toggleOptionList.get(id).second.triggerToggleButton(toggle,id,stage);
        }
    }

    @Override
    public void closePageEvent(AbstractPage page) {
        //判断是否为子页面请求关闭
        if(page == this.subPage)
            this.subPage = null;
    }
}
