package pvp_in_the_spire.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

//纯粹的一个功能，等待其它机器的连接
//关键是等待连接的时候不要阻塞界面
public class NoPauseWaitConnection {

    public static final int DEFAULT_PORT = 6007;

    //为了方便再次使用的时候析构它，把这两个变量设置成静态的
    static private ServerSocket serverSocket = null;
    static private ExecutorService executorService = null;
    private Future<Socket> future;

    private Socket acceptNewConnection() {
        try {
            return this.serverSocket.accept();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //构造的时候需要传入一个端口号
    public NoPauseWaitConnection(int idPort)
    {
        this.close();
        try
        {
            //这两个变量必须被初始化，即使作为客户端连接成功也不行
            serverSocket = new ServerSocket(idPort);
            executorService = Executors.newSingleThreadExecutor();
            //否则会额外开辟一个线程一直等待连接
            this.future = executorService.submit(this::acceptNewConnection);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public Socket accept() throws ExecutionException, InterruptedException {
        if (this.future.isDone()) {
            Socket clientSocket = this.future.get();
            this.future = this.executorService.submit(this::acceptNewConnection); // Start accepting the next connection
            return clientSocket;
        } else {
            return null;
        }
    }

    public void close() {
        try
        {
            if(executorService!=null)
            {
                executorService.shutdown();
                executorService=null;
            }
            if(serverSocket!=null)
            {
                serverSocket.close();
                serverSocket=null;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}
