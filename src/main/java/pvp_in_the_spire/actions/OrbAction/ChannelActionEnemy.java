package pvp_in_the_spire.actions.OrbAction;

import pvp_in_the_spire.character.ControlMoster;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;

import java.util.Iterator;

//针对敌人的action操作
public class ChannelActionEnemy extends AbstractGameAction {

    private AbstractOrb orbType;
    private boolean autoEvoke;
    //记录需要被操作的是谁的充能球
    ControlMoster targetMonster;

    public ChannelActionEnemy(AbstractOrb newOrbType, ControlMoster monster) {
        this(newOrbType,monster, true);
    }

    public ChannelActionEnemy(AbstractOrb newOrbType,ControlMoster monster, boolean autoEvoke) {
        this.autoEvoke = false;
        this.duration = Settings.ACTION_DUR_FAST;
        this.orbType = newOrbType;
        this.autoEvoke = autoEvoke;
        //记录要操作的充能球是哪个充能球
        targetMonster = monster;
    }

    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            if (this.autoEvoke) {
                targetMonster.channelOrb(this.orbType);
            } else {
                Iterator var1 = targetMonster.orbs.iterator();

                while(var1.hasNext()) {
                    AbstractOrb o = (AbstractOrb)var1.next();
                    if (o instanceof EmptyOrbSlot) {
                        targetMonster.channelOrb(this.orbType);
                        break;
                    }
                }
            }

            if (Settings.FAST_MODE) {
                this.isDone = true;
                return;
            }
        }

        this.tickDuration();
    }

}
