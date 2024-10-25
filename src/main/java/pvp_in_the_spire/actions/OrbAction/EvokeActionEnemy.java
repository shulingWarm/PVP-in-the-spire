package pvp_in_the_spire.actions.OrbAction;

import pvp_in_the_spire.character.ControlMoster;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;

//球激发时的操作
public class EvokeActionEnemy extends AbstractGameAction {

    private int orbCount;
    //需要处理哪个敌人的充能球
    ControlMoster monster;

    public EvokeActionEnemy(int amount, ControlMoster monster) {
        if (Settings.FAST_MODE) {
            this.duration = Settings.ACTION_DUR_XFAST;
        } else {
            this.duration = Settings.ACTION_DUR_FAST;
        }

        this.duration = this.startDuration;
        this.orbCount = amount;
        this.actionType = ActionType.DAMAGE;
        //记录需要处理的目标敌人
        this.monster = monster;
    }

    public void update() {
        if (this.duration == this.startDuration) {
            for(int i = 0; i < this.orbCount; ++i) {
                //直接额外触发它对应的激发动画，但原版代码其实不是这样实现的
                this.monster.triggerEvokeAnimation(0);
                this.monster.evokeOrb();
            }
        }

        this.tickDuration();
    }

}
