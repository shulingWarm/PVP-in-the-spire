package pvp_in_the_spire.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;

//另一种天罚形态，每2回合生效一次，最多生效M次
public class GodPunishmentPower2 extends AbstractPower {

    public static final String POWER_ID = "godPunishment";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESC;

    //生效的回合数
    public int triggerTime;

    //判断是否该获得无实体了
    public boolean obtainThisTime = true;

    public GodPunishmentPower2(AbstractCreature owner, int amount)
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
        this.amount += stackAmount;
    }

    public void atEndOfTurn(boolean isPlayer) {
        AbstractPlayer player = AbstractDungeon.player;
        //判断这次是否该获得无实体了
        if(obtainThisTime)
        {
            //判断玩家是否有无实体
            if(!player.hasPower(IntangiblePlayerPower.POWER_ID))
            {
                //获得一次无实体
                obtainThisTime = false;
                AbstractDungeon.actionManager.addToBottom(
                        new ApplyPowerAction(player,player,new IntangiblePlayerPower(player,1))
                );
                this.amount--;
            }
        }
        else {
            obtainThisTime = true;
        }
        if(this.amount<=0)
        {
            //移除这个power
            AbstractDungeon.actionManager.addToBottom(
                new RemoveSpecificPowerAction(player,player,POWER_ID)
            );
        }
        updateDescription();
    }

    public void updateDescription() {
        this.description = DESC[0] + this.amount +
                DESC[1];
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings("godPunishment2");
        NAME = powerStrings.NAME;
        DESC = powerStrings.DESCRIPTIONS;
    }

}
