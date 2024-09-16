package WarlordEmblem.relics;

import WarlordEmblem.actions.MultiPauseAction;
import WarlordEmblem.actions.UserCodeAction;
import WarlordEmblem.patches.ActionNetworkPatches;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.unique.CodexAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.NilrysCodex;

//自定义的尼利的宝典
public class UserNiliCodex extends AbstractRelic {

    public static final String ID = "Nilry's Codex";

    public UserNiliCodex() {
        super(ID, "codex.png", AbstractRelic.RelicTier.SPECIAL, AbstractRelic.LandingSound.MAGICAL);
    }

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    public void onPlayerEndTurn() {
        //需要判断玩家是否还活着
        if(AbstractDungeon.player.currentHealth > 0 &&
            (!ActionNetworkPatches.disableCombatTrigger))
        {
            this.addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            this.addToBot(new UserCodeAction());
        }
        //禁止提前开始下回合
        if(!AbstractDungeon.getCurrRoom().skipMonsterTurn)
            MultiPauseAction.pauseStage = true;
    }

    public AbstractRelic makeCopy() {
        return new UserNiliCodex();
    }

}
