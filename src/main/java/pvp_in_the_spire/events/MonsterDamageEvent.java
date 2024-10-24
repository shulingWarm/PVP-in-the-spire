package pvp_in_the_spire.events;

import pvp_in_the_spire.pvp_api.BaseEvent;
import pvp_in_the_spire.room.FriendManager;
import pvp_in_the_spire.character.ControlMoster;
import pvp_in_the_spire.patches.ActionNetworkPatches;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//monster对玩家的伤害事件
public class MonsterDamageEvent extends BaseEvent {

    public DamageInfo info;

    public MonsterDamageEvent(DamageInfo info)
    {
        this.info = info;
        this.eventId = "MonsterDamage";
    }

    public MonsterDamageEvent()
    {
        this(null);
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        try
        {
            //写入伤害来源
            streamHandle.writeInt(
                FriendManager.instance.getIdByMonster((AbstractMonster) this.info.owner)
            );
            //传入伤害类型
            streamHandle.writeUTF(this.info.type.name());
            //传入基础伤害
            streamHandle.writeInt(this.info.base);
            //传入最后实际的伤害
            streamHandle.writeInt(this.info.output);
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
            //读取伤害来源
            int idMonster = streamHandle.readInt();
            //读取伤害类型
            DamageInfo.DamageType damageType = DamageInfo.DamageType.valueOf(
                streamHandle.readUTF()
            );
            //读取伤害的基础数值
            int baseDamage = streamHandle.readInt();
            //读取最终伤害的输出数值
            int outputDamage = streamHandle.readInt();
            //新建伤害信息
            DamageInfo tempInfo = new DamageInfo(
                FriendManager.instance.getMonsterById(idMonster).getMonster(),
                baseDamage,damageType
            );
            //修改临时的伤害信息的输出数值
            tempInfo.output = outputDamage;
            //构造对controlMonster的伤害
            ActionNetworkPatches.stopSendAttack = true;
            ControlMoster.instance.damage(tempInfo);
            ActionNetworkPatches.stopSendAttack = false;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
