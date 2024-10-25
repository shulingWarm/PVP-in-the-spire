package pvp_in_the_spire.network;

import pvp_in_the_spire.AutomaticSocketServer;
import pvp_in_the_spire.SocketServer;
import pvp_in_the_spire.SteamSocketServer;
import pvp_in_the_spire.actions.FightProtocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//steam好友的连接器，用于执行一些打招呼的信息
public class SteamConnector {

    //上次发送打招呼信息的时间
    public static long lastHelloTime = 0;

    public static void sendHelloMessage(DataOutputStream streamHandle)
    {
        try
        {
            streamHandle.writeInt(FightProtocol.STEAM_HELLO);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //判断是否可以收到对方的打招呼信息
    public static boolean getHelloMessage(DataInputStream streamHandle)
    {
        //消息里面应该只有一个int消息
        try
        {
            int infoHead = streamHandle.readInt();
            return infoHead == FightProtocol.STEAM_HELLO;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    //只发送打招呼信息，但不再处理接收信息
    public static void onlySendHello()
    {
        AutomaticSocketServer server = AutomaticSocketServer.globalServer;
        assert (server instanceof SteamSocketServer);
        //获取当前的时间
        long currTime = System.currentTimeMillis();
        //判断是否到了下一个发送周期
        if(currTime - lastHelloTime > 3000)
        {
            lastHelloTime = currTime;
            //给对方发送打招呼的信息
            sendHelloMessage(server.streamHandle);
            server.send();
        }
    }

    //根据上次发送的打招呼信息的时间间隔来发送，并且检测是否收到了打招呼的信息
    public static boolean sendSteamHello()
    {
        // System.out.println("sending steam hello!!!");
        AutomaticSocketServer server = AutomaticSocketServer.globalServer;
        assert (server instanceof SteamSocketServer);
        //获取当前的时间
        long currTime = System.currentTimeMillis();
        //判断是否到了下一个发送周期
        if(currTime - lastHelloTime > 3000)
        {
            lastHelloTime = currTime;
            //给对方发送打招呼的信息
            sendHelloMessage(server.streamHandle);
            server.send();
        }
        //尝试接收来自目标的打招呼信息
        while(server.isDataAvailable())
        {
            if(getHelloMessage(server.inputHandle))
            {
                //记录当前的时间，这个就是游戏开始的时间
                SocketServer.beginGameTime = System.currentTimeMillis();
                return true;
            }
        }
        return false;
    }

}
