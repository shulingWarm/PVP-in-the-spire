package pvp_in_the_spire.orbs;


import pvp_in_the_spire.actions.FightProtocol;
import com.megacrit.cardcrawl.orbs.AbstractOrb;

import java.util.HashMap;

//为了同步两边的球，给两边的所有的球做一个编号
//玩家的球是从球到数字的映射
//敌人的球是从数字到球的映射
public class OrbMapping {

    //玩家的球位映射表
    public static HashMap<AbstractOrb,Integer> playerOrbMapping =
            new HashMap<AbstractOrb,Integer>();

    //敌人的从数字到球的映射
    public static HashMap<Integer,AbstractOrb> monsterOrbMapping =
            new HashMap<Integer,AbstractOrb>();

    //初始化球的映射表
    public static void init()
    {
        playerOrbMapping.clear();
        monsterOrbMapping.clear();
    }

    //玩家的球的映射表
    public static int addPlayerOrb(AbstractOrb orb)
    {
        //判断有没有这个球，如果有的话就不用加了
        if(playerOrbMapping.containsKey(orb))
        {
            return playerOrbMapping.get(orb);
        }
        int newKey = playerOrbMapping.size();
        //在球的映射表里面添加这个球
        playerOrbMapping.put(orb,newKey);
        return newKey;
    }

    //获得某个球对应的玩家映射表
    public static int getPlayerOrbNum(AbstractOrb orb)
    {
        //判断map里面有没有这个球，没有就算了
        if(!playerOrbMapping.containsKey(orb))
            return -1;
        return playerOrbMapping.get(orb);
    }

    //根据球的种类生成球
    public static MonsterOrb generateOrbByType(int idType)
    {
        if(idType == FightProtocol.ORB_LIGHTING)
        {
            return new MonsterLighting();
        }
        else if(idType == FightProtocol.ORB_BLOCK)
        {
            //冰球
            return new MonsterFrost();
        }
        else if(idType == FightProtocol.ORB_DARK)
        {
            //黑球 但这里加的是假的黑球
            return new MonsterDark();
        }
        else if(idType == FightProtocol.ORB_PLASMA)
        {
            return new MonsterPlasma();
        }
        return null;
    }

    //把某个球和对应的敌人的标号添加到数据中
    public static void addMonsterOrb(AbstractOrb orb,int idOrb)
    {
        monsterOrbMapping.put(idOrb,orb);
    }

    //获得敌人的某个数字对应的球
    public static AbstractOrb getMonsterOrb(int idOrb)
    {
        //判断是否存在这个球，如果没有的话返回空指针
        if(!monsterOrbMapping.containsKey(idOrb))
            return null;
        return monsterOrbMapping.get(idOrb);
    }

}
