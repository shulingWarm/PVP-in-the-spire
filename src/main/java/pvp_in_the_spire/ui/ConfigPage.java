package pvp_in_the_spire.ui;

import pvp_in_the_spire.ui.Button.ReadyButton;
import pvp_in_the_spire.ui.Button.ReadyButtonCallback;
import pvp_in_the_spire.ui.Chat.ChatFoldPage;
import pvp_in_the_spire.ui.Events.ConfigChangeEvent;
import pvp_in_the_spire.ui.Events.UpdateCharacter;
import pvp_in_the_spire.ui.configOptions.*;
import pvp_in_the_spire.AutomaticSocketServer;
import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.room.FriendManager;
import pvp_in_the_spire.SocketServer;
import pvp_in_the_spire.actions.ConfigProtocol;
import pvp_in_the_spire.character.CharacterInfo;
import pvp_in_the_spire.patches.RenderPatch;
import pvp_in_the_spire.patches.connection.InputIpBox;
import pvp_in_the_spire.patches.connection.MeunScreenFadeout;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

//游戏内容自定义的界面
public class ConfigPage extends AbstractPage implements UpdateCharacter, ConfigChangeEvent, ReadyButtonCallback {

    //显示各种配置选项的面板
    BasePanel configPanel;
    //配置选项的背景板
    PlainBox panelBackground;
    //一个用于渲染的角色
    CharacterBox characterBox;

    //x位置的偏移量
    //这个表示的是两边的角色显示时对应的位置
    public static final float X_PADDING = 0.2f;
    public static final float Y_PADDING = 0.5f;
    //准备按钮显示的x的位置
    public static final float READY_Y_PADDING = 0.4f;
    public static final float VERSION_PADDING = 0.35f;
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

    //对方的准备标志
    public PlainBox oppositeReadyLabel;
    //对方是否已经准备完成
    public boolean oppositeReadyFlag = false;

    //我方的准备按钮
    public ReadyButton readyButton;

    public VersionText myVersionText;
    public VersionText oppositeVersionText;

    //所有需要被添加的config列表
    public ArrayList<AbstractConfigOption> optionList = new ArrayList<>();

    @Override
    public void updateCharacter(AbstractPlayer.PlayerClass playerClass,String versionInfo) {
        System.out.println("construct char box");
        //记录敌方的角色信息
        SocketServer.oppositeCharacter = new CharacterInfo(playerClass);
        //初始化对方角色的box
        oppositeBox = new CharacterBox(Settings.WIDTH*(1-X_PADDING),
            Settings.HEIGHT*Y_PADDING,SocketServer.oppositeCharacter);
        oppositeBox.flipHorizontal = true;
        //初始化对方的版本信息
        oppositeVersionText.text = versionInfo;
        //判断两边的版本是否一致
        if(!myVersionText.text.equals(oppositeVersionText.text))
        {
            myVersionText.color = Color.RED;
            oppositeVersionText.color = Color.RED;
        }
    }

    @Override
    public void receiveCharacterRequest(DataInputStream streamHandle) {
        System.out.println("Config page should not get receiveCharacterRequest");
    }

    @Override
    public void receiveToggleChange(int idToggle, boolean stage) {

    }

    //接收所有config信息
    @Override
    public void receiveAllConfig(DataInputStream streamHandle) {
        System.out.println("Config page should not get all config info");
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

    //判断是否两边都准备了
    public void judgeAllReady()
    {
        if(this.readyButton.readyFlag && this.oppositeReadyFlag)
        {
            //判断是否有mod需要使用
            this.checkGlobalMods();
            MeunScreenFadeout.connectOk = true;
            //初始化友军管理器
            FriendManager.initGlobalManager();
            //这次是真的可以了，准备进入游戏
            RenderPatch.delayBox = new DelayBox();
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
            streamHandle.writeUTF(SocketServer.myName);
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
        //败者奖励的选项
        optionList.add(new LoserRewardOption(optionWidth));
        //遍历每个需要被添加的page
        for(int idPage=0;idPage<optionList.size();++idPage)
        {
            //当前要添加的page
            AbstractConfigOption currOption = optionList.get(idPage);
            //设置option的标号
            currOption.setOptionId(idPage);
            //添加到panel里面
            configPanel.addNewPage(currOption);
        }
    }

    public ConfigPage()
    {
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
        characterBox = new CharacterBox(Settings.WIDTH*X_PADDING,
                Settings.HEIGHT*Y_PADDING, new CharacterInfo(CardCrawlGame.chosenCharacter));
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
        this.readyButton = new ReadyButton(Settings.WIDTH*X_PADDING,
                Settings.HEIGHT*READY_Y_PADDING,READY_BUTTON_WIDTH,READY_BUTTON_HEIGHT,
                InputIpBox.generateFont(20));
        //我方的版本号
        this.myVersionText = new VersionText(Settings.WIDTH*X_PADDING,
                Settings.HEIGHT*VERSION_PADDING,
                InputIpBox.generateFont(20));
        //对方的版本号
        this.oppositeVersionText = new VersionText(Settings.WIDTH*(1-X_PADDING),
                Settings.HEIGHT*VERSION_PADDING,
                InputIpBox.generateFont(20));
        //默认不显示对方的版本号
        this.oppositeVersionText.text = null;
        this.readyButton.readyButtonCallback = this;
        SocketServer server = AutomaticSocketServer.getServer();
        sendMyCharacter(server.streamHandle,CardCrawlGame.chosenCharacter);
        server.send();
        initConfigOption();
        InputHelper.initialize();
    }

    @Override
    public void update() {
        //调用渲染列表的更新
        configPanel.update();
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
        //渲染聊天框
        ChatFoldPage.getInstance().render(sb);
        //每个渲染周期都要解析一下配置信息
        ConfigProtocol.readData(AutomaticSocketServer.getServer());
    }
}
