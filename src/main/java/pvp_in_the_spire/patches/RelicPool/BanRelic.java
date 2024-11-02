package pvp_in_the_spire.patches.RelicPool;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

//其实是一个patch,把那些在pvp里面不适合的遗物禁用掉
public class BanRelic {

    //禁用指定的遗物
    @SpirePatch(clz = UnlockTracker.class,method = "isRelicLocked")
    public static class RemoveRelicInPool
    {
        @SpirePrefixPatch
        public static SpireReturn<Boolean> fix(String key)
        {
            //不能被添加的遗物
            switch (key)
            {
                case SlaversCollar.ID:
                case BlackStar.ID:
                case JuzuBracelet.ID:
                case PreservedInsect.ID:
                case GremlinHorn.ID:
                case Pantograph.ID:
                case SingingBowl.ID:
                case Courier.ID:
                case PaperCrane.ID:
                case Ginger.ID:
                case PrayerWheel.ID:
                case Torii.ID:
                case TungstenRod.ID:
                case Turnip.ID:
                case WingBoots.ID:
                case TheSpecimen.ID:
                case MembershipCard.ID:
                case Sling.ID:
                case Sozu.ID:
                case VelvetChoker.ID: //狗圈
                case Ectoplasm.ID: //绿帽
                case OrangePellets.ID: //药丸
                case LizardTail.ID: //尾巴
                //case Brimstone.ID: //硫磺
                return SpireReturn.Return(true);
            }
            return SpireReturn.Continue();
        }

    }

}
