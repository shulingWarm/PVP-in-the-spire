package pvp_in_the_spire.network;

import java.io.DataInputStream;

//接收到ip通信的消息时的回调
public interface ReceiveInterface {

    //接收消息
    public void receiveMessage(DataInputStream stream,int idClient);

}
