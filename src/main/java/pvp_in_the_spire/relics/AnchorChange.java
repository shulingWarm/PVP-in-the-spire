package pvp_in_the_spire.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Anchor;

//修改过的锚，更改它的触发时机
public class AnchorChange extends Anchor {

    //改成不在战斗开始时触发
    public void atBattleStart() {

    }

    //改成在第一回合的抽牌后触发
    public void atTurnStartPostDraw() {
        //判断是否生效过
        if(this.grayscale)
        {
            return;
        }
        //否则按照战斗开始的逻辑来处理
        super.atBattleStart();
    }

    //修改遗物的patch 把遗物池里面的锚换了
    @SpirePatch(clz = Anchor.class,method = "makeCopy")
    public static class ChangeRelicPatch
    {
        @SpirePrefixPatch
        public static SpireReturn<AbstractRelic> fix(Anchor __instance)
        {
            return SpireReturn.Return(new AnchorChange());
        }
    }


    public AbstractRelic makeCopy() {
        return new AnchorChange();
    }

}
