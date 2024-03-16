package WarlordEmblem.PVPApi;

import java.io.DataInputStream;
import java.io.DataOutputStream;

//通信事件的基类
public class BaseEvent {

    //事件的标志
    public String eventId;

    //对事件的编码
    public void encode(DataOutputStream streamHandle)
    {

    }

    //对事件的解码
    public void decode(DataInputStream streamHandle)
    {

    }


}
