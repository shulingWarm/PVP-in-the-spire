package pvp_in_the_spire.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.PotionHelper;

import java.util.ArrayList;

//截取随机获取药水的列表，从根源上禁止某些药水的获取
public class PotionPatch {

    //获取药水的函数
    //从源头上彻底禁止某些药水
    @SpirePatch(clz= PotionHelper.class,method = "getPotions")
    public static class GetPotionChange
    {
        @SpirePrefixPatch
        public static SpireReturn<ArrayList<String>> fix(AbstractPlayer.PlayerClass c, boolean getAll)
        {
            ArrayList<String> retVal = new ArrayList();
            if (!getAll) {
                switch (c) {
                    case IRONCLAD:
                        //retVal.add("BloodPotion"); 禁用血药
                        retVal.add("ElixirPotion");
                        retVal.add("HeartOfIron");
                        break;
                    case THE_SILENT:
                        retVal.add("Poison Potion");
                        retVal.add("CunningPotion");
                        retVal.add("GhostInAJar");
                        break;
                    case DEFECT:
                        retVal.add("FocusPotion");
                        retVal.add("PotionOfCapacity");
                        retVal.add("EssenceOfDarkness");
                        break;
                    case WATCHER:
                        retVal.add("BottledMiracle");
                        retVal.add("StancePotion");
                        retVal.add("Ambrosia");
                }
            } else {
                //retVal.add("BloodPotion");
                retVal.add("ElixirPotion");
                retVal.add("HeartOfIron");
                retVal.add("Poison Potion");
                retVal.add("CunningPotion");
                retVal.add("GhostInAJar");
                retVal.add("FocusPotion");
                retVal.add("PotionOfCapacity");
                retVal.add("EssenceOfDarkness");
                retVal.add("BottledMiracle");
                retVal.add("StancePotion");
                retVal.add("Ambrosia");
            }


            retVal.add("BloodPotion");//把血药改成所有角色共有
            retVal.add("Block Potion");
            retVal.add("Dexterity Potion");
            retVal.add("Energy Potion");
            retVal.add("Explosive Potion");
            retVal.add("Fire Potion");
            retVal.add("Strength Potion");
            retVal.add("Swift Potion");
            retVal.add("Weak Potion");
            retVal.add("FearPotion");
            retVal.add("AttackPotion");
            retVal.add("SkillPotion");
            retVal.add("PowerPotion");
            retVal.add("ColorlessPotion");
            retVal.add("SteroidPotion");
            retVal.add("SpeedPotion");
            retVal.add("BlessingOfTheForge");
            retVal.add("Regen Potion");
            retVal.add("Ancient Potion");
            retVal.add("LiquidBronze");
            retVal.add("GamblersBrew");
            retVal.add("EssenceOfSteel");
            retVal.add("DuplicationPotion");
            retVal.add("DistilledChaos");
            retVal.add("LiquidMemories");
            retVal.add("CultistPotion");
            retVal.add("Fruit Juice");
            retVal.add("SneckoOil");
            //retVal.add("FairyPotion"); 瓶装精灵
            //retVal.add("SmokeBomb"); 烟雾弹
            retVal.add("EntropicBrew");
            return SpireReturn.Return(retVal);
        }
    }

}
