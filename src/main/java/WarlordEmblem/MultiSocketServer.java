package WarlordEmblem;

import WarlordEmblem.network.ClientConnectInterface;
import WarlordEmblem.network.ClientHandle;
import WarlordEmblem.network.ClientManager;
import WarlordEmblem.network.ReceiveInterface;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
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
    public ClientManager clientManager;

    public MultiSocketServer(int idPort)
    {
        super();
        //初始化用户数据的列表
        this.clientList = new ArrayList<>();
        //初始化client的管理器
        this.clientManager = new ClientManager(idPort,this);
    }

    //这里的接收信息是由多线程来控制的
    @Override
    public boolean isDataAvailable() {
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
