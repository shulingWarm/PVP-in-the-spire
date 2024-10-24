package pvp_in_the_spire.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.exordium.Cultist;

//用来显示我方召唤的敌人的动画的
public class MonsterBox extends CreatureBox {

    //用来被渲染的敌人
    public AbstractMonster monster;

    public MonsterBox()
    {
        //随便初始化一个敌人，就是测试一下显示效果
        monster = new Cultist((float)Settings.WIDTH/2,
                (float)Settings.HEIGHT/2);
        //初始化怪物的动画
        monster.init();
        monster.drawX = (float)Settings.WIDTH/2;
        monster.drawY = (float)Settings.HEIGHT/2;
        monster.applyPowers();
        monster.showHealthBar();
        monster.createIntent();
        monster.usePreBattleAction();
    }

    //传入抽象的敌人的初始化，上面的空初始化函数仅仅是个测试
    public MonsterBox(AbstractMonster monster)
    {
        //记录monster
        this.monster = monster;
        this.monster.init();
        this.monster.showHealthBar();
        this.monster.flipHorizontal = true;
        //设置意图
        this.monster.rollMove();
        this.monster.applyPowers();
    }

    //获取实体沉浸的位置
    @Override
    public float[] getLocation() {
        return new float[]{this.monster.drawX,
            this.monster.drawY};
    }

    //更新操作
    public void update() {
        monster.update();
    }

    //渲染操作
    public void render(SpriteBatch sb) {
        monster.render(sb);
    }

    @Override
    public AbstractCreature getCreature() {
        return this.monster;
    }
}
