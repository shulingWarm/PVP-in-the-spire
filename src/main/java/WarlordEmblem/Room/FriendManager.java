package WarlordEmblem.Room;

import WarlordEmblem.character.FriendMonster;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;
import java.util.HashMap;

//友军管理器，不过这还是通过打patch实现的
public class FriendManager {

    //友军管理器的实体
    public static FriendManager instance = null;

    //初始化友军的实体管理器
    public static void initGlobalManager()
    {
        instance = new FriendManager();
    }

    //友军的列表
    public ArrayList<FriendMonster> monsterList = new ArrayList<>();

    //xy的变化幅度
    public static final float X_CHANGE = 0.1f;
    public static final float Y_CHANGE = 0.05f;

    //从id到monster的映射
    //表示我方友军
    public HashMap<Integer,FriendMonster> id2MonsterMap = new HashMap<>();

    //从monster到id的映射
    //用于处理当敌方爪牙状态发生变化时，告知对方同步状态
    public HashMap<AbstractMonster,Integer> monster2IdMap = new HashMap<>();


    public FriendManager()
    {

    }

    //适配新的monster的位置
    public float[] getNewMonsterXY()
    {
        //最后添加的一个位置的xy
        float[] lastXy = {AbstractDungeon.player.drawX,
                AbstractDungeon.player.drawY};
        //判断有没有友军列表
        if(!monsterList.isEmpty())
        {
            //遍历是否有空位
            for(FriendMonster eachMonster : monsterList)
            {
                //如果它的坐标是无效的，就直接返回它的维度坐标
                if(!eachMonster.judgeValid())
                {
                    //暂时不应该走这个分支，目前还不考虑从中间插入敌人的情况
                    System.out.println("Should not happen");
                    return eachMonster.getLocation();
                }
            }
            //把上一个位置改成当前位置
            lastXy = monsterList.get(monsterList.size()-1).getLocation();
        }
        //从上一个位置叠加新的位置
        return new float[]{lastXy[0]+ Settings.WIDTH*X_CHANGE,
            lastXy[1]+Settings.HEIGHT*Y_CHANGE};
    }

    //添加友军 如果传入的就是monster那就直接添加了
    //但后面也可以直接添加FriendMonster,那就是兼容了可控制的友军
    public void addFriend(AbstractMonster monster)
    {
        FriendMonster tempMonster = new FriendMonster(monster);
        //把它记录到映射表中
        id2MonsterMap.put(id2MonsterMap.size(), tempMonster);
        //构造一个友军添加进去
        monsterList.add(tempMonster);
    }

    //通过monster获取id
    public int getIdByMonster(AbstractMonster monster)
    {
        if(monster2IdMap.containsKey(monster))
            return monster2IdMap.get(monster);
        return -1;
    }

    //记录对面的monster和它的id
    public void registerOppositeMonster(AbstractMonster monster,
        int id)
    {
        monster2IdMap.put(monster,id);
    }

    //通过id获取monster
    public FriendMonster getMonsterById(int id)
    {
        if(id2MonsterMap.containsKey(id))
        {
            return id2MonsterMap.get(id);
        }
        return null;
    }

    //由ControlMonster的实体来调用，这个逻辑后面再优化
    public void render(SpriteBatch sb)
    {
        //遍历每个monster来执行渲染
        for(FriendMonster eachMonster : monsterList)
        {
            eachMonster.render(sb);
        }
    }

    // 对画面逻辑的更新操作
    public void update()
    {
        for(FriendMonster eachMonster : monsterList)
        {
            eachMonster.update();
        }
    }


}
