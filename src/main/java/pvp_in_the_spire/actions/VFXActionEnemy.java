package pvp_in_the_spire.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class VFXActionEnemy extends AbstractGameAction {

    private AbstractGameEffect effect;
    private float startingDuration;
    private boolean isTopLevelEffect;

    public VFXActionEnemy(AbstractGameEffect effect) {
        this((AbstractCreature)null, effect, 0.0F);
    }

    public VFXActionEnemy(AbstractGameEffect effect, float duration) {
        this((AbstractCreature)null, effect, duration);
    }

    public VFXActionEnemy(AbstractCreature source, AbstractGameEffect effect, float duration) {
        this.isTopLevelEffect = false;
        this.setValues(source, source);
        this.effect = effect;
        this.duration = duration;
        this.startingDuration = duration;
        this.actionType = ActionType.WAIT;
    }

    public VFXActionEnemy(AbstractCreature source, AbstractGameEffect effect, float duration, boolean topLevel) {
        this.isTopLevelEffect = false;
        this.setValues(source, source);
        this.effect = effect;
        this.duration = duration;
        this.startingDuration = duration;
        this.actionType = ActionType.WAIT;
        this.isTopLevelEffect = topLevel;
    }

    public void update() {
        if (this.duration == this.startingDuration) {
            if (this.isTopLevelEffect) {
                AbstractDungeon.topLevelEffects.add(this.effect);
            } else {
                AbstractDungeon.effectList.add(this.effect);
            }
        }

        this.tickDuration();
    }

}
