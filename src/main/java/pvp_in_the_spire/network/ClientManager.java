package pvp_in_the_spire.network;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//多个client的管理器
//这是用来处理客户端的连接请求的
public class ClientManager {

    //线程执行器
    public ExecutorService executorService;
    //用来等待连接的server socket
    public ServerSocket serverSocket;
    //当收到新的用户连接请求时的回调
    public ClientConnectInterface clientInterface;

    //这是用于等待用户连接的函数
    public void waitForConnection()
    {
        //目前的ip通信就先简单处理一下
        for(int idClient=0;idClient<3;++idClient)
        {
            //获取新的客户连接
            try
            {
                Socket clientSocket = this.serverSocket.accept();
                //把得到的socket传给回调
                clientInterface.receiveConnection(clientSocket);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public ClientManager(int idPort,
         ClientConnectInterface clientInterface
    )
    {
        //记录用户连接请求时的回调
        this.clientInterface = clientInterface;
        try
        {
            //新建线程执行器
            this.executorService = Executors.newSingleThreadExecutor();
            //初始化新的server socket
            this.serverSocket = new ServerSocket(idPort);
            //调用监听聊天请求的操作
            this.executorService.submit(this::waitForConnection);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
