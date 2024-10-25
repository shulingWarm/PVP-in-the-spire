package pvp_in_the_spire.room;

import pvp_in_the_spire.character.FriendMonster;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.actions.utility.HideHealthBarAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.vfx.combat.InflameEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    //对方上次加入过的monster
    //这个是为了保证两边每一局加入的友军是一样的
    public String oppositeFriendName = "";

    public FriendManager()
    {

    }

    //战斗开始时的初始化
    public void battleBeginInit()
    {
        this.oppositeFriendName = "";
        //清空我方友军和敌方友军
        id2MonsterMap.clear();
        monster2IdMap.clear();
        //清空友军列表
        monsterList.clear();
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

    //判断一个creature是否属于敌方友军
    public boolean judgeOppositeFriend(AbstractCreature creature)
    {
        return creature instanceof AbstractMonster &&
                monster2IdMap.containsKey((AbstractMonster) creature);
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

    //玩家死亡时，清理爪牙，这里指的是清理monster列表里的内容
    public void makeMinionSuicide()
    {
        //遍历每个爪牙
        for (Map.Entry<AbstractMonster, Integer> entry : this.monster2IdMap.entrySet()) {
            AbstractMonster m = entry.getKey();
            if(m.isDead || m.isDying)
                continue;
            AbstractDungeon.actionManager.addToTop(new HideHealthBarAction(m));
            AbstractDungeon.actionManager.addToTop(new SuicideAction(m));
            AbstractDungeon.actionManager.addToTop(new VFXAction(m, new InflameEffect(m), 0.2F));
        }
        //清理我方敌人槽位
        monster2IdMap.clear();
    }

    //由ControlMonster的实体来调用，这个逻辑后面再优化
    public void render(SpriteBatch sb)
    {
        //需要确保当前的状态是战斗
        if(!(AbstractDungeon.getCurrRoom() instanceof MonsterRoom))
            return;
        //遍历每个monster来执行渲染
        for(FriendMonster eachMonster : monsterList)
        {
            eachMonster.render(sb);
        }
    }

    // 对画面逻辑的更新操作
    public void update()
    {
        if(!(AbstractDungeon.getCurrRoom() instanceof MonsterRoom))
            return;
        for(FriendMonster eachMonster : monsterList)
        {
            eachMonster.update();
        }
    }


}
