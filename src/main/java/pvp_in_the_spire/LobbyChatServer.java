package pvp_in_the_spire;

import pvp_in_the_spire.util.Pair;
import pvp_in_the_spire.patches.steamConnect.SteamManager;
import com.codedisaster.steamworks.SteamID;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;

//调用房间里的chat来进行通信的操作
public class LobbyChatServer extends AutomaticSocketServer {

    public static final int BUFFER_SIZE = 8192;
    //用于发送信息的buffer
    public ByteBuffer sendBuffer;
    //用于存储实质性数据的底层字节流
    public ByteArrayOutputStream byteSendStream = new ByteArrayOutputStream();

    //管理的所有用户
    public HashMap<Integer,SteamID> playerMap;

    public static LobbyChatServer instance = null;

    //构造函数需要指定lobby的id
    public LobbyChatServer()
    {
        super();
        //初始化玩家的列表
        this.playerMap = new HashMap<>();
        //数据访问的时候使用的输入流，整个游戏都在使用这个接口
        streamHandle = new DataOutputStream(byteSendStream);
        //输入流的数据最开始的时候被初始化为空
        inputHandle=null;
        this.sendBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
        instance = this;
    }

    //注册新的玩家
    public void registerPlayer(SteamID steamID){
        int steamTag = steamID.getAccountID();
        if(!playerMap.containsKey(steamTag))
            playerMap.put(steamTag,new SteamID(steamID));
    }

    //移除所有的玩家
    public void removeAllPlayer()
    {
        playerMap.clear();
    }

    //移除玩家
    public void removePlayer(SteamID steamID)
    {
        playerMap.remove(steamID.getAccountID());
    }


    @Override
    public void send() {
        //把发送序列补齐4字节
        SteamSocketServer.appendEmptyMessage(this.streamHandle);
        super.send();
        //把里面的数据转换成byte数组
        byte[] tempByteArray = this.byteSendStream.toByteArray();
        sendBuffer.put(tempByteArray);
        sendBuffer.position(0);
        sendBuffer.limit(tempByteArray.length);
        System.out.printf("Sending player num: %d\n",playerMap.size());
        //遍历每个玩家发送消息
        for(SteamID eachPlayer : playerMap.values())
        {
            SteamManager.sendDataFromByteBuffer(eachPlayer,sendBuffer);
        }
        sendBuffer.clear();
        byteSendStream.reset();
    }

    @Override
    public void targetSend(int playerTag) {
        //把发送序列补齐4字节
        SteamSocketServer.appendEmptyMessage(this.streamHandle);
        super.send();
        //把里面的数据转换成byte数组
        byte[] tempByteArray = this.byteSendStream.toByteArray();
        sendBuffer.put(tempByteArray);
        sendBuffer.position(0);
        sendBuffer.limit(tempByteArray.length);
        //把消息发送给目标玩家
        if(this.playerMap.containsKey(playerTag))
        {
            SteamManager.sendDataFromByteBuffer(this.playerMap.get(playerTag),
                sendBuffer);
        }
        sendBuffer.clear();
        byteSendStream.reset();
    }

    @Override
    public boolean isDataAvailable() {
        //先判断下是否输入流里面还有东西
        if(inputHandle!=null && super.isDataAvailable())
        {
            return true;
        }
        Pair<DataInputStream,SteamID> readResult = SteamManager.directReadData();
        if(readResult != null)
        {
            inputHandle = readResult.first;
            registerPlayer(readResult.second);
            return super.isDataAvailable();
        }
        else {
            inputHandle = null;
        }
        return false;
    }
}
