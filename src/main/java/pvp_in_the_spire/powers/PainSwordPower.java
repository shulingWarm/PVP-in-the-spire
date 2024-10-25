package pvp_in_the_spire.powers;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

//这个是小手的buff,区别是自己的视角看到的是假的，对方的视角下看到的才是真的
public class PainSwordPower extends AbstractPower {

    public static final String POWER_ID = "Painful Stabs";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    public PainSwordPower(AbstractCreature owner,int amount) {
        this.name = NAME;
        this.ID = "Painful Stabs";
        this.owner = owner;
        this.amount = amount;
        this.updateDescription();
        this.loadRegion("painfulStabs");
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    public void onInflictDamage(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (damageAmount > 0 && info.type == DamageInfo.DamageType.NORMAL) {
            this.addToBot(new MakeTempCardInDiscardAction(new Wound(), 1));
        }

    }

    public void atStartOfTurn() {
        //判断是不是需要删除
        if(this.amount<=1)
        {
            AbstractDungeon.actionManager.addToBottom(
                    new RemoveSpecificPowerAction(this.owner,this.owner,POWER_ID)
            );
        }
        else {
            //将power数量减1
            AbstractDungeon.actionManager.addToBottom(
                    new ReducePowerAction(this.owner,this.owner,POWER_ID,1)
            );
        }
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings("Painful Stabs");
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }

}
