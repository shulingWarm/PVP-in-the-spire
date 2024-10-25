package pvp_in_the_spire.powers;

import pvp_in_the_spire.AutomaticSocketServer;
import pvp_in_the_spire.actions.FightProtocol;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.TimeWarpTurnEndEffect;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;

//真正的时间吞噬的power
public class TimeEatPower extends AbstractPower {

    public static final String POWER_ID = "timeEatPowerReal";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESC;
    private int strengthAmount=2;
    private static final int COUNTDOWN_AMT = 16;

    public TimeEatPower(AbstractCreature owner) {
        this.name = NAME;
        this.ID = "timeEatPowerReal";
        this.owner = owner;
        this.amount = 0;
        this.updateDescription();
        this.loadRegion("time");
        this.type = AbstractPower.PowerType.BUFF;
    }

    public void playApplyPowerSfx() {
        CardCrawlGame.sound.play("POWER_TIME_WARP", 0.05F);
    }

    public void updateDescription() {
        this.description = DESC[0] + COUNTDOWN_AMT + DESC[1] + strengthAmount + DESC[2];
    }

    public static void sendUpdateTimeNum(int newNum)
    {
        AutomaticSocketServer server = AutomaticSocketServer.getServer();
        DataOutputStream streamHandle = server.streamHandle;
        try
        {
            //发送数据头
            streamHandle.writeInt(FightProtocol.TIME_EAT_UPDATE);
            //发送数值
            streamHandle.writeInt(newNum);
            //把数据发出去
            server.send();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //多次使用的时候会增加力量
    public void stackPower(int stackAmount) {
        strengthAmount += 2;
        this.updateDescription();
    }

    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        this.flashWithoutSound();
        ++this.amount;
        if (this.amount == COUNTDOWN_AMT) {
            this.amount = 0;
            this.playApplyPowerSfx();
            AbstractDungeon.actionManager.callEndTurnEarlySequence();
            CardCrawlGame.sound.play("POWER_TIME_WARP", 0.05F);
            AbstractDungeon.effectsQueue.add(new BorderFlashEffect(Color.GOLD, true));
            AbstractDungeon.topLevelEffectsQueue.add(new TimeWarpTurnEndEffect());
            Iterator var3 = AbstractDungeon.getMonsters().monsters.iterator();

            while(var3.hasNext()) {
                AbstractMonster m = (AbstractMonster)var3.next();
                this.addToBot(new ApplyPowerAction(m, m, new StrengthPower(m, strengthAmount), strengthAmount));
            }
        }

        //当触发这个操作的时候，通知对面更新数值
        sendUpdateTimeNum(this.amount);

        this.updateDescription();
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings("timeEatPowerReal");
        NAME = powerStrings.NAME;
        DESC = powerStrings.DESCRIPTIONS;
    }

}
