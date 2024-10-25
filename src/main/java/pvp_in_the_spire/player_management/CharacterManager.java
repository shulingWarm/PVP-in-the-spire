package pvp_in_the_spire.player_management;


import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;

import java.util.ArrayList;

//角色管理器
//这是用来服务换人选角色的，用静态函数实现就够了
public class CharacterManager {

    public static ArrayList<AbstractPlayer.PlayerClass> classList = null;

    public static void initArrayList()
    {
        if(classList == null)
        {
            classList = new ArrayList<>();
            //遍历所有的character
            for(AbstractPlayer eachPlayer : CardCrawlGame.characterManager.getAllCharacters())
            {
                //在列表里面添加对应的操作
                classList.add(eachPlayer.chosenClass);
            }
        }
    }

    //获取角色所属的id
    public static int getClassId(AbstractPlayer.PlayerClass playerClass)
    {
        //遍历每个class
        for(int idClass=0;idClass<classList.size();++idClass)
        {
            if(playerClass == classList.get(idClass))
                return idClass;
        }
        return 0;
    }

    //传入的id可能是负数，矫正一下id
    public static int adjustId(int idClass){
        return (idClass + classList.size())%classList.size();
    }


    //获取玩家的class
    public static AbstractPlayer.PlayerClass getClassById(int idClass)
    {
        return classList.get(idClass);
    }

}
