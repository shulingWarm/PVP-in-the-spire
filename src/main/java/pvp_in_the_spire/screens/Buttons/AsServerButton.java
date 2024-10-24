package pvp_in_the_spire.screens.Buttons;

import pvp_in_the_spire.ui.Events.ConnectOkEvent;
import pvp_in_the_spire.AutomaticSocketServer;
import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.screens.UserButton;
import pvp_in_the_spire.SocketServer;
import pvp_in_the_spire.network.NoPauseWaitConnection;
import pvp_in_the_spire.patches.connection.MeunScreenFadeout;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Enumeration;
import java.util.concurrent.ExecutionException;

//这是一个把本机作为server的button
//当发生点击事件的时候，在指定的目标字符串上显示ip地址
public class AsServerButton extends UserButton {

    //用于显示ip的字符串，但这是一个指针
    StringBuilder ipString;
    //连接对方的按钮，当当前的按钮按下的时候，把那个按钮禁用
    ConnectButton connectButton;
    //等待连接的button
    NoPauseWaitConnection connectTool=null;

    //连接成功时的回调接口
    public ConnectOkEvent callbackEvent=null;

    public AsServerButton(float x, float y, float width, float height,
           BitmapFont font,
           StringBuilder ipText, //当发生点击事件的时候，会把这个字符串改成本机的ip
          ConnectButton connectButton //和这个按钮链接的表示连接对方的按钮
   )
    {
        super(x,y,width,height,"set as server",font);
        //记录链接的字符串
        this.ipString = ipText;
        this.connectButton = connectButton;
    }

    //获得本机的ip地址
    public static String getLocalIp()
    {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp()) {
                    continue;  // 不是有效的网络接口或者是回环接口，跳过
                }

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while(addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr.isSiteLocalAddress()) {  // 判断是否是局域网地址
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "invalid connection";
    }

    //对按钮渲染时的操作，每个渲染周期都会检查一次有没有连接成功
    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if(connectTool!=null)
        {
            try
            {
                Socket socket = connectTool.accept();
                if(socket!=null)
                {
                    //用一个外部的socket初始化socketserver
                    AutomaticSocketServer.globalServer = new AutomaticSocketServer(socket);
                    //判断是否有回调接口需要处理
                    if(callbackEvent!=null)
                    {
                        callbackEvent.connectOk(true);
                    }
                    else {
                        //标记已经连接完成
                        MeunScreenFadeout.connectOk = true;
                        //记录当前的时间
                        SocketServer.beginGameTime = System.currentTimeMillis();
                    }
                }
            }
            catch (ExecutionException | InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    //从字符串里面解析端口号
    public static int parsePortFromInput(String usrInput)
    {
        if(usrInput.length()<6 && (!usrInput.isEmpty()))
        {
            int tempPort = Integer.parseInt(usrInput);
            if(tempPort>1023 && tempPort<50000)
            {
                return tempPort;
            }
        }
        return NoPauseWaitConnection.DEFAULT_PORT + GlobalManager.idGame;
    }

    //准备连接对方的服务器
    public void prepareConnectTool(int idPort)
    {
        //准备用于连接的句柄
        connectTool = new NoPauseWaitConnection(idPort);
    }


    //当按钮被点击的时候，把关联的ip设置成本机的ip
    @Override
    public void clickEvent() {
        //如果目前已经是等待状态了，那就改回原始的状态
        if(connectTool!=null)
        {
            //重新设置成可点击的状态
            connectTool.close();
            connectTool = null;
            this.text = "set as server";
            //允许使用给对方发送连接的操作
            connectButton.disabled = false;
        }
        else {
            //从目前的字符串里面解析出一个数字
            int idPort = parsePortFromInput(ipString.toString());
            //把ip地址换成本机的ip
            ipString.setLength(0);
            ipString.append(getLocalIp());
            ipString.append(":");
            ipString.append(idPort);
            System.out.printf("ip: %s\n",ipString);
            //把显示的按钮改成取消
            this.text = "cancel";
            //禁用发送数据给对方的按钮
            connectButton.disabled = true;
            prepareConnectTool(idPort);
        }
    }
}
