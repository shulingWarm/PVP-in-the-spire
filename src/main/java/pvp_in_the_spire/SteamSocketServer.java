package pvp_in_the_spire;

import pvp_in_the_spire.patches.steamConnect.SteamManager;
import com.codedisaster.steamworks.SteamID;

import java.io.*;
import java.nio.ByteBuffer;

//用steam实现的网络管理器，里面会有一套单独的输入流和输出流
//但这个输入流和输出流每次使用的时候还需要和steam数据交换一下
public class SteamSocketServer extends AutomaticSocketServer {

    //需要被连接的目标用户
    SteamID receiver;
    public static final int BUFFER_SIZE = 8192;
    //用于接收信息的消息队列
    public ByteBuffer receiveBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    //用于发送信息的buffer
    public ByteBuffer sendBuffer;
    //用于存储实质性数据的底层字节流
    public ByteArrayOutputStream byteSendStream = new ByteArrayOutputStream();
    //用来存储接收到的数据的临时交换数组
    //public byte[] swapArray = new byte[BUFFER_SIZE];

    public static int bytesToInt(byte[] ary, int offset) {
        int value;
        value = (int) ((ary[offset+3] & 0xFF)
                | ((ary[offset+2] & 0xFF)<<8)
                | ((ary[offset+1] & 0xFF)<<16)
                | ((ary[offset] & 0xFF)<<24));
        return value;
    }

    public static DataInputStream convertByteBufferToStream(ByteBuffer buffer,int byteSize)
    {
        byte[] tempArray = new byte[byteSize];
        //把buffer数据转换到数组里
        buffer.get(tempArray);
//        int receiveData = bytesToInt(tempArray,0);
//        if(receiveData==13){
//            for(int i=0;i<tempArray.length;++i)
//            {
//                System.out.printf("%d ",tempArray[i]);
//            }
//            System.out.println();
//        }
//        else {
//            System.out.print("receive ");
//            System.out.println(receiveData);
//        }
        ByteArrayInputStream tempByteStream = new ByteArrayInputStream(tempArray);
        //初始化输入流
        return new DataInputStream(tempByteStream);
    }

    //判断是否有数据可用于接收的重载，这里判断接收数据的时候，
    //需要从steam的接口里读取数据来判断是否有待读取的数据
    @Override
    public boolean isDataAvailable()
    {
        //先判断下是否输入流里面还有东西
        if(inputHandle!=null && super.isDataAvailable())
        {
            return true;
        }
        //从steam端接收数据，如果有数据就存储到inputHandle里面
        receiveBuffer.clear();
        int byteSize = SteamManager.readDataToByteBuffer(receiveBuffer);
        //如果读取到了数据，说明有可用的数据
        if(receiveBuffer.remaining()>0 && byteSize>0)
        {
            //把byteBuffer转换成inputStream
            this.inputHandle = convertByteBufferToStream(receiveBuffer,byteSize);
            return super.isDataAvailable();
        }
        return false;
    }

    //清空输入流，主要是steam每次读取出来的数据都固定是buffer长度的，干扰太大了
    @Override
    public void clearInputStream()
    {
        inputHandle = null;
    }

    public static void appendEmptyMessage(DataOutputStream stream)
    {
        try{
            stream.writeInt(0);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //然后需要重写发送的接口，当数据被发送时应该走steam的通道
    @Override
    public void send()
    {
        //在数据最后面补足一个0,传字符串的时候它可能凑不够4个字节
        appendEmptyMessage(this.streamHandle);
        super.send();
        //把输入流里面的数据转换到ByteBuffer
        byte[] tempByteArray = byteSendStream.toByteArray();

        //int sendFlag = bytesToInt(tempByteArray,0);

//        if(sendFlag==20)
//        {
//            for(int i=0;i<tempByteArray.length;++i)
//            {
//                System.out.printf("%d ",tempByteArray[i]);
//            }
//            System.out.println();
//        }
//        else {
//            System.out.printf("length %d\n",tempByteArray.length);
//        }

        sendBuffer.put(tempByteArray);
        sendBuffer.position(0);
        sendBuffer.limit(tempByteArray.length);
        //通过steam发送数据
        SteamManager.sendDataFromByteBuffer(receiver,sendBuffer);
        sendBuffer.clear();
        byteSendStream.reset();
    }

    public SteamSocketServer(SteamID receiver)
    {
        super();
        //记录接收者
        this.receiver = receiver;
        //数据访问的时候使用的输入流，整个游戏都在使用这个接口
        streamHandle = new DataOutputStream(byteSendStream);
        //输入流的数据最开始的时候被初始化为空
        inputHandle=null;
        this.sendBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    }

}
