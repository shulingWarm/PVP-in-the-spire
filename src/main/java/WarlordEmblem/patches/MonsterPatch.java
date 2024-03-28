package WarlordEmblem.patches;

import WarlordEmblem.Events.MonsterIntentChangeEvent;
import WarlordEmblem.PVPApi.Communication;
import WarlordEmblem.Room.FriendManager;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

//对敌人行为的监测，方便实时更新双边意图
public class MonsterPatch {

    //截取monster更改意图的事件
    @SpirePatch(clz = AbstractMonster.class,method = "setMove",
        paramtypez = {String.class,byte.class, AbstractMonster.Intent.class,
        int.class,int.class,boolean.class})
    public static class MonsterSetMovePatch
    {
        @SpirePrefixPatch
        public static void fix(AbstractMonster __instance,
           String moveName, byte nextMove, AbstractMonster.Intent intent, int baseDamage, int multiplier, boolean isMultiDamage)
        {
            int idMonster = FriendManager.instance.getIdByMonster(__instance);
            //判断敌人是否有映射关系
            if(idMonster >= 0)
            {
                //发送意图更改的事件
                Communication.sendEvent(
                    new MonsterIntentChangeEvent(idMonster,intent)
                );
            }
        }
    }

}
