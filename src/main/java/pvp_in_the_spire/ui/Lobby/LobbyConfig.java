package pvp_in_the_spire.ui.Lobby;

import pvp_in_the_spire.ui.*;
import pvp_in_the_spire.ui.Button.ReadyButton;
import pvp_in_the_spire.ui.Button.ReadyButtonCallback;
import pvp_in_the_spire.ui.Button.WithUpdate.BaseUpdateButton;
import pvp_in_the_spire.ui.Chat.ChatFoldPage;
import pvp_in_the_spire.ui.Events.*;
import pvp_in_the_spire.ui.configOptions.*;
import pvp_in_the_spire.AutomaticSocketServer;
import pvp_in_the_spire.GameManager;
import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.room.FriendManager;
import pvp_in_the_spire.SocketServer;
import pvp_in_the_spire.actions.ConfigProtocol;
import pvp_in_the_spire.character.CharacterInfo;
import pvp_in_the_spire.helpers.FontLibrary;
import pvp_in_the_spire.network.Lobby.LobbyManager;
import pvp_in_the_spire.network.SteamConnector;
import pvp_in_the_spire.patches.RenderPatch;
import pvp_in_the_spire.patches.connection.InputIpBox;
import pvp_in_the_spire.patches.connection.MeunScreenFadeout;
import pvp_in_the_spire.patches.steamConnect.SteamManager;
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

