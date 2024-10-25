package pvp_in_the_spire.pvp_api;

import pvp_in_the_spire.AutomaticSocketServer;
import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.actions.FightProtocol;
import pvp_in_the_spire.powers.PowerMapping;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

//允许用户在这里添加自定义的通信事件
public class Communication {
    //在事件列表里面注册信息
    //返回true表示添加成功
    public static boolean registerEvent(BaseEvent event)
    {
        //原始的记录表
        HashMap<String,BaseEvent> eventMap = GlobalManager.eventMap;
        ArrayList<BaseEvent> eventList = GlobalManager.eventList;
        if(eventMap.containsKey(event.eventId))
            return false;
        eventMap.put(event.eventId,event);
        //记录事件在列表中的id
        event.listId = eventList.size();
        //在列表里面也记录这个事件
        eventList.add(event);
        return true;
    }

    //发送event
    public static void advanceSendEvent(BaseEvent event,int playerTag)
    {
        if(AutomaticSocketServer.globalServer == null)
            return;
        //如果是未注册过的事件，不做处理
        HashMap<String,BaseEvent> eventMap = GlobalManager.eventMap;
        if(!eventMap.containsKey(event.eventId))
        {
            System.out.printf("Unknown event: %s\n",event.eventId);
            return;
        }
        //获取输入流
        AutomaticSocketServer server = AutomaticSocketServer.getServer();
        //写入用户事件的事件头
        DataOutputStream stream = server.streamHandle;
        try
        {
            stream.writeInt(FightProtocol.CUSTOM_EVENT);
            //写入事件id
            stream.writeInt(eventMap.get(event.eventId).listId);
            //调用事件信息进行编码
            event.encode(stream);
            //发送消息
            if(playerTag == -1)
                server.send();
            else
                server.targetSend(playerTag);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //发送event
    public static void sendEvent(BaseEvent event)
    {
        advanceSendEvent(event,-1);
    }

    //注册power映射
    public static boolean registerPowerMapping(String powerId,PowerCreate creator)
    {
        //power的映射表
        HashMap<String,PowerCreate> creatorMapping = PowerMapping.creatorMapper;
        if(creatorMapping == null || creatorMapping.isEmpty())
        {
            PowerMapping.initCreatorMapper();
            creatorMapping = PowerMapping.creatorMapper;
        }
        //判断是否已经存在
        if(creatorMapping.containsKey(powerId))
            return false;
        //添加映射关系
        creatorMapping.put(powerId,creator);
        return true;
    }
}
