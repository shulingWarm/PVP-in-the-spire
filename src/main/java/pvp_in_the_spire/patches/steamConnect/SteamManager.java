package pvp_in_the_spire.patches.steamConnect;

import pvp_in_the_spire.util.Pair;
import pvp_in_the_spire.SteamSocketServer;
import pvp_in_the_spire.helpers.FieldHelper;
import com.codedisaster.steamworks.*;
import com.megacrit.cardcrawl.integrations.steam.SFCallback;
import com.megacrit.cardcrawl.integrations.steam.SteamIntegration;

import java.io.*;
import java.nio.ByteBuffer;

//steam的连接管理器，但最开始的时候只是读取一个文本来判断一下需要查找哪个玩家
public class SteamManager {

    //游戏内容的接收者
    public static String receiver = "";
    //是否判断过的标志，防止每次都读取一遍谁的
    public static int judgedFlag = -1;

    //steam好友的管理器
    public static SteamFriends steamFriends=null;
    //steam的网络管理器
    public static SteamNetworking steamNetworking=null;
    //目标好友的id
    public static SteamID targetId = null;
    //自身的steam user
    public static SteamUser steamUser;
    //自身的steam id
    public static SteamID selfSteamId;
    //固定使用的steamChannel
    public static final int STEAM_CHANNEL = 0;
    //仅仅用来接收消息的id,消息是谁发来的根本不重要
    public static SteamID receiverId = null;

    //初始化steam相关的全局变量
    public static void initManager()
    {
        receiver = "";
        judgedFlag=-1;
        steamFriends=null;
        steamNetworking=null;
        targetId=null;
    }

    //steam的网络连接的回调函数的实现
    public static class SteamCallback implements SteamNetworkingCallback
    {
        @Override
        public void onP2PSessionConnectFail(SteamID steamID, SteamNetworking.P2PSessionError p2PSessionError) {
            System.out.println(p2PSessionError.name());
        }

        @Override
        public void onP2PSessionRequest(SteamID steamID) {
            //打印请求的帐户
            System.out.println(steamID.getAccountID());
            steamNetworking.acceptP2PSessionWithUser(steamID);
        }
    }

    //直接读取数据，生成byte buffer
    public static Pair<DataInputStream, SteamID> directReadData()
    {
        //判断是否有消息可以接收
        int [] bufferSize = new int[1];
        steamNetworking.isP2PPacketAvailable(STEAM_CHANNEL,bufferSize);
        try
        {
            if(bufferSize[0] != 0)
            {
                //动态创建byte buffer数据
                ByteBuffer dstBuffer = ByteBuffer.allocateDirect(bufferSize[0]);
                bufferSize[0] = steamNetworking.readP2PPacket(receiverId,dstBuffer,STEAM_CHANNEL);
                return new Pair<>(SteamSocketServer.convertByteBufferToStream(dstBuffer,bufferSize[0]),
                        receiverId);
            }
        }
        catch (SteamException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    //从指定的steamid里面接收数据，存储到指定的buffer里面
    public static int readDataToByteBuffer(ByteBuffer dstBuffer)
    {
        //判断是否有消息可以接收
        int [] bufferSize = new int[1];
        steamNetworking.isP2PPacketAvailable(STEAM_CHANNEL,bufferSize);
        try
        {
            if(bufferSize[0] != 0)
                return steamNetworking.readP2PPacket(receiverId,dstBuffer,STEAM_CHANNEL);
        }
        catch (SteamException e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    //获得自己的用户名
    public static String getMyName()
    {
        return steamFriends.getPersonaName();
    }

    public static void sendDataFromByteBuffer(SteamID id,ByteBuffer fromBuffer)
    {
        try
        {
            steamNetworking.sendP2PPacket(id,fromBuffer, SteamNetworking.P2PSend.ReliableWithBuffering,STEAM_CHANNEL);
        }
        catch (SteamException e)
        {
            e.printStackTrace();
        }
    }

    //初始化steam的各种配置信息
    //这个东西只需要被调用一次
    public static void prepareNetworking()
    {
        //如果已经初始化过就退出
        if(steamFriends==null)
        {
            steamFriends = new SteamFriends(new SFCallback());
            steamNetworking = new SteamNetworking(new SteamCallback());
            //初始化自身的steam user
            steamUser = FieldHelper.getPrivateStaticValue(
                SteamIntegration.class,"steamUser"
            );
            selfSteamId = steamUser.getSteamID();
            receiverId = new SteamID();
        }
    }

    //获取自身的steam id
    public static SteamID getSelfSteamId()
    {
        return selfSteamId;
    }


    //初始化接收者的steamID
    public static void initReceiverID()
    {
        prepareNetworking();
        //获取好友的数量
        int friendNum = steamFriends.getFriendCount(
                SteamFriends.FriendFlags.All
        );
        //遍历每个好友，找那个真正的目标好友
        for(int idFriend=0;idFriend<friendNum;++idFriend)
        {
            //获取当前好友的steamid
            SteamID tempSteamId = steamFriends.getFriendByIndex(idFriend, SteamFriends.FriendFlags.All);
            //获取好友的name
            String friendName = steamFriends.getFriendPersonaName(tempSteamId);
            //如果找到了目标好友就停止处理
            if(friendName.equals(receiver))
            {
                targetId = tempSteamId;
                return;
            }
        }
        //如果没找到说明信息有误
        System.out.print("cannot find friend ");
        System.out.println(receiver);
        judgedFlag=0;
    }

    //判断是否需要使用steam,目前只是读取文本来判断一下
    //后面正式使用的时候会满足一下常规的使用逻辑
    public static boolean judgeUseSteam()
    {
        if(judgedFlag!=-1)
            return judgedFlag==1;
        judgedFlag=0;
        //判断的时候固定检查这个文本
        final String targetFile = "./mods/steam.txt";
        File fileDescriptor = new File(targetFile);
        if(!fileDescriptor.exists())
        {
            System.out.println("no steam friend file");
            return false;
        }
        //读取文本内容
        try
        {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileDescriptor));
            //把文本里面的好友名称存在静态变量里
            receiver = bufferedReader.readLine();
            if(receiver!=null && (!receiver.isEmpty()))
            {
                judgedFlag=1;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        initReceiverID();
        return (judgedFlag==1);
    }

}