//lobby的设置界面
public class LobbyConfig extends AbstractPage
        implements UpdateCharacter,
        ConfigChangeEvent,
        ReadyButtonCallback,
        ClickCallback,
        MemberChangeEvent //处理房间人员变化的回调
{

    //类实体，这是做实验用的，正常游戏不用这个
    public static LobbyConfig instance;

    public static final UIStrings uiStrings =
            CardCrawlGame.languagePack.getUIString("LobbyScreen");

    //显示各种配置选项的面板
    BasePanel configPanel;
    //配置选项的背景板
    PlainBox panelBackground;
    //一个用于渲染的角色
    CharacterBox characterBox;

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

    //对方选用的角色
    public static AbstractPlayer.PlayerClass oppositeCharacter = null;
    //对方的版本号
    public static String oppositeVersionInfo = null;
    //用于渲染对方角色的page
    public CharacterBox oppositeBox = null;

    //返回按钮
    public BaseUpdateButton backButton;
    //关闭页面时的回调函数
    public ClosePageEvent closePageEvent = null;

    //对方的准备标志
    public PlainBox oppositeReadyLabel;
    //对方是否已经准备完成
    public boolean oppositeReadyFlag = false;

    //我方的准备按钮
    public ReadyButton readyButton;

    public VersionText myVersionText;
    public VersionText oppositeVersionText;
    //对方的名字
    public VersionText oppositeName;

    //所有需要被添加的config列表
    public ArrayList<AbstractConfigOption> optionList = new ArrayList<>();

    //向左右选人的按钮
    public BaseUpdateButton leftButton;
    public BaseUpdateButton rightButton;

    //游戏角色的列表
    public ArrayList<AbstractPlayer.PlayerClass> classArrayList;
    //当前正在选择的角色列表
    public int currentCharacter = 0;

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
        //判断已有的信息里面是否重复
        if(this.characterBox != null &&
                SocketServer.oppositeCharacter != null&&
            SocketServer.oppositeCharacter.getPlayerClass() == playerClass)
        {
            return;
        }
        //记录敌方的角色信息
        SocketServer.oppositeCharacter = new CharacterInfo(playerClass);
        //播放选择角色时的音效
        SocketServer.oppositeCharacter.playPlayerSound();
        //初始化对方角色的box
        oppositeBox = new CharacterBox(Settings.WIDTH*(1-X_PADDING),
                Settings.HEIGHT*Y_PADDING,SocketServer.oppositeCharacter);
        oppositeBox.flipHorizontal = true;
        //显示对方的名字
        this.oppositeName.text = SocketServer.oppositeName;
        //初始化对方的版本信息
        oppositeVersionText.text = versionInfo;
        //判断两边的版本是否一致
        if(!myVersionText.text.equals(oppositeVersionText.text))
        {
            myVersionText.color = Color.RED;
            oppositeVersionText.color = Color.RED;
        }
    }

    //获取目前角色的class
    AbstractPlayer.PlayerClass getCurrentPlayerClass()
    {
        return classArrayList.get(currentCharacter);
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
            this.oppositeReadyFlag = (readInfo==1);
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
        //指定自己选中的角色
        CardCrawlGame.chosenCharacter = this.getCurrentPlayerClass();
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
        if(this.readyButton.readyFlag && this.oppositeReadyFlag)
        {
            enterGame();
        }
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
        //直接发送我方角色就可以了
        AutomaticSocketServer server = AutomaticSocketServer.getServer();
        this.sendMyCharacter(server.streamHandle,this.getCurrentPlayerClass());
        server.send();
    }

    @Override
    public void receiveToggleChange(int idToggle, boolean stage) {

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
            //发送自己的版本号
            streamHandle.writeUTF(myVersionText.text);
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

    //更新当前显示的角色
    public void updateChosenCharacter()
    {
        //新建character info
        CharacterInfo tempInfo = new CharacterInfo(getCurrentPlayerClass());
        //播放选择角色时的音效
        tempInfo.playPlayerSound();
        this.characterBox.updateCharacter(tempInfo);
        if(this.networkStage == 1)
        {
            AutomaticSocketServer server = AutomaticSocketServer.getServer();
            //通知对方更新角色信息
            sendMyCharacter(server.streamHandle,this.getCurrentPlayerClass());
            server.send();
        }
    }

    //点击事件
    @Override
    public void clickEvent(BaseUpdateButton button) {
        //判断是不是点击向左
        if(button == leftButton)
        {
            this.currentCharacter = (this.currentCharacter - 1 +
                this.classArrayList.size())%this.classArrayList.size();
            //更新当前显示的角色
            updateChosenCharacter();
        }
        else if (button == rightButton) {
            this.currentCharacter = (this.currentCharacter + 1)%this.classArrayList.size();
            //更新当前显示的角色
            updateChosenCharacter();
        }
        else if(button == this.backButton && this.closePageEvent != null)
        {
            //调用离开房间
            LobbyManager.leaveRoom();
            //取消p2p连接
            AutomaticSocketServer.globalServer = null;
            //调用关闭页面
            this.closePageEvent.closePageEvent(this);
        }
    }

    //取消显示对方的角色信息
    public void removeOppositeCharacter()
    {
        oppositeBox = null;
        oppositeName.text = null;
        oppositeVersionText.text = null;
        //取消socket里面的角色信息
        SocketServer.oppositeCharacter = null;
    }


    //把准备按钮重置成不可用的状态
    public void resetReadyButton()
    {
        this.readyButton.readyFlag = false;
        this.readyButton.disabled = true;
        this.readyButton.updateButtonText();
        this.oppositeReadyFlag = false;
        //同时设置成所有按钮可交互
        this.pressReady(false);
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
        //初始化目标的array list
        this.classArrayList = new ArrayList<>();
        //遍历所有的character
        for(AbstractPlayer eachPlayer : CardCrawlGame.characterManager.getAllCharacters())
        {
            //在列表里面添加对应的操作
            this.classArrayList.add(eachPlayer.chosenClass);
        }
    }

    //适配了ip子类的初始化
    public LobbyConfig()
    {
        this(null);
    }


    public LobbyConfig(AbstractPlayer.PlayerClass playerClass)
    {
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
        //初始化用于渲染的角色
//        characterBox = new CharacterBox(Settings.WIDTH*X_PADDING,
//            Settings.HEIGHT*Y_PADDING, CardCrawlGame.chosenCharacter);
        if(playerClass == null)
            playerClass = this.classArrayList.get(0);
        else {
            for(int i=0;i<classArrayList.size();++i)
            {
                if(classArrayList.get(i).equals(playerClass))
                {
                    this.currentCharacter = i;
                    break;
                }
            }
        }
        characterBox = new CharacterBox(Settings.WIDTH*X_PADDING,
                Settings.HEIGHT*Y_PADDING, new CharacterInfo(playerClass));
        //初始化左右的按钮
        this.leftButton = new BaseUpdateButton(Settings.WIDTH*0.04f,
                Settings.HEIGHT*0.73f,
                Settings.WIDTH * 0.07f,
                Settings.HEIGHT * 0.07f,
                "",
                FontLibrary.getBaseFont(),
                ImageMaster.CF_LEFT_ARROW,
                this
        );
        this.rightButton = new BaseUpdateButton(
                Settings.WIDTH * 0.18f,
                Settings.HEIGHT*0.73f,
                Settings.WIDTH * 0.07f,
                Settings.HEIGHT * 0.07f,
                "",
                FontLibrary.getBaseFont(),
                ImageMaster.CF_RIGHT_ARROW,
                this
        );
        //判断是否已经有可用的对方角色形象
        if(oppositeCharacter!=null)
        {
            updateCharacter(oppositeCharacter,oppositeVersionInfo);
        }
        //设置自己的回调结果
        ConfigProtocol.characterCallback = this;
        ConfigProtocol.configChangeCallback = this;
        //初始化对方是否已经准备完成
        this.oppositeReadyLabel = new PlainBox(READY_BUTTON_WIDTH,READY_BUTTON_HEIGHT,Color.WHITE);
        this.oppositeReadyLabel.x = Settings.WIDTH*(1-X_PADDING);
        this.oppositeReadyLabel.y = Settings.HEIGHT*READY_Y_PADDING;
        this.oppositeReadyLabel.texture = TextureManager.READY_TEXTURE;
        //初始化我方的准备按钮
        this.readyButton = new ReadyButton(Settings.WIDTH*0.1f,
                Settings.HEIGHT*READY_Y_PADDING,READY_BUTTON_WIDTH,READY_BUTTON_HEIGHT,
                InputIpBox.generateFont(20));
        //在最开始的时候把它设置为不可点击
        this.readyButton.disabled = true;
        //我方的版本号
        this.myVersionText = new VersionText(Settings.WIDTH*0.13f,
                Settings.HEIGHT*VERSION_PADDING,
                InputIpBox.generateFont(20));
        //对方的版本号
        this.oppositeVersionText = new VersionText(Settings.WIDTH*(1-X_PADDING),
                Settings.HEIGHT*VERSION_PADDING,
                InputIpBox.generateFont(20));
        //对方的名字
        this.oppositeName = new VersionText(Settings.WIDTH*(1-X_PADDING),
                Settings.HEIGHT*NAME_PADDING,
                InputIpBox.generateFont(20));
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
        //默认不显示对方的版本号
        this.oppositeVersionText.text = null;
        this.oppositeName.text = null;
        this.readyButton.readyButtonCallback = this;
        initConfigOption();
        InputHelper.initialize();
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
        //取消对方的角色框
        oppositeCharacter = null;
        SocketServer.oppositeCharacter = null;
        oppositeBox = null;
        oppositeVersionText.text = null;
        oppositeName.text = null;
        //取消对方的准备状态
        oppositeReadyFlag = false;
        //把自己的准备按钮也设置为不可点击
        this.readyButton.readyFlag = false;
        this.readyButton.updateButtonText();
        this.readyButton.disabled = true;
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
                //打开准备按钮
                this.readyButton.disabled = false;
                //把自己的信息置为配置页面的回调
                ConfigProtocol.configChangeCallback = this;
                ConfigProtocol.characterCallback = this;
                System.out.println("sending my character");
                //发送我方角色
                AutomaticSocketServer server = AutomaticSocketServer.getServer();
                sendMyCharacter(server.streamHandle,getCurrentPlayerClass());
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
                //判断自己是不是还不知道对方的信息
                if(oppositeBox == null)
                {
                    AutomaticSocketServer server = AutomaticSocketServer.getServer();
                    this.requestOppositeCharacter(server.streamHandle);
                    server.send();
                }
            }
        }
    }


    @Override
    public void update() {
        //调用渲染列表的更新
        configPanel.update();
        //更新左右按钮
        this.leftButton.update();
        this.rightButton.update();
        //更新网络信息
        this.networkUpdate();
        //更新返回按钮
        this.backButton.update();
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
        //判断是否需要渲染对方的准备状态
        if(this.oppositeReadyFlag)
        {
            this.oppositeReadyLabel.render(sb);
        }
        //渲染我方的准备按钮
        this.readyButton.render(sb);
        //渲染人物
        characterBox.render(sb);
        //判断是否有对面的人物需要渲染
        if(oppositeBox!=null)
        {
            oppositeBox.render(sb);
        }
        //渲染我方版本号和对方版本号
        myVersionText.render(sb);
        oppositeVersionText.render(sb);
        //渲染对方的名字
        oppositeName.render(sb);
        //渲染左右按钮
        this.leftButton.render(sb);
        this.rightButton.render(sb);
        this.backButton.render(sb);
        //渲染聊天框
        ChatFoldPage.getInstance().render(sb);
    }
}
