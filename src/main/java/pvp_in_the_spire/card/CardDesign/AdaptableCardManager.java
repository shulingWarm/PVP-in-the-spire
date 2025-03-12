package pvp_in_the_spire.card.CardDesign;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import org.lwjgl.Sys;
import pvp_in_the_spire.card.AdaptableCard;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

//玩家自定义卡牌的管理器
//玩家有自定义卡牌的时候都会存储在这里面
public class AdaptableCardManager {

    //管理卡牌的实体
    public static AdaptableCardManager instance;
    //所有的玩家设计的卡牌
    public HashMap<String, AdaptableCard> userCardMap;
    //卡包的map
    public HashMap<String, CardPackage> packageMap;
    //用来记录所有卡包的路径
    public final String PACKAGE_INFO_PATH = "PackageInfo.bin";

    //获得manager的实体
    public static AdaptableCardManager getInstance()
    {
        if(instance == null)
        {
            instance = new AdaptableCardManager();
        }
        return instance;
    }

    //从离散卡牌库中读取卡牌
    public void loadAdaptCard(String cardName)
    {
        //判断是否已经读取过这个卡牌
        if(!userCardMap.containsKey(cardName))
        {
            try
            {
                //新建当前卡牌的数据流
                DataInputStream stream = new DataInputStream(
                    Files.newInputStream(Paths.get(AdaptableCard.getCardPath(cardName)))
                );
                //读取基础卡牌
                String baseCardName = stream.readUTF();
                //基础卡牌的对象
                AbstractCard baseCard = CardLibrary.getCard(baseCardName);
                //初始化卡牌
                AdaptableCard adaptableCard = new AdaptableCard(baseCard);
                //令新的卡牌读取数据流
                adaptableCard.loadCard(stream);
                //把它添加到card map里面
                userCardMap.put(cardName, adaptableCard);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    //存储adaptable card的流程
    public void saveAdaptableCard(AdaptableCard card)
    {
        //新建这个卡牌的数据流
        try
        {
            DataOutputStream stream = new DataOutputStream(
                Files.newOutputStream(Paths.get(AdaptableCard.getCardPath(card.cardID)))
            );
            //调用卡牌里面执行操作的流程
            card.saveCard(stream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //注册新的卡包
    public void registerNewPackage(CardPackage cardPackage)
    {
        this.packageMap.put(cardPackage.packageName, cardPackage);
        //开辟文件流存储所有卡包
        try
        {
            DataOutputStream outputStream = new DataOutputStream(
                Files.newOutputStream(Paths.get(PACKAGE_INFO_PATH))
            );
            //写入package的个数
            outputStream.writeInt(this.packageMap.size());
            //遍历写入每个package的名字
            for(String eachPackage : this.packageMap.keySet())
            {
                outputStream.writeUTF(eachPackage);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //读取默认卡包
    public boolean loadCardPackage(String packagePath)
    {
        //把字符串转换成path
        Path filePath = Paths.get(CardPackage.makePackagePath(packagePath));
        if(!Files.exists(filePath))
        {
            return false;
        }
        try
        {
            //新建文件输入流
            DataInputStream inputStream = new DataInputStream(
                    Files.newInputStream(filePath)
            );
            //新建一个空的卡包
            CardPackage tempPackage = new CardPackage();
            //调用读取数据流的过程
            tempPackage.readData(inputStream);
            //遍历卡包里面的所有卡牌
            for(String eachCard : tempPackage.linkedCards)
            {
                this.loadAdaptCard(eachCard);
            }
            this.packageMap.put(tempPackage.packageName, tempPackage);
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    //加载所有的卡包
    public void loadAllPackage()
    {
        try
        {
            DataInputStream stream = new DataInputStream(
                Files.newInputStream(Paths.get(PACKAGE_INFO_PATH))
            );
            //读取卡包的个数
            int packageNum = stream.readInt();
            for(int i=0;i<packageNum;++i)
            {
                //读取卡包的名字
                String tempPackage = stream.readUTF();
                //读取卡包
                loadCardPackage(tempPackage);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public AdaptableCardManager()
    {
        this.userCardMap = new HashMap<>();
        this.packageMap = new HashMap<>();
        //读取所有卡包
        this.loadAllPackage();
        //如果没有卡包就构造一个默认卡包
        if(this.packageMap.isEmpty())
        {
            //读取失败的情况下，直接自己构造卡包
            CardPackage tempPackage = new CardPackage(CardPackage.DEFAULT_PACKAGE_NAME);
            //把默认卡牌添加到卡牌库中
            this.registerNewPackage(tempPackage);
        }
    }

    //给卡牌注册卡包
    public void registerCardPackageOfCard(AdaptableCard card,
      String packageName)
    {
        //判断是否存在这个卡包
        if(this.packageMap.containsKey(packageName))
        {
            //给卡包里面的链接卡牌数计数增加
            CardPackage tempPackage = this.packageMap.get(packageName);
            tempPackage.addLinkCard(card.cardID);
        }
    }

    //获得指定的卡包
    public CardPackage getCardPackage(String packageName)
    {
        if(this.packageMap.containsKey(packageName))
        {
            return this.packageMap.get(packageName);
        }
        return null;
    }

    //获得已经保存过的玩家自定义卡牌
    public AdaptableCard getCard(String cardId)
    {
        if(this.userCardMap.containsKey(cardId))
        {
            return this.userCardMap.get(cardId);
        }
        return null;
    }

    //添加新的卡牌，这会发生在另存卡牌的时候
    public void addNewCard(AdaptableCard card)
    {
        //整合新卡牌的信息
        card.summarizeModification();
        //存储卡牌数据流
        this.saveAdaptableCard(card);
        //把它添加到映射表里面
        this.userCardMap.put(card.cardID, card.adaptableCopy());
        //标记卡牌已经保存过的标志
        card.cardSavedFlag = true;
    }

}
