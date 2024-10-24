package pvp_in_the_spire;

import java.io.IOException;

//两个进程通信时需要用到的信息
public class NetworkManager {

    //发送回合结束的消息
    public static void sendEndTurnMessage(SocketServer server)
    {
        try
        {
            //给目标发送一个回合结束的消息
            server.streamHandle.writeInt(0);
            server.send();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
