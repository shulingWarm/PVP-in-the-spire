package pvp_in_the_spire.patches.connection;

import pvp_in_the_spire.AutomaticSocketServer;
import pvp_in_the_spire.screens.Buttons.AsServerButton;
import pvp_in_the_spire.screens.Buttons.ConnectButton;
import pvp_in_the_spire.SocketServer;
import pvp_in_the_spire.SteamSocketServer;
import pvp_in_the_spire.actions.FightProtocol;
import pvp_in_the_spire.patches.steamConnect.SteamManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.codedisaster.steamworks.SteamID;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.LocalizedStrings;

import java.io.IOException;

//一个独立的界面类
public class InputIpBox {

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
    //是否已经发送过steam刚刚连接上时的打招呼信息
    public boolean sendSteamHelloFlag=false;
    //steam发送打招呼信息的时间
    public long sendHelloTime=0;

    //自制的一个产生字体的函数
    public static BitmapFont generateFont(int size)
    {
        //供系统使用的字体，但仅限于中文的情况
        FileHandle fontFile = Gdx.files.internal("font/zhs/NotoSansMonoCJKsc-Regular.otf");
        //字体生成器
        FreeTypeFontGenerator g = new FreeTypeFontGenerator(fontFile);

        if (Settings.BIG_TEXT_MODE) {
            size *= 1.2F;
        }
        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
        p.characters = "";
        p.incremental = true;
        p.size = Math.round(size * Settings.scale);
        p.gamma = 0.9F;
        p.spaceX = 0;
        p.spaceY = 0;
        p.borderColor = new Color(0.35F, 0.35F, 0.35F, 1.0F);
        p.borderStraight = false;
        p.borderWidth = 2.0F * Settings.scale;
        p.borderGamma = 0.9F;
        p.shadowColor = new Color(0.0F, 0.0F, 0.0F, 0.25F);
        p.shadowOffsetX = Math.round(3.0F * Settings.scale);
        p.shadowOffsetY = Math.round(3.0F * Settings.scale);
        p.minFilter = Texture.TextureFilter.Linear;
        p.magFilter = Texture.TextureFilter.Linear;

        g.scaleForPixelHeight(p.size);
        BitmapFont font = g.generateFont(p);
        font.setUseIntegerPositions(false);
        font.getData().markupEnabled = true;
        if (LocalizedStrings.break_chars != null) {
            font.getData().breakChars = LocalizedStrings.break_chars.toCharArray();
        }

        font.getData().fontFile = fontFile;
        return font;
    }

    public InputIpBox()
    {
        background = new Color(1.0F, 0.965F, 0.886F,0.95F);
        //金色的字体，按照输入名字的框那样抄来的
        fontColor = Settings.GOLD_COLOR.cpy();
        textColor = new Color(1,1,1,1);
        //令它等于牌面上的字体，但放大一些
        if(titleFont==null)
        {
            titleFont = generateFont(60);
        }
        //初始化连接至对方的按钮
        buttonForConnection = new ConnectButton(0.52F*Settings.WIDTH,
                0.35F*Settings.HEIGHT,0.22F*Settings.WIDTH,0.13F*Settings.HEIGHT,
                generateFont(40),ipInputProcessor.inputResult);
        //初始化用于实验的按钮
        button = new AsServerButton(0.27F*Settings.WIDTH,0.35F*Settings.HEIGHT,
                0.22F*Settings.WIDTH,0.13F*Settings.HEIGHT,
                generateFont(40),ipInputProcessor.inputResult,buttonForConnection);
    }

    //初次打开时调用的操作，令输入ip的逻辑接管当前的输入框
    public void open()
    {
        //令处理ip的逻辑接管输入
        Gdx.input.setInputProcessor(ipInputProcessor);
    }

    //steam初始化相关的操作
    public void initSteamConnect(SteamID steamID)
    {
        try
        {
            //判断是否已经初始化过连接信息
            if(!sendSteamHelloFlag)
            {
                //初始化server里面的信息
                SteamSocketServer socketServer = new SteamSocketServer(steamID);
                AutomaticSocketServer.globalServer = socketServer;
                sendSteamHelloFlag=true;
                //发送基本的打招呼信息
                socketServer.streamHandle.writeInt(FightProtocol.STEAM_HELLO);
                socketServer.send();
                //初始化发送hello的时间
                sendHelloTime=System.currentTimeMillis();
            }
            //判断是否有消息可以接收
            SocketServer tempServer = AutomaticSocketServer.getServer();
            if(tempServer.isDataAvailable())
            {
                //读取int数值，接收新的打招呼信息
                int msgTag=tempServer.inputHandle.readInt();
                //判断是不是打招呼的信息
                if(msgTag==FightProtocol.STEAM_HELLO)
                {
                    System.out.println("steam connect ok!");
                    //把信息标记成连接成功
                    MeunScreenFadeout.connectOk = true;
                    //记录当前的时间
                    SocketServer.beginGameTime = System.currentTimeMillis();
                    //给对方反馈一个打招呼的信息
                    tempServer.streamHandle.writeInt(FightProtocol.STEAM_HELLO);
                    tempServer.send();
                }
                else {
                    System.out.println("unexpected hello message!!!");
                }
            }
            //查看当前的时间
            long tempTime = System.currentTimeMillis();
            if(tempTime-sendHelloTime>5000)
            {
                //再次发送打招呼信息
                tempServer.streamHandle.writeInt(FightProtocol.STEAM_HELLO);
                tempServer.send();
                sendHelloTime = tempTime;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //对窗口的渲染
    public void render(SpriteBatch sb)
    {
        //在这里检查是否需要使用steam的连接接口，如果使用steam的连接接口，判断连接后就直接结束处理了
        if(SteamManager.judgeUseSteam())
        {
            //根据获取到的steamId做初始化的信息发送
            initSteamConnect(SteamManager.targetId);
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
    }

    //渲染输入框
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
}
