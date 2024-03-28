package WarlordEmblem.Events;

import WarlordEmblem.PVPApi.BaseEvent;
import WarlordEmblem.Room.FriendManager;
import WarlordEmblem.character.FriendMonster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//敌人的意图更改的事件
public class MonsterIntentChangeEvent extends BaseEvent {

    public int idMonster;
    public AbstractMonster.Intent newIntent;

    public MonsterIntentChangeEvent(int idMonster,
        AbstractMonster.Intent newIntent)
    {
        this.idMonster = idMonster;
        this.newIntent = newIntent;
        //设置事件的id
        this.eventId = "IntentChange";
    }

    public MonsterIntentChangeEvent()
    {
        this(0, AbstractMonster.Intent.DEBUG);
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
            String intent = streamHandle.readUTF();
            //从FriendManager里面解码monster
            FriendMonster tempMonster =
                FriendManager.instance.getMonsterById(idMonster);
            if(tempMonster != null && tempMonster.judgeValid())
            {
                //获取monster的实体
                AbstractMonster monster = tempMonster.getMonster();
                if(monster != null)
                {
                    //写入意图
                    monster.setMove((byte)1,
                        AbstractMonster.Intent.valueOf(intent));
                    monster.createIntent();
                    System.out.printf("write intent %s\n",intent);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
