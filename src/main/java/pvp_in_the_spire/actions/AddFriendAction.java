package pvp_in_the_spire.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class AddFriendAction extends AbstractGameAction {

    //即将添加的敌人
    public AbstractMonster monster;

    //添加敌方单位的action
    public AddFriendAction(AbstractMonster monster)
    {
        //记录即将被添加的敌人
        this.monster = monster;
    }


    @Override
    public void update() {
        //在monsters里面添加敌人
        AbstractDungeon.getCurrRoom().monsters.addMonster(monster);
    }
}
