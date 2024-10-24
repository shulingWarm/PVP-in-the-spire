package pvp_in_the_spire;

import java.io.*;
import java.net.*;


//特殊的进程间通信连接句柄，构造的时候会先测试能否连接，如果没有连接就会把自己初始化成服务端
public class AutomaticSocketServer extends SocketServer {

    //服务端的socket 它是用来等待连接的
    ServerSocket server;

    //用于记录是不是server
    boolean serverFlag = false;

    //一个抽象的全局使用的server
    public static AutomaticSocketServer globalServer;

    //初始化自动转换的server,这个主要用于局域网连接
    public static void initAutomatic()
    {
        globalServer = null;
    }

    public static AutomaticSocketServer getServer() throws NullPointerException
    {
        if(globalServer == null)
        {
            throw new NullPointerException();
        }
        return globalServer;
    }

    //空的构造函数，但不要随便用，它是给DynamicServer用的
    public AutomaticSocketServer(Socket externalSocket)
    {
        this.socket = externalSocket;
        try
        {
            streamHandle = new DataOutputStream(socket.getOutputStream());
            inputHandle = new DataInputStream(socket.getInputStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //空的构造函数，把控制权完全交给子类，其实应该继承SocketServer
    //这来源于前期失败的设计，这里只好这样写了
    protected AutomaticSocketServer(){}

    protected AutomaticSocketServer(int idPort)
    {
        super();
        try
        {
            //初始化socket
            socket = new Socket("localhost",idPort);
            //初始化信息传输的句柄
            streamHandle = new DataOutputStream(socket.getOutputStream());
            inputHandle = new DataInputStream(socket.getInputStream());
            System.out.println("client connect ok!!!!!");
        }
        catch (IOException e)
        {
            //如果后面要改成那种动态即时连接的模式，就添加一个throw e然后把下面注释掉
            try
            {
                //连接失败的情况下就把自己转换成服务端
                System.out.println("construct server socket!!!!!");
                server = new ServerSocket(idPort);
                serverFlag = true;
                System.out.println("waiting connection!!!!!");
                //等待客户端连接
                socket = server.accept();
                System.out.println("accept ok!!!!!");
                //从socket中获取输入流和输出流
                streamHandle = new DataOutputStream(socket.getOutputStream());
                inputHandle = new DataInputStream(socket.getInputStream());
                System.out.println("construct stream!!!!!");
            }
            catch (IOException serverException)
            {
                serverException.printStackTrace();
            }
        }
    }

    //返回这个对象是不是服务端
    public boolean isServer()
    {
        return serverFlag;
    }

    //发送给指定玩家的操作
    public void targetSend(int playerTag)
    {
        this.send();
    }

}
