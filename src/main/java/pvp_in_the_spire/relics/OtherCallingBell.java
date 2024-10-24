package pvp_in_the_spire.relics;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.curses.CurseOfTheBell;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

//为了解决拿到铃铛后不给遗物的bug,重新实现铃铛
public class OtherCallingBell extends AbstractRelic {

    public static final String ID = "Calling Bell";
    private boolean cardsReceived = true;

    public OtherCallingBell() {
        super("Calling Bell", "bell.png", RelicTier.BOSS, LandingSound.SOLID);
    }

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    public void onEquip() {
        this.cardsReceived = false;
        CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        AbstractCard bellCurse = new CurseOfTheBell();
        UnlockTracker.markCardAsSeen(bellCurse.cardID);
        group.addToBottom(bellCurse.makeCopy());
        AbstractDungeon.gridSelectScreen.openConfirmationGrid(group, this.DESCRIPTIONS[1]);
        CardCrawlGame.sound.playA("BELL", MathUtils.random(-0.2F, -0.3F));
    }

    public void update() {
        super.update();
        if (!this.cardsReceived) {
            AbstractDungeon.combatRewardScreen.reopen();
            AbstractDungeon.combatRewardScreen.rewards.clear();
            AbstractDungeon.combatRewardScreen.rewards.add(new RewardItem(AbstractDungeon.returnRandomScreenlessRelic(RelicTier.COMMON)));
            AbstractDungeon.combatRewardScreen.rewards.add(new RewardItem(AbstractDungeon.returnRandomScreenlessRelic(RelicTier.UNCOMMON)));
            AbstractDungeon.combatRewardScreen.rewards.add(new RewardItem(AbstractDungeon.returnRandomScreenlessRelic(RelicTier.RARE)));
            AbstractDungeon.combatRewardScreen.positionRewards();
            AbstractDungeon.overlayMenu.proceedButton.setLabel(this.DESCRIPTIONS[2]);
            this.cardsReceived = true;
            AbstractDungeon.getCurrRoom().rewardPopOutTimer = 0.25F;
        }

        if (this.hb.hovered && InputHelper.justClickedLeft) {
            CardCrawlGame.sound.playA("BELL", MathUtils.random(-0.2F, -0.3F));
            this.flash();
        }

    }

    //截取铃铛的复制，改为复制这个
//    @SpirePatch(clz = CallingBell.class, method = "makeCopy")
//    public static class ChangeCopy
//    {
//        @SpirePrefixPatch
//        public static SpireReturn<AbstractRelic> fix(CallingBell __instance)
//        {
//            return SpireReturn.Return(new OtherCallingBell());
//        }
//    }

    public AbstractRelic makeCopy() {
        return new OtherCallingBell();
    }
}
