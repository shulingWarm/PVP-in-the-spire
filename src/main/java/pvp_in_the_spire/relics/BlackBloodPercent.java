package pvp_in_the_spire.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.BlackBlood;

//改黑血的处理逻辑
public class BlackBloodPercent {

    public static final RelicStrings relicStrings = CardCrawlGame.languagePack.getRelicStrings(BlackBlood.ID);

    //更改燃烧之血的描述
    @SpirePatch(clz = BlackBlood.class, method = "getUpdatedDescription")
    public static class DescriptionChange
    {
        @SpirePrefixPatch
        public static SpireReturn<String> fix(BlackBlood __instance)
        {
            return SpireReturn.Return(
                    relicStrings.DESCRIPTIONS[0] + BurnBloodPercent.percentString.DESCRIPTIONS[1]
                        + relicStrings.DESCRIPTIONS[1]
            );
        }
    }

    //更改实际的回血效果
    @SpirePatch(clz = BlackBlood.class, method = "onVictory")
    public static class ChangeHealNum
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> fix(BlackBlood __instance)
        {
            __instance.flash();
            AbstractPlayer player = AbstractDungeon.player;
            //头上显示遗物
            AbstractDungeon.actionManager.addToTop(
                    new RelicAboveCreatureAction(player,__instance)
            );
            if(player.currentHealth>0)
            {
                //回复12%的血量
                player.heal((int) (0.2f * player.maxHealth));
            }
            return SpireReturn.Return();
        }
    }

}
