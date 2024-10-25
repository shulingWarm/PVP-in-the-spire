package pvp_in_the_spire.events;

import pvp_in_the_spire.pvp_api.BaseEvent;
import pvp_in_the_spire.room.FriendManager;
import pvp_in_the_spire.character.ControlMoster;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//自己的友军受到伤害时触发的事件
public class DamageOnMonsterEvent extends BaseEvent {

    //受到伤害的目标monster
    public AbstractMonster targetMonster;
    //对目标敌人的伤害信息
    public DamageInfo info;

    public DamageOnMonsterEvent(AbstractMonster monster, DamageInfo info)
    {
        this.targetMonster = monster;
        this.info = info;
        //记录事件id
        this.eventId = "DamageOnMonster";
    }

    public DamageOnMonsterEvent()
    {
        this(null,null);
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        try
        {
            int idMonster = FriendManager.instance.getIdByMonster(this.targetMonster);
            //检查monster id的有效性
            if(idMonster < 0)
            {
                System.out.println("Unknown monster id");
                return;
            }
            //写入目标monster
            streamHandle.writeInt(idMonster);
            //伤害来源一定是当前游戏中的player,所以伤害来源不用解析
            //记录伤害种类
            streamHandle.writeUTF(info.type.name());
            //记录伤害的数值
            streamHandle.writeInt(info.base);
            //记录真实的伤害值
            streamHandle.writeInt(info.output);
            System.out.printf("send damage %s %d\n",targetMonster.name,info.output);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //解码操作
    @Override
    public void decode(DataInputStream streamHandle) {
        try
        {
            //读取敌人的id
            int idMonster = streamHandle.readInt();
            //读取伤害精英
            DamageInfo.DamageType damageType = DamageInfo.DamageType.valueOf(
                streamHandle.readUTF()
            );
            //读取伤害的基础数值
            int baseDamage = streamHandle.readInt();
            //读取伤害的最终输出数值
            int outputDamage = streamHandle.readInt();
            //新建伤害信息
            DamageInfo tempInfo = new DamageInfo(
                damageType == DamageInfo.DamageType.NORMAL ?
                    ControlMoster.instance : null,
                baseDamage,damageType
            );
            //记录输出的伤害值
            tempInfo.output = outputDamage;
            System.out.printf("execute damage %s %d\n",
                    FriendManager.instance.getMonsterById(idMonster).creatureBox.getCreature().name,
                    outputDamage);
            //获取对应的monster实体并执行伤害
            FriendManager.instance.getMonsterById(idMonster).damage(tempInfo);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
