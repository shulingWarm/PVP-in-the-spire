package WarlordEmblem.PlayerManagement;

import WarlordEmblem.Events.EndOfRoundEvent;
import WarlordEmblem.PVPApi.Communication;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.AbstractPower;

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

    //这里什么都不做
    @Override
    public void applyPreTurnLogic() {

    }

    @Override
    public void applyEndOfTurnPowers() {
        //发送end round结束时的处理
        Communication.sendEvent(new EndOfRoundEvent());
        for(AbstractPower eachPower : AbstractDungeon.player.powers)
        {
            eachPower.atEndOfRound();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        this.friendPlayerGroup.render(sb);
    }

    //检查更新友军的hover
    public void checkFriendHover()
    {
        //如果当前没有hover的敌人，从友军这里找一个
        if(this.hoveredMonster == null)
        {
            this.hoveredMonster = friendPlayerGroup.getHoveredMonster();
        }
    }

    @Override
    public void update() {
        super.update();
        this.friendPlayerGroup.update();
        this.checkFriendHover();
    }

    @Override
    public void updateAnimations() {
        super.updateAnimations();
        this.friendPlayerGroup.updateAnimation();
    }
}
