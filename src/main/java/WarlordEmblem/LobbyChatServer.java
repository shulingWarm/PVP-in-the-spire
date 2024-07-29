package WarlordEmblem;

import WarlordEmblem.network.Lobby.LobbyManager;
import WarlordEmblem.network.SteamConnector;
import WarlordEmblem.patches.steamConnect.SteamManager;
import com.codedisaster.steamworks.SteamException;
import com.codedisaster.steamworks.SteamID;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;
import java.util.HashSet;

//调用房间里的chat来进行通信的操作
public class LobbyChatServer extends AutomaticSocketServer {

    public static final int BUFFER_SIZE = 8192;
    //用于接收信息的消息队列
    public ByteBuffer receiveBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    //用于发送信息的buffer
    public ByteBuffer sendBuffer;
    //用于存储实质性数据的底层字节流
    public ByteArrayOutputStream byteSendStream = new ByteArrayOutputStream();
    //已经建立连接了的每个用户
    public HashSet<SteamID> playerSet;

    public static LobbyChatServer instance = null;

    //构造函数需要指定lobby的id
    public LobbyChatServer()
    {
        super();
        //初始化玩家的列表
        this.playerSet = new HashSet<>();
        //数据访问的时候使用的输入流，整个游戏都在使用这个接口
        streamHandle = new DataOutputStream(byteSendStream);
        //输入流的数据最开始的时候被初始化为空
        inputHandle=null;
        this.sendBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
        instance = this;
    }

    //注册新的玩家
    public void registerPlayer(SteamID steamID){
        playerSet.add(steamID);
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
        //遍历每个玩家发送消息
        for(SteamID eachPlayer : playerSet)
        {
            SteamManager.sendDataFromByteBuffer(eachPlayer,sendBuffer);
        }
        if(playerSet.isEmpty())
        {
            System.out.println("No player to send!");
        }
        sendBuffer.clear();
        byteSendStream.reset();
    }

    @Override
    public boolean isDataAvailable() {
        //遍历每个玩家
        for(SteamID eachPlayer : playerSet)
        {
            receiveBuffer.clear();
            //读取该玩家的数据
            int byteSize = SteamManager.readDataToByteBuffer(eachPlayer,receiveBuffer);
            if(receiveBuffer.remaining() > 0 && byteSize > 0)
            {
                //把字节流转换成stream
                this.inputHandle = SteamSocketServer.convertByteBufferToStream(
                    receiveBuffer,byteSize
                );
                //调用input handle的处理逻辑
                GlobalManager.messageTriggerInterface.triggerMessage(this.inputHandle);
            }
        }
        return false;
    }
}
