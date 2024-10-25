package pvp_in_the_spire.patches;

import pvp_in_the_spire.relics.BlockGainer;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.JuggernautPower;
import com.megacrit.cardcrawl.powers.RupturePower;
import com.megacrit.cardcrawl.powers.watcher.MarkPower;

//一些特殊的buff直接把触发逻辑改了就行，没必要写一个新的
public class PowerPatch {

    //修改撕裂的判断逻辑，只有失去生命才能加力量
    @SpirePatch(clz = RupturePower.class, method = "wasHPLost")
    public static class RuptureJudgePatch
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> fix(RupturePower __instance, DamageInfo info, int damageAmount)
        {
            //只有伤害类型是失血的时候才做后续的判断
            if(info.type == DamageInfo.DamageType.HP_LOSS)
                return SpireReturn.Continue();
            return SpireReturn.Return();
        }
    }

    //势不可挡的处理，格挡增益获得的格挡不会触发势不可挡
    @SpirePatch(clz = JuggernautPower.class, method = "onGainedBlock")
    public static class JuggernautTriggerChange
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> fix(JuggernautPower __instance, float blockAmount)
        {
            //如果这次是格挡增益触发的，就不再处理
            if(BlockGainer.inTriggerFlag)
                return SpireReturn.Return();
            return SpireReturn.Continue();
        }
    }

    //点穴的处理，触发点穴伤害的时候不会
    @SpirePatch(clz = MarkPower.class, method = "triggerMarks")
    public static class PowerPressChange
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> fix(MarkPower __instance,
            AbstractCard card)
        {
            if(card.cardID.equals("PathToVictory"))
            {
                //添加伤害信息，不能再失去生命了
                AbstractDungeon.actionManager.addToBottom(
                    new DamageAction(__instance.owner,
                        new DamageInfo(null,__instance.amount, DamageInfo.DamageType.THORNS),
                            AbstractGameAction.AttackEffect.FIRE)
                );
            }
            //完全覆盖之前的实现
            return SpireReturn.Return();
        }
    }

}
