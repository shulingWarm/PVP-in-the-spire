package pvp_in_the_spire.patches;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.actions.TransformCardAction;
import pvp_in_the_spire.character.PlayerMonster;
import pvp_in_the_spire.powers.FateTransformPower;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.utility.ScryAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

//和卡组转移相关的patch
public class CardGroupPatch {

    //用来标记目前是否正在处理预见相关的逻辑
    @SpirePatch(clz = ScryAction.class,method = "update")
    public static class LabelScryProcess
    {

        //标记开始执行预见
        public static boolean onScry=false;

        //用于判断目前是否正处于预见执行期间
        @SpirePrefixPatch
        public static void prefix(ScryAction __instance)
        {
            onScry = true;
        }

        //预见的逻辑执行结束后再退出这个标记
        @SpirePostfixPatch
        public static void postfix(ScryAction __instance)
        {
            onScry = false;
        }

    }

    //处理将牌移动到弃牌堆的逻辑
    //用于服务观者的弃置状态牌相关的操作
    @SpirePatch(clz = CardGroup.class,method = "moveToDiscardPile")
    public static class WatcherTransformPatch
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> fix(CardGroup __instance, AbstractCard c)
        {
            //判断目前是否牌预见阶段
            //如果是预见阶段并且是状态牌的话，就把牌送出去
            if(LabelScryProcess.onScry && c.type== AbstractCard.CardType.STATUS
            && AbstractDungeon.player.hasPower(FateTransformPower.POWER_ID))
            {
                PlayerMonster randMonster = GlobalManager.getBattleInfo().getRandEnemy();
                if(randMonster != null)
                {
                    //阻止这个操作的正常生效，而是把这个牌转移给对面
                    AbstractDungeon.actionManager.addToBottom(
                            new TransformCardAction(c,__instance,1,randMonster)
                    );
                }
                return SpireReturn.Return();
            }
            //其它情况下正常执行
            return SpireReturn.Continue();
        }

    }

}
