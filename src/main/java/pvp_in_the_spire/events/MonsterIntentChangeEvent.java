package pvp_in_the_spire.events;

import pvp_in_the_spire.pvp_api.BaseEvent;
import pvp_in_the_spire.room.FriendManager;
import pvp_in_the_spire.character.FriendMonster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//敌人的意图更改的事件
public class MonsterIntentChangeEvent extends BaseEvent {

    public int idMonster;
    public AbstractMonster.Intent newIntent;
    //记录基础伤害值和伤害次数
    public int baseDamage;
    public int damageTimes;

    public MonsterIntentChangeEvent(int idMonster,
        int baseDamage,
        int damageTimes,
        AbstractMonster.Intent newIntent)
    {
        this.idMonster = idMonster;
        this.newIntent = newIntent;
        //设置事件的id
        this.eventId = "IntentChange";
        //记录伤害值
        this.baseDamage = baseDamage;
        this.damageTimes = damageTimes;
    }

    public MonsterIntentChangeEvent()
    {
        this(0, 0,0,
            AbstractMonster.Intent.DEBUG);
    }

    //对事件的编码
    @Override
    public void encode(DataOutputStream streamHandle) {
        System.out.println("sending intent");
        System.out.println(this.newIntent.name());
        try
        {
            //写入monster的id
            streamHandle.writeInt(idMonster);
            streamHandle.writeUTF(this.newIntent.name());
            //记录伤害数值
            streamHandle.writeInt(baseDamage);
            streamHandle.writeInt(damageTimes);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //解码敌人的意图变化
    @Override
    public void decode(DataInputStream streamHandle) {
        System.out.println("receive intent");
        try
        {
            //读取更改意图的monster
            int idMonster = streamHandle.readInt();
            //读取敌人的新意图
            String intentStr = streamHandle.readUTF();
            //从FriendManager里面解码monster
            FriendMonster tempMonster =
                FriendManager.instance.getMonsterById(idMonster);
            //获取具体的意图
            AbstractMonster.Intent intent = AbstractMonster.Intent.valueOf(intentStr);
            //读取基础伤害和伤害次数
            int tempBase = streamHandle.readInt();
            int tempTimes = streamHandle.readInt();
            if(tempMonster != null && tempMonster.judgeValid())
            {
                //获取monster的实体
                AbstractMonster monster = tempMonster.getMonster();
                if(monster != null)
                {
                    //写入意图
                    monster.setMove((byte)1,intent,tempBase,tempTimes,tempTimes>1);
                    monster.createIntent();
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
