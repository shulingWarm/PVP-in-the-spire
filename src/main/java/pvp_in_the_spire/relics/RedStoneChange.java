package pvp_in_the_spire.relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.PhilosopherStone;

import java.util.Iterator;

//改版之后的红石，它有不一样的触发时机
public class RedStoneChange extends PhilosopherStone {

    //记录一下刚刚触发过
    public boolean justStart=false;

    public void atBattleStart() {
        //记录刚刚触发过
        justStart = true;
    }

    //回合开始时触发相应的操作
    public void atTurnStartPostDraw() {
        //如果需要触发就给对面加一次力量
        if(justStart)
        {
            justStart=false;
            Iterator var1 = AbstractDungeon.getMonsters().monsters.iterator();

            while(var1.hasNext()) {
                AbstractMonster m = (AbstractMonster)var1.next();
                this.addToTop(new RelicAboveCreatureAction(m, this));
                this.addToBot(new ApplyPowerAction(m,AbstractDungeon.player,
                    new StrengthPower(m,1),1));
            }
        }
    }

    public AbstractRelic makeCopy() {
        return new RedStoneChange();
    }

}
