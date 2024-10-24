package pvp_in_the_spire.network;

import java.io.DataInputStream;

//当接收到消息时的触发回调函数
//fight协议和config协议会继承这个东西
public interface MessageTriggerInterface {

    public void triggerMessage(DataInputStream stream);

}
