package pvp_in_the_spire.card;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.lwjgl.Sys;
import pvp_in_the_spire.card.CardAction.AbstractCardAction;
import pvp_in_the_spire.card.CardDesign.AdaptableCardManager;
import pvp_in_the_spire.card.CardDesign.CardPackage;
import pvp_in_the_spire.patches.PanelScreenPatch;

import javax.print.DocFlavor;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

//允许被修改的卡牌
public class AdaptableCard extends AbstractCard {

    //被修改的卡牌的基础卡牌
    public AbstractCard baseCard;

    //卡牌抽象动作的列表
    public HashMap<String, AbstractCardAction> cardActionMap;

    //当前卡牌链接的卡包
    public HashSet<String> linkCardPackages;

    //卡牌是否已经保存过的标志
    public boolean cardSavedFlag = false;

    //存储卡牌的文件夹的名字
    public static final String CARD_FOLDER_NAME = "PlayerCards";

    public AdaptableCard(AbstractCard card) {
        super(card.cardID, card.name, card.assetUrl, card.cost,
            card.rawDescription, card.type, card.color, card.rarity, card.target);
        //初始化卡牌抽象动作的列表
        this.cardActionMap = new HashMap<>();
        //初始化链接的卡包
        this.linkCardPackages = new HashSet<>();
        //记录基础卡牌
        this.baseCard = card;
        //初始化卡牌数值
        this.baseMagicNumber = card.baseMagicNumber;
        this.baseDamage = card.baseDamage;
        this.baseBlock = card.baseBlock;
    }

    //初始化folder
    public static void initCardFolder()
    {
        //当前目录下的folder文件
        File folderFile = new File(CARD_FOLDER_NAME);
        //判断这是不是个文件夹
        if(!folderFile.exists())
        {
            //新建文件夹
            if(!folderFile.mkdirs())
            {
                System.out.println("Create folder failed");
            }
        }
    }

    //根据卡牌名称获得路径
    public static String getCardPath(String cardName)
    {
        return CARD_FOLDER_NAME + "/" + cardName + ".pvpcard";
    }

    //向卡牌操作里面添加Action
    public void addActionToCard(AbstractCardAction cardAction)
    {
        //先尝试能不能直接通过修改卡牌数值而不添加action
        if(cardAction.tryDirectApply(this))
        {
            return;
        }
        //判断这个action是否记录过
        if(this.cardActionMap.containsKey(cardAction.getActionId()))
        {
            this.cardActionMap.get(cardAction.getActionId()).adjustRepeatAction(cardAction);
        }
        else
        {
            this.cardActionMap.put(cardAction.getActionId(),cardAction);
        }
    }

    //判断是否需要添加伤害Action
    //只有当基础卡牌不涉及伤害数值的时候才需要添加伤害类型的Action
    public boolean isNeedDamageAction()
    {
        return this.baseCard.damage == 0;
    }

    //整合所有的action,当调用卡牌另存的时候，需要把卡牌信息整合一下
    public void summarizeModification()
    {
        //最终决定的card id
        String newCardId = this.baseCard.cardID + "_mod";
        //目前的id后缀
        int suffixId = 0;
        //目前确定的id后缀
        while (Files.exists(Paths.get(getCardPath(newCardId)))) {
            //判断卡牌是否存在
            ++suffixId;
            newCardId = this.baseCard.cardID + "_mod" + suffixId;
        }
        //修改卡牌的id
        this.cardID = newCardId;
        //如果没有链接的卡包，就把它弄成默认卡包
        if(this.linkCardPackages.isEmpty())
        {
            AdaptableCardManager.getInstance().registerCardPackageOfCard(
                this,CardPackage.DEFAULT_PACKAGE_NAME
            );
        }
    }

    //从信息流里面读取卡牌
    public void loadCard(DataInputStream stream)
    {
        //读取卡牌的id
        try
        {
            this.cardID = stream.readUTF();
            System.out.println(cardID);
            //读取卡牌的基础伤害值
            this.baseDamage = stream.readInt();
            System.out.println(this.baseDamage);
            //读取基础格挡值
            this.baseBlock = stream.readInt();
            System.out.println(this.baseBlock);
            this.baseMagicNumber = stream.readInt();
            System.out.println(this.baseMagicNumber);
            //读取action的个数
            int actionNum = stream.readInt();
            System.out.printf("action num: %d\n",actionNum);
            //读取每个action的个数
            for(int idAction=0;idAction<actionNum;++idAction)
            {
                //读取action的id
                String actionId = stream.readUTF();
                //获取对应的action
                AbstractCardAction tempAction = AbstractCardAction.getCardAction(actionId);
                if(tempAction != null)
                {
                    //从stream中读取action的数据
                    tempAction.loadCardAction(stream);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //将卡牌信息写入文件流
    public void saveCard(DataOutputStream stream)
    {
        try
        {
            //记录base卡牌的id
            stream.writeUTF(this.baseCard.cardID);
            //保存卡牌id
            stream.writeUTF(this.cardID);
            //记录卡牌的基础伤害值
            stream.writeInt(this.baseCard.baseDamage);
            stream.writeInt(this.baseCard.baseBlock);
            stream.writeInt(this.baseCard.baseMagicNumber);
            //记录卡牌里面的action个数
            stream.writeInt(this.cardActionMap.size());
            //保存卡牌里面涉及到的action
            for(AbstractCardAction eachAction : this.cardActionMap.values())
            {
                //记录action的id
                stream.writeUTF(eachAction.getActionId());
                //写入card action
                eachAction.saveCardAction(stream);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void upgrade() {

    }

    @Override
    public void use(AbstractPlayer abstractPlayer, AbstractMonster abstractMonster) {

    }

    //复制所有的action
    public void cloneAction(HashMap<String, AbstractCardAction> otherActions)
    {
        //遍历所有的action
        for(Map.Entry<String, AbstractCardAction> eachAction : otherActions.entrySet())
        {
            this.cardActionMap.put(eachAction.getKey(),eachAction.getValue().makeCopy());
        }
    }

    //适用于adaptable card的copy函数
    public AdaptableCard adaptableCopy()
    {
        AdaptableCard tempCard = new AdaptableCard(this.baseCard);
        //复制所有的action
        tempCard.cloneAction(this.cardActionMap);
        return tempCard;
    }

    @Override
    public AbstractCard makeCopy() {
        return adaptableCopy();
    }
}
