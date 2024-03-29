package WarlordEmblem.relics;

import WarlordEmblem.actions.UserCodeAction;
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
        this.addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
        this.addToBot(new UserCodeAction());
    }

    public AbstractRelic makeCopy() {
        return new UserNiliCodex();
    }

}
