package pvp_in_the_spire.powers;

import pvp_in_the_spire.actions.PoisonLoseHpActionCanBlock;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import java.util.ArrayList;

//可以被格挡的毒 并且是回合结束时生效
public class BlockablePoisonPower extends AbstractPower {

    public static final String POWER_ID = "Poison";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    private AbstractCreature source;

    public BlockablePoisonPower(AbstractCreature owner, AbstractCreature source, int poisonAmt) {
        this.name = NAME;
        this.ID = "Poison";
        this.owner = owner;
        this.source = source;
        this.amount = poisonAmt;
        if (this.amount >= 9999) {
            this.amount = 9999;
        }

        this.updateDescription();
        this.loadRegion("poison");
        this.type = PowerType.DEBUFF;
        this.isTurnBased = true;
    }

    public void playApplyPowerSfx() {
        CardCrawlGame.sound.play("POWER_POISON", 0.05F);
    }

    public void updateDescription() {
        if (this.owner != null && !this.owner.isPlayer) {
            this.description = DESCRIPTIONS[2] + this.amount + DESCRIPTIONS[1];
        } else {
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
        }

    }

    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        if (this.amount > 98 && AbstractDungeon.player.chosenClass == AbstractPlayer.PlayerClass.THE_SILENT) {
            UnlockTracker.unlockAchievement("CATALYST");
        }

    }

    //施加毒伤害，确保在格挡移除之前触发
    public void addDamage() {
        if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && !AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            this.flashWithoutSound();
            this.addToTop(new PoisonLoseHpActionCanBlock(this.owner, this.source, this.amount, AbstractGameAction.AttackEffect.POISON));
        }
        --this.amount;
        //如果减到零了就移除
        if(this.amount<=0)
        {
            this.addToBot(new RemoveSpecificPowerAction(this.owner,this.owner,POWER_ID));
        }
        updateDescription();
    }

    //检查某个敌方单位是否有毒
    public static void checkAddPoisonDamage(AbstractCreature creature)
    {
        //遍历它的所有power
        for(AbstractPower eachPower : creature.powers)
        {
            if(eachPower instanceof BlockablePoisonPower)
            {
                //调用施加伤害
                ((BlockablePoisonPower)eachPower).addDamage();
            }
        }
    }

    //检查所有的敌方单位有没有毒，如果有的话，调用施加伤害
    public static void checkAddPoisonDamage()
    {
        ArrayList<AbstractMonster> monsterList =
                AbstractDungeon.getCurrRoom().monsters.monsters;
        for(AbstractMonster eachMonster : monsterList)
        {
            checkAddPoisonDamage(eachMonster);
        }
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings("Poison");
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }

}
