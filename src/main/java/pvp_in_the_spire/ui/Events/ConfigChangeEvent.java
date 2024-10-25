package pvp_in_the_spire.ui.Events;

import java.io.DataInputStream;

//当某一个config选项发生变化的时候需要回调的函数
public interface ConfigChangeEvent {

    public void receiveConfigChange(DataInputStream streamHandle);

    //更新准备状态的接口
    public void updateReadyStage(DataInputStream streamHandle);

    //接收所有信息的接口
    public void receiveAllConfig(DataInputStream streamHandle);

    //接收发送角色的请求
    public void receiveCharacterRequest(DataInputStream streamHandle);

    //toggle按钮更新时的触发事件
    public void receiveToggleChange(int idToggle,boolean stage);

}
