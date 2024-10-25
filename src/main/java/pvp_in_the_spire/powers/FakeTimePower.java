package pvp_in_the_spire.powers;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.TimeWarpTurnEndEffect;

import java.io.DataInputStream;
import java.io.IOException;

//老头的假表，己方只显示个数字，实现功能还是看对面
public class FakeTimePower extends AbstractPower {

    public static final String POWER_ID = "timeEatPowerFake";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESC;
    private int strengthAmount=2;
    private static final int COUNTDOWN_AMT = 16;

    public static FakeTimePower lastInstance=null;

    public FakeTimePower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = "timeEatPowerFake";
        this.owner = owner;
        this.amount = 0;
        this.updateDescription();
        this.loadRegion("time");
        this.type = PowerType.BUFF;
        //上次使用时的实体
        lastInstance=this;
    }

    public void timeUpdate(int newNum)
    {
        //如果没有更新就算了
        if(newNum==this.amount)
            return;
        this.flashWithoutSound();
        this.amount = newNum;
        if (this.amount == 0) {
            this.playApplyPowerSfx();
            CardCrawlGame.sound.play("POWER_TIME_WARP", 0.05F);
            AbstractDungeon.effectsQueue.add(new BorderFlashEffect(Color.GOLD, true));
            AbstractDungeon.topLevelEffectsQueue.add(new TimeWarpTurnEndEffect());
        }

        this.updateDescription();
    }

    //接收新的数据
    public static void receiveUpdate(DataInputStream streamHandle)
    {
        try
        {
            //读取新的数值
            int newNum = streamHandle.readInt();
            //获取假的老头表的power
            if(lastInstance!=null)
            {
                lastInstance.timeUpdate(newNum);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //多次使用时只是增加力量
    public void stackPower(int stackAmount) {
        strengthAmount += 2;
        //更新最近的实例
        lastInstance = this;
        this.updateDescription();
    }

    public void playApplyPowerSfx() {
        CardCrawlGame.sound.play("POWER_TIME_WARP", 0.05F);
    }

    public void updateDescription() {
        this.description = DESC[0] + COUNTDOWN_AMT + DESC[1] + strengthAmount + DESC[2];
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings("timeEatPowerFake");
        NAME = powerStrings.NAME;
        DESC = powerStrings.DESCRIPTIONS;
    }

}
