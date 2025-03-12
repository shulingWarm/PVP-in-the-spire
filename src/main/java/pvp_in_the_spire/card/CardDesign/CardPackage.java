package pvp_in_the_spire.card.CardDesign;

import pvp_in_the_spire.card.AdaptableCard;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

//玩家自定义卡牌的卡包
//这是为了方便玩家在游戏配置界面加载自己的卡包
public class CardPackage {

    public String packageName;
    //与卡包的所有链接的卡牌
    public HashSet<String> linkedCards;
    //默认的卡包名
    public static final String DEFAULT_PACKAGE_NAME = "默认卡包";
    //卡包的后缀名
    public static final String PACKAGE_SUFFIX = ".cardpkg";
    //卡牌的文件输出流
    public DataOutputStream stream;

    //构造的时候需要指定卡包的名称
    public CardPackage(String packageName)
    {
        this.packageName = packageName;
        //初始化链接的所有卡牌
        this.linkedCards = new HashSet<>();
    }

    //无名称的卡包
    public CardPackage()
    {
        this("");
    }

    //从数据流里面读取数据
    public void readData(DataInputStream stream)
    {
        try
        {
            //读取卡包的名称
            this.packageName = stream.readUTF();
            //读取链接卡牌的个数
            int tempCardNum = stream.readInt();
            System.out.printf("Package card num: %d\n", tempCardNum);
            //读取卡包里面涉及到的所有卡牌
            for(int idCard=0;idCard<tempCardNum;++idCard)
            {
                String tempCardName = stream.readUTF();
                //记录到集合里面
                this.linkedCards.add(tempCardName);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //添加链接的卡牌
    public void addLinkCard(String cardId)
    {
        this.linkedCards.add(cardId);
        try
        {
            //新建卡牌的输出流
            DataOutputStream stream = new DataOutputStream(
                    Files.newOutputStream(Paths.get(this.getPackagePath()))
            );
            //调用写入卡包数据的过程
            this.writeData(stream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //将数据写入数据流
    public void writeData(DataOutputStream stream)
    {
        //写入卡包的名称
        try
        {
            stream.writeUTF(this.packageName);
            //写入链接卡牌的个数
            stream.writeInt(this.linkedCards.size());
            //遍历写入每个链接卡牌
            for(String eachCard : this.linkedCards)
            {
                stream.writeUTF(eachCard);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //获得卡包应该保存的路径
    public String getPackagePath()
    {
        return this.packageName + PACKAGE_SUFFIX;
    }

    public static String makePackagePath(String packageName)
    {
        return packageName + PACKAGE_SUFFIX;
    }

    //默认默认卡包的文件名
    public static String getDefaultPackagePath()
    {
        return DEFAULT_PACKAGE_NAME + PACKAGE_SUFFIX;
    }

}
