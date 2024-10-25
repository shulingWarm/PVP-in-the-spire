package pvp_in_the_spire.pvp_api;

import java.io.DataInputStream;
import java.io.DataOutputStream;

//通信事件的基类
public class BaseEvent {

    //事件的标志
    public String eventId;
    //事件在列表里面的id
    public int listId;

    //对事件的编码
    public void encode(DataOutputStream streamHandle)
    {

    }

    //对事件的解码
    public void decode(DataInputStream streamHandle)
    {

    }


}
