package pvp_in_the_spire.orbs;

import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.OrbStrings;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.Frost;
import com.megacrit.cardcrawl.orbs.Lightning;
import com.megacrit.cardcrawl.orbs.Dark;
import com.megacrit.cardcrawl.vfx.combat.OrbFlareEffect;

//因为不想做新的球类，所以特殊的修改操作都只能用一些外部函数来实现了
public class OrbExternalFunction {

    //常用的4个orb描述
    public static final OrbStrings frostString;
    public static final OrbStrings lightingString;
    public static final OrbStrings darkString;

    //对基本球的应用focus,除了黑球都是这样用的
    public static void applyFocusLighting(Lightning orb, int focusAmount)
    {
        //修改基本的数值
        orb.passiveAmount = Math.max(0, 3 + focusAmount);
        orb.evokeAmount = Math.max(0, 8 + focusAmount);
    }

    //对冰球应用集中
    public static void applyFocusBlock(Frost orb, int focusAmount)
    {
        //修改基本的数值
        orb.passiveAmount = Math.max(0, 2 + focusAmount);
        orb.evokeAmount = Math.max(0, 5 + focusAmount);
    }

    //对黑球应用集中
    public static void applyFocusDark(Dark orb, int focusAmount)
    {
        //修改基本的数值
        orb.passiveAmount = Math.max(0, 6 + focusAmount);
    }

    //黑球的回合结束时的操作，因为黑球会读取玩家的行为，所以目前是这样做的
    public static void darkOnEndOfTurn(Dark orb)
    {
        //处理黑球闪烁的效果
        float speedTime = 0.6F / (float)AbstractDungeon.player.orbs.size();
        if (Settings.FAST_MODE) {
            speedTime = 0.0F;
        }

        AbstractDungeon.actionManager.addToBottom(new VFXAction(new OrbFlareEffect(orb, OrbFlareEffect.OrbFlareColor.DARK), speedTime));
        orb.evokeAmount += orb.passiveAmount;
        orb.description = Dark.DESC[0] + orb.passiveAmount + Dark.DESC[1] + orb.evokeAmount + Dark.DESC[2];
    }

    //对各种球应用focus的通用操作
    public static void applyFocusAny(AbstractOrb orb,int focusAmount)
    {
        //对各种球分别操作
        switch (orb.ID)
        {
            case Lightning.ORB_ID:
                applyFocusLighting((Lightning) orb,focusAmount);
                break;
            case Frost.ORB_ID:
                applyFocusBlock((Frost) orb,focusAmount);
                break;
            case Dark.ORB_ID:
                applyFocusDark((Dark) orb,focusAmount);
                break;
        }
    }

    //对冰球修改描述
    public static void updateDescriptionBlock(Frost orb)
    {
        orb.description = frostString.DESCRIPTION[0] + orb.passiveAmount +
                frostString.DESCRIPTION[1] + orb.evokeAmount + frostString.DESCRIPTION[2];
    }

    //对电球修改描述
    public static void updateDescriptionLighting(Lightning orb)
    {
        orb.description = lightingString.DESCRIPTION[0] + orb.passiveAmount +
                lightingString.DESCRIPTION[1] + orb.evokeAmount + lightingString.DESCRIPTION[2];
    }

    //对黑球修改描述
    public static void updateDescriptionDark(Dark orb)
    {
        orb.description = darkString.DESCRIPTION[0] + orb.passiveAmount +
                darkString.DESCRIPTION[1] + orb.evokeAmount + darkString.DESCRIPTION[2];
    }

    static {
        frostString = CardCrawlGame.languagePack.getOrbString("Frost");
        lightingString = CardCrawlGame.languagePack.getOrbString("Lightning");
        darkString = CardCrawlGame.languagePack.getOrbString("Dark");
    }

}
