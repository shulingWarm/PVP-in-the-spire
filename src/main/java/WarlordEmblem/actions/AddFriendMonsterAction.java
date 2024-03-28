package WarlordEmblem.actions;

import WarlordEmblem.Events.AddMonsterEvent;
import WarlordEmblem.PVPApi.Communication;
import WarlordEmblem.Room.FriendManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import javax.swing.*;
import java.awt.event.ActionEvent;

//添加友军操作的action
public class AddFriendMonsterAction extends AbstractGameAction {

    public AbstractMonster monster;

    public AddFriendMonsterAction(AbstractMonster monster)
    {
        //记录需要添加的友军monster
        this.monster = monster;
    }

    @Override
    public void update() {
        this.isDone = true;
        //如果monster是非法的就直接退出了
        if(this.monster == null)
        {
            return;
        }
        FriendManager manager = FriendManager.instance;
        //获得它应该被添加的位置
        float[] drawXy = manager.getNewMonsterXY();
        //更改即将添加的敌人的位置
        this.monster.drawX = drawXy[0];
        this.monster.drawY = drawXy[1];
        //执行添加敌人的信号
        Communication.sendEvent(new AddMonsterEvent(this.monster,
            manager.id2MonsterMap.size()));
        //添加友军信息
        manager.addFriend(monster);
    }
}
