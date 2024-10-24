package pvp_in_the_spire.screens.Buttons;

import pvp_in_the_spire.ui.Events.ConnectOkEvent;
import pvp_in_the_spire.AutomaticSocketServer;
import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.screens.UserButton;
import pvp_in_the_spire.screens.WarningText;
import pvp_in_the_spire.SocketServer;
import pvp_in_the_spire.network.NoPauseWaitConnection;
import pvp_in_the_spire.patches.connection.MeunScreenFadeout;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

//根据当前的ip地址做连接
public class ConnectButton extends UserButton {

    public StringBuilder inputResult;

    //警告信息，当连接失败的时候触发这个警告信息
    public WarningText warningText;

    //连接成功时的回调接口
    public ConnectOkEvent callbackEvent = null;

    public ConnectButton(float x, float y, float width, float height,
         BitmapFont font,StringBuilder inputResult)
    {
        super(x,y,width,height,"connect",font);
        this.inputResult = inputResult;

        //初始化警告信息
        warningText = new WarningText("connection failed",
                FontHelper.largeCardFont,(float) Settings.WIDTH/2,(float) Settings.HEIGHT/4,
                Color.RED);
    }

    //在render里面渲染警告信息，虽然警告可能不用渲染
    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        warningText.render(sb);
    }

    public class IpPortInfo
    {
        //纯粹的ip地址
        public String pureIp;
        //端口号
        public int idPort = 0;

        public  IpPortInfo(String totalStr)
        {
            //判断有没有: 如果没有的话直接按默认的算法
            if(totalStr.contains(":"))
            {
                //按照冒号做区分
                String[] strList = totalStr.split(":");
                //记录前半部分
                pureIp = strList[0];
                //记录后面的数字
                idPort = Integer.valueOf(strList[1]);
            }
            else {
                //把端口号记录成默认的
                idPort = NoPauseWaitConnection.DEFAULT_PORT + GlobalManager.idGame;
                //整个区域都是ip
                pureIp = totalStr;
            }
        }

    }

    //初始化server
    public void initGlobalServer(Socket socket)
    {
        AutomaticSocketServer.globalServer = new AutomaticSocketServer(socket);
    }

    //点击事件，当发生点击的时候和对方做连接
    @Override
    public void clickEvent() {
        //根据已经输入好的ip和对方做连接
        try
        {
            //测试和目标的连接，但如果没连接上就什么都不做
            Socket testSocket = new Socket();
            //从输入的字符串里面解析端口号和ip
            IpPortInfo tempInfo = new IpPortInfo(inputResult.toString());
            testSocket.connect(new InetSocketAddress(tempInfo.pureIp,tempInfo.idPort), 30);
            initGlobalServer(testSocket);
            //判断是否有回调函数需要处理
            if(callbackEvent!=null)
            {
                callbackEvent.connectOk(false);
            }
            else {
                //通知连接完成
                MeunScreenFadeout.connectOk = true;
                //记录当前的时间
                SocketServer.beginGameTime = System.currentTimeMillis();
            }
        }
        catch (IOException e)
        {
            //如果连接失败，就弹出连接失败的信息
            warningText.idFrame = 0;
        }
    }
}
