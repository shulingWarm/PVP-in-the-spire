package pvp_in_the_spire.actions.OrbAction;

import pvp_in_the_spire.character.ControlMoster;
import com.megacrit.cardcrawl.actions.AbstractGameAction;

//更新敌人的充能球的显示信息的action
public class UpdateOrbDescriptionAction extends AbstractGameAction {

    public ControlMoster monster;

    public UpdateOrbDescriptionAction(ControlMoster monster)
    {
        this.monster = monster;
    }

    public void update()
    {
        //调用更新
        this.monster.updateOrbDescription();
        this.isDone = true;
    }

}
