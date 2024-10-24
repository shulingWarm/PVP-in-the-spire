package pvp_in_the_spire.actions;

import pvp_in_the_spire.ui.ConfigPage;
import pvp_in_the_spire.ui.Events.ConfigChangeEvent;
import pvp_in_the_spire.ui.Events.UpdateCharacter;
import pvp_in_the_spire.SocketServer;
import pvp_in_the_spire.network.MessageTriggerInterface;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

import java.io.DataInputStream;
import java.io.IOException;

//自定义配置界面的相关设置
public class ConfigProtocol implements MessageTriggerInterface {

    //双方角色更新的消息
    public static final int UPDATE_CHARACTER = 20001;
    //更新设置信息的数据头
    public static final int CHANGE_CONFIG = 20002;
    //选择准备的信息
    public static final int READY_INFO = 20003;
    //接收所有的信息
    public static final int ALL_CONFIG_INFO = 20004;
    //请求对方角色的数据头
    public static final int REQUEST_CHARACTER = 20005;

    //更新角色时的回调函数
    public static UpdateCharacter characterCallback = null;
    //更新设置时的回调函数
    public static ConfigChangeEvent configChangeCallback = null;

    //接收角色的更新信息
    public static void characterUpdateReceive(DataInputStream streamHandle)
    {
        //读取角色的种类
        try
        {
            String characterName = streamHandle.readUTF();
            //接收对方的版本号
            String versionInfo = streamHandle.readUTF();
            //接收对方的名字
            SocketServer.oppositeName = streamHandle.readUTF();
            //解析成class
            AbstractPlayer.PlayerClass playerClass = AbstractPlayer.PlayerClass.valueOf(characterName);
            //判断是否有回调函数
            if(characterCallback!=null)
            {
                characterCallback.updateCharacter(playerClass,versionInfo);
            }
            else {
                //在config页面里面记录这个类型
                ConfigPage.oppositeCharacter = playerClass;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void readData(DataInputStream stream)
    {
        try
        {
            //临时读取一个数据
            int tempData = stream.readInt();
            switch (tempData)
            {
                case UPDATE_CHARACTER:
                    characterUpdateReceive(stream);
                    break;
                case CHANGE_CONFIG:
                    configChangeCallback.receiveConfigChange(stream);
                    break;
                //准备状态的更新
                case READY_INFO:
                    configChangeCallback.updateReadyStage(stream);
                    break;
                //所有的config数据
                case ALL_CONFIG_INFO:
                    configChangeCallback.receiveAllConfig(stream);
                    break;
                //请求发送角色数据
                case REQUEST_CHARACTER:
                    configChangeCallback.receiveCharacterRequest(stream);
                    break;
                case FightProtocol.CUSTOM_EVENT:
                    FightProtocol.handleCustomEvent(stream);
                    break;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    //处理自定义配置阶段的相关信息
    public static void readData(SocketServer server)
    {
        if(!server.isDataAvailable())
        {
            return;
        }
        readData(server.inputHandle);
    }

    @Override
    public void triggerMessage(DataInputStream stream) {
        //当接收到消息时，需要调用静态的消息处理函数
        readData(stream);
    }
}
