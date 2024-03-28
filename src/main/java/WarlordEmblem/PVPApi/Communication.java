package WarlordEmblem.PVPApi;

import WarlordEmblem.AutomaticSocketServer;
import WarlordEmblem.GlobalManager;
import WarlordEmblem.actions.FightProtocol;
import WarlordEmblem.powers.PowerMapping;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

//允许用户在这里添加自定义的通信事件
public class Communication {
    //在事件列表里面注册信息
    //返回true表示添加成功
    public static boolean registerEvent(BaseEvent event)
    {
        //原始的记录表
        HashMap<String,BaseEvent> eventMap = GlobalManager.eventMap;
        if(eventMap.containsKey(event.eventId))
            return false;
        eventMap.put(event.eventId,event);
        return true;
    }

    //发送event
    public static void sendEvent(BaseEvent event)
    {
        //获取输入流
        AutomaticSocketServer server = AutomaticSocketServer.getServer();
        //写入用户事件的事件头
        DataOutputStream stream = server.streamHandle;
        try
        {
            stream.writeInt(FightProtocol.CUSTOM_EVENT);
            //写入事件id
            stream.writeUTF(event.eventId);
            System.out.println(event.eventId);
            //调用事件信息进行编码
            event.encode(stream);
            //发送消息
            server.send();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //注册power映射
    public static boolean registerPowerMapping(String powerId,PowerCreate creator)
    {
        //power的映射表
        HashMap<String,PowerCreate> creatorMapping = PowerMapping.creatorMapper;
        if(creatorMapping.isEmpty())
        {
            PowerMapping.initCreatorMapper();
        }
        //判断是否已经存在
        if(creatorMapping.containsKey(powerId))
            return false;
        //添加映射关系
        creatorMapping.put(powerId,creator);
        return true;
    }
}
