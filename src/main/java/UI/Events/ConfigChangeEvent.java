package UI.Events;

import WarlordEmblem.SocketServer;

import java.io.DataInputStream;

//当某一个config选项发生变化的时候需要回调的函数
public interface ConfigChangeEvent {

    public void receiveConfigChange(DataInputStream streamHandle);

    //更新准备状态的接口
    public void updateReadyStage(DataInputStream streamHandle);

}
