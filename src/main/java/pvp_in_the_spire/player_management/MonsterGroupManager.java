package pvp_in_the_spire.player_management;

import pvp_in_the_spire.events.EndOfRoundEvent;
import pvp_in_the_spire.pvp_api.Communication;
import pvp_in_the_spire.character.PlayerMonster;
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
    //目前正在渲染手牌的player
    public PlayerMonster currentRenderCardPlayer = null;

    public MonsterGroupManager(AbstractMonster[] input)
    {
        super(input);
        //遍历每个monster
        for(AbstractMonster monster : input)
        {
            //寻找第一个player monster
            if(monster instanceof PlayerMonster)
            {
                this.currentRenderCardPlayer = (PlayerMonster) monster;
                this.currentRenderCardPlayer.setRenderCard(true);
                break;
            }
        }
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
        //判断是否需要更新渲染的monster
        if(this.hoveredMonster != this.currentRenderCardPlayer &&
                this.hoveredMonster instanceof PlayerMonster)
        {
            this.currentRenderCardPlayer.setRenderCard(false);
            this.currentRenderCardPlayer = (PlayerMonster) this.hoveredMonster;
            this.currentRenderCardPlayer.setRenderCard(true);
        }
    }

    @Override
    public void updateAnimations() {
        super.updateAnimations();
        this.friendPlayerGroup.updateAnimation();
    }
}
