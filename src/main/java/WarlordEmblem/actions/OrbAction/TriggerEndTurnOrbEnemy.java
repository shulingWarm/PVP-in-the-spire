package WarlordEmblem.actions.OrbAction;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;

import java.util.Iterator;
import WarlordEmblem.character.ControlMoster;

//针对敌人的触发回合结束时的球操作
public class TriggerEndTurnOrbEnemy extends AbstractGameAction {

    public TriggerEndTurnOrbEnemy() {
    }

    public void update() {
        //实际上需要被处理的敌人
        ControlMoster targetMonster = ControlMoster.instance;
        //这里需要访问的是属于敌人的
        if (!targetMonster.orbs.isEmpty()) {
            Iterator var1 = targetMonster.orbs.iterator();

            while(var1.hasNext()) {
                AbstractOrb o = (AbstractOrb)var1.next();
                o.onEndOfTurn();
            }
        }

        this.isDone = true;
    }
}
