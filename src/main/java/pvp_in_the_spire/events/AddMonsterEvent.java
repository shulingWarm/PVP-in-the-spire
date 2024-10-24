package pvp_in_the_spire.events;

import pvp_in_the_spire.pvp_api.BaseEvent;
import pvp_in_the_spire.room.FriendManager;
import pvp_in_the_spire.helpers.ClassNameHelper;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//添加友军时会使用这个event把消息传给对面
public class AddMonsterEvent extends BaseEvent {

    //要添加的敌人的类名
    public String monsterName;
    //画敌人位置时的xy
    float x;
    float y;
    //敌人生命的最大值
    int maxHealth;
    //敌人的id,这个id两边要对应上
    int idMonster;

    public AddMonsterEvent(String monsterName,float x,float y,
           int maxHealth,int idMonster)
    {
        //指定id
        this.eventId = "AddMonster";
        this.monsterName = monsterName;
        this.x = x;
        this.y = y;
        //记录敌人生命的最大值
        this.maxHealth = maxHealth;
        this.idMonster = idMonster;
    }

    //传入类对象的形式
    public AddMonsterEvent(AbstractMonster monster,int idMonster)
    {
        this(ClassNameHelper.getCreatureClassName(monster),
                monster.drawX,monster.drawY,monster.maxHealth,idMonster);
    }

    //空的构造函数，这是用来往event列表里面记录了
    public AddMonsterEvent()
    {
        this("",0.f,0.f,
        0,0);
    }

    //添加友军的class
    @Override
    public void encode(DataOutputStream streamHandle) {
        //写入className
        try
        {
            streamHandle.writeUTF(this.monsterName);
            //记录新怪物的x,y的位置
            streamHandle.writeFloat(this.x);
            streamHandle.writeFloat(this.y);
            //记录怪的血量
            streamHandle.writeInt(this.maxHealth);
            //记录敌人的数量
            streamHandle.writeInt(this.idMonster);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //添加友军的解码操作
    @Override
    public void decode(DataInputStream streamHandle) {
        System.out.println("receive AddMonsterEvent");
        try
        {
            //读取敌人名称
            String tempName = streamHandle.readUTF();
            //读取xy的数据
            float drawX = Settings.WIDTH - streamHandle.readFloat();
            float drawY = streamHandle.readFloat();
            //读取最大血量
            int tempMaxHealth = streamHandle.readInt();
            //读取敌人的id
            int tempIdMonster = streamHandle.readInt();
            //用临时的怪物名称映射出具体的monster
            AbstractMonster tempMonster =
                ClassNameHelper.createMonster(tempName);
            //判断是否为有效的monster
            if(tempMonster!=null)
            {
                //记录对方的敌人名称
                FriendManager.instance.oppositeFriendName = tempName;
                tempMonster.drawX = drawX;
                tempMonster.drawY = drawY;
                //修改临时的怪物的血量
                tempMonster.maxHealth = tempMaxHealth;
                tempMonster.currentHealth = tempMaxHealth;
                //在映射表里面注册这个id
                FriendManager.instance.registerOppositeMonster(
                    tempMonster,tempIdMonster
                );
                SpawnMonsterAction action = new SpawnMonsterAction(tempMonster,true,
                    0);
                action.update();
                tempMonster.createIntent();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
