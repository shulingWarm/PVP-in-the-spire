package pvp_in_the_spire.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

import java.util.Iterator;

//属于敌人的获得格挡的操作
public class GainBlockEnemy extends AbstractGameAction {

    private static final float DUR = 0.25F;

    public GainBlockEnemy(AbstractCreature target, int amount) {
        this.target = target;
        this.amount = amount;
        this.actionType = ActionType.BLOCK;
        this.duration = 0.25F;
        this.startDuration = 0.25F;
    }

    public GainBlockEnemy(AbstractCreature target, AbstractCreature source, int amount) {
        this.setValues(target, source, amount);
        this.actionType = ActionType.BLOCK;
        this.duration = 0.25F;
        this.startDuration = 0.25F;
    }

    public GainBlockEnemy(AbstractCreature target, int amount, boolean superFast) {
        this(target, amount);
        if (superFast) {
            this.duration = this.startDuration = Settings.ACTION_DUR_XFAST;
        }

    }

    public GainBlockEnemy(AbstractCreature target, AbstractCreature source, int amount, boolean superFast) {
        this(target, source, amount);
        if (superFast) {
            this.duration = this.startDuration = Settings.ACTION_DUR_XFAST;
        }

    }

    public void update() {
        if (!this.target.isDying && !this.target.isDead && this.duration == this.startDuration) {
            AbstractDungeon.effectList.add(new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY, AttackEffect.SHIELD));
            this.target.addBlock(this.amount);
            Iterator var1 = AbstractDungeon.player.hand.group.iterator();

            while(var1.hasNext()) {
                AbstractCard c = (AbstractCard)var1.next();
                c.applyPowers();
            }
        }

        this.tickDuration();
    }

}
