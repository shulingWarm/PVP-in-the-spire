package pvp_in_the_spire.network;

import pvp_in_the_spire.SocketServer;

import java.io.IOException;

//两个玩家之间的连接逻辑处理器，但不一样的是
//服务端在等到连接之前也是非阻塞的
//再有一个另外的功能是，连接上之后这里会初始化地把两边的生命上限和形象同步一下
public class Connector {

    SocketServer server;

    public Connector(int idPort)
    {
        //先测试把自己作为用户端和对面做连接
        try
        {
            server = new SocketServer(idPort);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
