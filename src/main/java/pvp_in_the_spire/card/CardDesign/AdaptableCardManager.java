package pvp_in_the_spire.card.CardDesign;

import jdk.internal.invoke.ABIDescriptorProxy;
import pvp_in_the_spire.card.AdaptableCard;

import java.io.DataInputStream;
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
            //新建当前卡牌的数据流
            
            //初始化一个空的卡牌
            AdaptableCard emptyCard = new AdaptableCard();
            //调用空卡牌的读取过程

        }
    }

    //读取默认卡包
    public boolean loadCardPackage(String packagePath)
    {
        //把字符串转换成path
        Path filePath = Paths.get(packagePath);
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
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public AdaptableCardManager()
    {
        this.userCardMap = new HashMap<>();
        this.packageMap = new HashMap<>();
        //默认卡包的路径
        String defaultCardPath = CardPackage.getDefaultPackagePath();
        //尝试读取默认卡包

        //加载默认卡包

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
            tempPackage.linkedCards.add(card.cardID);
        }
    }

    //添加新的卡牌，这会发生在另存卡牌的时候
    public void addNewCard(AdaptableCard card)
    {
        System.out.println("Prepare add new card");
        //整合新卡牌的信息
        card.summarizeModification();
        //记录到新的卡牌里面
        userCardMap.put(card.cardID,card.adaptableCopy());
    }

}
