package pvp_in_the_spire;

import pvp_in_the_spire.actions.FightProtocol;
import pvp_in_the_spire.character.ControlMoster;
import pvp_in_the_spire.patches.ActionNetworkPatches;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

//动态进行网络连接的服务器
public class DynamicServer {

    private ServerSocket serverSocket = null;
    private ExecutorService executorService = null;
    private Future<Socket> future;

    //一个专门用来读取数据的句柄
    FightProtocol protocol;

    //用于表示是否已经彻底处理结束，只有当接收数据也处理完的时候才会这样判定
    public boolean isDone = false;

    //已经update的次数，故意前5轮什么都不做
    public int updateCycle = 0;

    //用于判断是否可以开始读对方玩家的状态了
    public static boolean canReadStage()
    {
        //判断一下，只要当双方已经建立连接了，就可以开始接收生命值数据了
        return AutomaticSocketServer.globalServer != null;
    }

    //结束等待时的条件
    public static void endWaitingLogic()
    {
        //退出屏幕占有
        AbstractDungeon.isScreenUp=false;
        //处理角色的生命上限和形象之类的
        ControlMoster.instance.initHealthAndTexture();
        //调用第一回合开始战斗的逻辑
        AbstractDungeon.player.applyPreCombatLogic();
        AbstractDungeon.player.applyStartOfCombatLogic();
        //修改血条 这里面会把时间点改成0.7,这样它就会重新计算血条更新的过程
        AbstractDungeon.player.showHealthBar();
        ControlMoster.instance.showHealthBar();
    }

    //每次渲染的时候会调用一下它
    public void update()
    {
        //更改更新次数
        updateCycle++;
        //前5轮什么都不做
        if(updateCycle<6)
        {
            return;
        }
        //如果已经处理结束了，那就直接返回
        if(isDone)
        {
            System.out.println("is done");
            return;
        }
        //判断是否可以开始接收数据
        if(canReadStage())
        {
            //如果有可读的数据说明已经可以结束了
            AutomaticSocketServer server = AutomaticSocketServer.getServer();
            try
            {
                //判断有没有数据可读，如果有可读的话说明已经读取到生命数据了
                if(server.inputHandle.available()>0)
                {
                    //进行数据读取
                    protocol.readData(server);
                    isDone = true;
                    endWaitingLogic();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else {
            try
            {
                //判断服务端是否连接成功
                Socket tempSocket = this.accept();
                if(tempSocket != null)
                {
                    //说明已经连接成功了，那么初始化server数据
                    AutomaticSocketServer.globalServer =
                            new AutomaticSocketServer(tempSocket);
                    //把它标记成服务端
                    AutomaticSocketServer.globalServer.serverFlag = true;
                    //发送当前角色的健康数据
                    sendHealth();
                }
            }
            catch (InterruptedException | ExecutionException e)
            {
                e.printStackTrace();
            }
        }
    }

    //发送生命数据
    public static void sendHealth()
    {
        AutomaticSocketServer server = AutomaticSocketServer.getServer();
        ActionNetworkPatches.sendHealth(server.streamHandle);
        server.send();
    }

    public DynamicServer(int port) throws IOException {

        //如果不使用网络，什么都不用做
        if(!SocketServer.USE_NETWORK)
        {
            this.isDone = true;
            return;
        }
        //先测试一下作为客户端能不能发起连接
        try
        {
            SocketServer tempServer = new SocketServer(port);
            //如果成功的话，就更新getServer里面的那个接口
            AutomaticSocketServer.globalServer = new AutomaticSocketServer(tempServer.socket);
            System.out.println("client connect ok");
            //如果连接成功了的话，发送生命数据的接口
            sendHealth();
        }
        catch (IOException e)
        {
            //这两个变量必须被初始化，即使作为客户端连接成功也不行
            this.serverSocket = new ServerSocket(port);
            this.executorService = Executors.newSingleThreadExecutor();
            //否则会额外开辟一个线程一直等待连接
            this.future = this.executorService.submit(this::acceptNewConnection);
        }
        //不论怎样都会更新一下读取数据的句柄的
        protocol = new FightProtocol();
    }

    private Socket acceptNewConnection() {
        try {
            return this.serverSocket.accept();
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    public void close() throws IOException {
        this.executorService.shutdown();
        this.serverSocket.close();
    }

}
