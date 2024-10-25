package pvp_in_the_spire.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

//这是给ip通信使用的
//用于专门处理服务于某个客户端
public class ClientHandle {

    public Socket socket;
    //输入输出使用的信息流
    public DataOutputStream streamHandle;
    //用来读取信息的句柄
    public DataInputStream inputHandle;
    //当前客户端的id
    public int idClient;
    public ReceiveInterface receiveInterface;
    //用于监听消息的进程
    public ExecutorService executorService;

    //结束运行的标志 想结束的情况下就把这个置为true
    public boolean endFlag = false;

    public ClientHandle(Socket socket,
        int idClient,
        ReceiveInterface receiveInterface
    )
    {
        this.socket = socket;
        this.idClient = idClient;
        this.receiveInterface = receiveInterface;
        try
        {
            streamHandle = new DataOutputStream(socket.getOutputStream());
            inputHandle = new DataInputStream(socket.getInputStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        //准备用于监听消息的进程
//        this.executorService = Executors.newSingleThreadExecutor();
//        this.executorService.submit(this::listenMessage);
    }

    //接收到消息时的函数
    public void listenMessage()
    {
        while(!endFlag)
        {
            this.checkMessage();
        }
    }

    public void checkMessage()
    {
        try
        {
            if(inputHandle.available() > 0)
            {
                //调用回调函数，把自己的input stream传给回调
                receiveInterface.receiveMessage(this.inputHandle,this.idClient);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //发送字节流数据
    //这里是写的时候直接就发出去的
    public void sendByte(byte[] byteData)
    {
        //需要确保同一时刻只能有一个线程往这里面发送数据
        synchronized (this)
        {
            //向output里面写入数据
            try
            {
                //这里还需要先写入字节的长度
                streamHandle.writeInt(byteData.length);
                streamHandle.write(byteData);
                streamHandle.flush();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


}
