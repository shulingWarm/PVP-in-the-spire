package pvp_in_the_spire.network;

import java.net.Socket;

//用户发来连接请求时的接口
public interface ClientConnectInterface {

    public void receiveConnection(Socket socket);

}
