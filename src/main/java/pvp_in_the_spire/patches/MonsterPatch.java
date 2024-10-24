package pvp_in_the_spire.patches;

import pvp_in_the_spire.events.DamageOnMonsterEvent;
import pvp_in_the_spire.pvp_api.Communication;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

//对敌人行为的监测，方便实时更新双边意图
public class MonsterPatch {

    //截取monster更改意图的事件
//    @SpirePatch(clz = AbstractMonster.class,method = "setMove",
//        paramtypez = {String.class,byte.class, AbstractMonster.Intent.class,
//        int.class,int.class,boolean.class})
//    public static class MonsterSetMovePatch
//    {
//        @SpirePrefixPatch
//        public static void fix(AbstractMonster __instance,
//           String moveName, byte nextMove, AbstractMonster.Intent intent, int baseDamage, int multiplier, boolean isMultiDamage)
//        {
//            int idMonster = FriendManager.instance.getIdByMonster(__instance);
//            //判断敌人是否有映射关系
//            if(idMonster >= 0)
//            {
//                if(!isMultiDamage)
//                    multiplier = 1;
//                //发送意图更改的事件
//                Communication.sendEvent(
//                    new MonsterIntentChangeEvent(idMonster,baseDamage,multiplier,intent)
//                );
//            }
//        }
//    }

    //触发monster伤害的事件，怪物受到伤害时，通知敌方友军执行伤害事件
    @SpirePatch(clz = AbstractMonster.class, method = "damage")
    public static class MonsterOnDamagePatch
    {
        @SpirePrefixPatch
        public static void fix(AbstractMonster __instance,
           DamageInfo info)
        {
            //判断现在是否需要传导伤害
            if(ActionNetworkPatches.stopSendAttack)
                return;
            //发送伤害事件
            Communication.sendEvent(new DamageOnMonsterEvent(__instance,info));
        }
    }

    //为了debug,监测一下kaka的行动
//    @SpirePatch(clz = Cultist.class,method = "takeTurn")
//    public static class LookKakaTurn
//    {
//        @SpirePrefixPatch
//        public static void fix(Cultist __instance)
//        {
//            //打印下一个行动
//            System.out.println("kaka take turn!!!");
//            System.out.println(__instance.nextMove);
//        }
//    }

}
