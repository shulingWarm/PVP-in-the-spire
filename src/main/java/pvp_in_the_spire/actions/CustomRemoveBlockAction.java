package pvp_in_the_spire.actions;

import pvp_in_the_spire.events.MelterEvent;
import pvp_in_the_spire.pvp_api.Communication;
import pvp_in_the_spire.character.PlayerMonster;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;

//移除block用到的action
//这个东西生效的时候会发生移除block的操作
public class CustomRemoveBlockAction extends RemoveAllBlockAction {

    public CustomRemoveBlockAction(AbstractCreature target, AbstractCreature source)
    {
        super(target,source);
    }

    @Override
    public void update() {
        if (!this.target.isDying && !this.target.isDead && this.duration == 0.25F && this.target.currentBlock > 0) {
            //判断是不是player monster
            if(target instanceof PlayerMonster)
            {
                PlayerMonster tempMonster = (PlayerMonster) target;
                tempMonster.forceLoseBlock(false);
                //发送移除格挡的事件
                Communication.sendEvent(new MelterEvent(tempMonster));
            }
            else {
                target.loseBlock();
            }
        }

        this.tickDuration();
    }
}
