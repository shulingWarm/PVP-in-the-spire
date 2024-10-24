package pvp_in_the_spire;

import pvp_in_the_spire.character.CharacterInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

import java.io.*;
import java.net.*;

//用java的基本的socket实现的两线程之间的通信，后面可能需要实现一个跨电脑的通信
public class SocketServer {

    //java的socket
    public Socket socket;
    //用来管理二进制数据输入的句柄
    public DataOutputStream streamHandle;
    //用来读取信息的句柄
    public DataInputStream inputHandle;

    //一个全局的开关，用于debug,不使用的时候关掉它这样就不联网了
    public static final boolean USE_NETWORK = true;
    //对方玩家的数据类型
    public static AbstractPlayer.PlayerClass playerType;
    //对方玩家的最大生命上限和目前的血量
    public static int monsterMaxHealth;
    public static int monsterCurrentHealth;
    //对方玩家的角色类型
    public static AbstractPlayer.PlayerClass monsterChar;
    //尾巴遗物的数量，这里只是为了处理丢人尾巴并且只处理活着的
    public static int tailNum;
    //自己的尾巴数量，播放bgm的时候会参考这个数量
    public static int myTailNum;
    //对面的敌人的靴子的数量，正常情况下最多只会有一个
    public static int bootNum;
    //是否有外卡钳的信息
    public static int hasCaliper;
    //瓶中精灵的数量
    public static int fairyPotionNum;
    //对方的钱数
    public static int oppositeGold;
    //对方的初始球位
    public static int beginOrbNum=0;
    //掉钱的基数，默认是0.5
    public static float loseGoldRate = 0;
    //系统的起始时间
    public static long beginGameTime=0;
    //用于判断第一场战斗谁先手
    public static long myEnterTime=0;
    public static long oppositeEnterTime=0;
    //我在下一次战斗中是不是先手
    public static boolean firstHandFlag = false;
    //目前已经经过了几次战斗
    public static int battleNum = 0;
    //对方是否已经准备好了
    public static boolean oppositePlayerReady = false;
    //用于更新自己是否有圆顶
    public static boolean hasDome = false;
    //自己显示给对方的名字
    public static String myName = "user";
    public static String oppositeName = "user";
    //敌方的角色信息
    public static CharacterInfo oppositeCharacter = null;
    //敌人的能量情况
    public static int masterEnergy = 0;
    public static int currentEnergy = 0;

    //初始化相关的静态变量
    public static void initGlobal()
    {
        oppositePlayerReady=false;
        firstHandFlag = false;
        myEnterTime=0;
        oppositeEnterTime=0;
        battleNum=0;
        myName = "user";
        oppositeName = "user";
    }

    //判断是否刚刚经过了胜利
    public static boolean isJustWin()
    {
        return !firstHandFlag;
    }

    protected SocketServer()
    {

    }

    public SocketServer(int idPort) throws IOException
    {
        //初始化socket
        socket = new Socket("localhost",idPort);
        //初始化信息传输的句柄
        streamHandle = new DataOutputStream(socket.getOutputStream());
        inputHandle = new DataInputStream(socket.getInputStream());
    }

    //清空输入流，局域网通信里面不需要这个接口，这是给steam远程通信用的
    public void clearInputStream()
    {

    }

    //发送所有写入过的数据
    public void send()
    {
        //把输入流够本里面的内容确保都已经发送出去了
        try
        {
            streamHandle.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //获取socket的输入数据流
    public DataInputStream receive()
    {
        //获取socket的输出流数据
        try
        {
            return new DataInputStream(socket.getInputStream());
        }
        catch(IOException e)
        {
            throw new RuntimeException("get input stream error");
        }
    }

    //判断是否有数据可以被用于接收
    public boolean isDataAvailable()
    {
        try
        {
            return inputHandle.available()>0;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }
}
