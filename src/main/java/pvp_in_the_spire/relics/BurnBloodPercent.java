package pvp_in_the_spire.relics;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.BurningBlood;

//燃烧之血换成百分比的形式
public class BurnBloodPercent{

    public static final RelicStrings relicStrings = CardCrawlGame.languagePack.getRelicStrings(BurningBlood.ID);
    public static final RelicStrings percentString = CardCrawlGame.languagePack.getRelicStrings("BloodPercent");

    //更改燃烧之血的描述
    @SpirePatch(clz = BurningBlood.class, method = "getUpdatedDescription")
    public static class DescriptionChange
    {
        @SpirePrefixPatch
        public static SpireReturn<String> fix(BurningBlood __instance)
        {
            return SpireReturn.Return(
                relicStrings.DESCRIPTIONS[0] + percentString.DESCRIPTIONS[0] + relicStrings.DESCRIPTIONS[1]
            );
        }
    }

    //更改实际的回血效果
    @SpirePatch(clz = BurningBlood.class, method = "onVictory")
    public static class ChangeHealNum
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> fix(BurningBlood __instance)
        {
            __instance.flash();
            AbstractPlayer player = AbstractDungeon.player;
            //头上显示遗物
            AbstractDungeon.actionManager.addToTop(
                new RelicAboveCreatureAction(player,__instance)
            );
            if(player.currentHealth>0)
            {
                //回复6%的血量
                player.heal((int) (0.1f * player.maxHealth));
            }
            return SpireReturn.Return();
        }
    }

}
