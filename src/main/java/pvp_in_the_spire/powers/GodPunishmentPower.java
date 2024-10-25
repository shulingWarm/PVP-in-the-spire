package pvp_in_the_spire.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;

//天罚形态
public class GodPunishmentPower extends AbstractPower {

    public static final String POWER_ID = "godPunishment";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESC;

    //生效的回合数
    public int triggerTime;

    public GodPunishmentPower(AbstractCreature owner,int amount)
    {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.triggerTime = amount;
        this.updateDescription();
        this.loadRegion("wraithForm");
        this.type = PowerType.BUFF;
    }

    //多次使用时什么都不需要做
    public void stackPower(int stackAmount) {
        //判断是不是更小的trigger
        if(stackAmount<triggerTime)
        {
            triggerTime=stackAmount;
            updateDescription();
        }
        this.amount=triggerTime;
        updateDescription();
    }

    public void atStartOfTurn() {
        //减少amount
        this.amount--;
        if(this.amount<=0)
        {
            //获得一次无实体
            AbstractPlayer player = AbstractDungeon.player;
            AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(player,player,new IntangiblePlayerPower(player,1))
            );
            //恢复回合数
            this.amount = triggerTime;
        }
        updateDescription();
    }

    public void updateDescription() {
        this.description = DESC[0] + this.triggerTime +
            DESC[1] + this.amount + DESC[2];
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings("godPunishment");
        NAME = powerStrings.NAME;
        DESC = powerStrings.DESCRIPTIONS;
    }

}
