package pvp_in_the_spire;

import pvp_in_the_spire.network.ClientConnectInterface;
import pvp_in_the_spire.network.ClientHandle;
import pvp_in_the_spire.network.ClientManager;
import pvp_in_the_spire.network.ReceiveInterface;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

//可以用来同时服务多个人服务器
public class MultiSocketServer extends AutomaticSocketServer
    implements ClientConnectInterface,
        ReceiveInterface
{

    //已经连接上的用户的列表
    public ArrayList<ClientHandle> clientList;
    //多用户的管理器，这是用来监听用户连接的
    public ClientManager clientManager = null;
    //用于存储实质性数据的底层字节流
    public ByteArrayOutputStream byteSendStream = new ByteArrayOutputStream();

    public MultiSocketServer(int idPort)
    {
        super();
        //初始化用户数据的列表
        this.clientList = new ArrayList<>();
        initIOStream();
        //初始化client的管理器
        this.clientManager = new ClientManager(idPort,this);
    }

    //这是给客户端使用的情况
    //这里面不会初始化client manager
    public MultiSocketServer(Socket socket)
    {
        super();
        //初始化用户数据的列表
        this.clientList = new ArrayList<>();
        //把这个socket直接用作客户端就可以了
        receiveConnection(socket);
        initIOStream();
    }

    //初始化io stream
    public void initIOStream()
    {
        //数据访问的时候使用的输入流，整个游戏都在使用这个接口
        streamHandle = new DataOutputStream(byteSendStream);
    }

    //这里的接收信息是由多线程来控制的
    @Override
    public boolean isDataAvailable() {
        //遍历检查每个client,但处理消息的逻辑是这里自身直接处理的
        for(ClientHandle eachClient : clientList)
        {
            eachClient.checkMessage();
        }
        return false;
    }

    //新的用户连接请求
    @Override
    public void receiveConnection(Socket socket) {
        //把socket包装成client handle
        clientList.add(new ClientHandle(socket,clientList.size(),this));
    }

    //从stream中获取字节数组
    public static byte[] getDataBytes(DataInputStream stream)
    {
        //获取字节长度
        try
        {
            int byteLength = stream.readInt();
            //如果字节长度太大，报一个警告
            if(byteLength > 1024)
            {
                System.out.printf("Strange message %d\n",byteLength);
                return new byte[0];
            }
            byte[] byteData = new byte[byteLength];
            int byteNum = stream.read(byteData,0,byteLength);
            if(byteNum != byteLength)
            {
                System.out.printf("need %d but get %d bytes\n",byteLength,byteNum);
            }
            return byteData;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return new byte[0];
    }

    @Override
    public void send() {
        //取出数据里面的字节
        byte[] byteData = byteSendStream.toByteArray();
        //遍历每个用户来发送消息
        for(ClientHandle eachClient : clientList)
        {
            eachClient.sendByte(byteData);
        }
        //清空字节流里面的数据
        byteSendStream.reset();
    }

    @Override
    public void receiveMessage(DataInputStream stream, int idClient) {
        //获取字节数据
        byte[] byteData = getDataBytes(stream);
        //如果长度是零的话就直接取消操作
        if(byteData.length == 0)
            return;
        //把消息移交给当前的消息处理操作
        for (ClientHandle currClient : clientList) {
            //获取client的数据
            if (currClient.idClient != idClient) {
                //调用client发送消息
                currClient.sendByte(byteData);
            }
        }
        //把byte信息打包成DataOutputStream
        ByteArrayInputStream byteStream = new ByteArrayInputStream(byteData);
        //把收到的消息交给当前的消息处理器
        GlobalManager.messageTriggerInterface.triggerMessage(
            new DataInputStream(byteStream)
        );
    }
}
