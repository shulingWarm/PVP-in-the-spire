package WarlordEmblem.character;

import UI.CreatureBox;
import UI.MonsterBox;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

//友军敌人，它里面应该另外包含MonsterBox
public class FriendMonster {

    //敌人的显示框，正常传入敌人的时候就用这个类，但如果后面要扩展2v2的时候就要用子类了
    public CreatureBox creatureBox;

    //传入一个抽象的敌人，然后把它显示到框里
    public FriendMonster(AbstractMonster monster)
    {
        //初始化显示框
        creatureBox = new MonsterBox(monster);
    }

    //获取里面的monster实体
    public AbstractMonster getMonster()
    {
        AbstractCreature creature = this.creatureBox.getCreature();
        if(creature instanceof AbstractMonster)
        {
            return (AbstractMonster) creature;
        }
        return null;
    }

    //判断是否已经死亡或无效，目前只考虑不死亡的情况
    public boolean judgeValid()
    {
        return true;
    }

    //获取它的位置
    public float[] getLocation()
    {
        return this.creatureBox.getLocation();
    }

    //更新操作
    public void update() {
        creatureBox.update();
    }

    //渲染操作
    public void render(SpriteBatch sb) {
        creatureBox.render(sb);
    }

}
