package WarlordEmblem.PlayerManagement;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

//敌人管理器的manager
//这跟父类的区别主要是除了管理敌人，还会额外渲染我方玩家
public class MonsterGroupManager extends MonsterGroup {

    //友军的管理信息
    FriendPlayerGroup friendPlayerGroup = null;

    public MonsterGroupManager(AbstractMonster[] input)
    {
        super(input);
    }

    public void setFriendPlayerGroup(FriendPlayerGroup friendPlayerGroup)
    {
        this.friendPlayerGroup = friendPlayerGroup;
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        this.friendPlayerGroup.render(sb);
    }

    @Override
    public void update() {
        super.update();
        this.friendPlayerGroup.update();
    }

    @Override
    public void updateAnimations() {
        super.updateAnimations();
        this.friendPlayerGroup.updateAnimation();
    }
}
