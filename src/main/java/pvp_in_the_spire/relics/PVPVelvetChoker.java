package pvp_in_the_spire.relics;

import basemod.abstracts.CustomRelic;
import com.megacrit.cardcrawl.actions.unique.LoseEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;

//pvp里面的狗圈
public class PVPVelvetChoker extends CustomRelic {

    public static final String ID = "PVPVelvetChoker";

    public PVPVelvetChoker() {
        super(ID, "redChoker.png", RelicTier.BOSS, LandingSound.FLAT);
    }

    public String getUpdatedDescription() {
        return AbstractDungeon.player != null ? this.setDescription(AbstractDungeon.player.chosenClass) : this.setDescription((AbstractPlayer.PlayerClass)null);
    }

    private String setDescription(AbstractPlayer.PlayerClass c) {
        return this.DESCRIPTIONS[0] + this.DESCRIPTIONS[1] + 6 +
                this.DESCRIPTIONS[2];
    }

    public void updateDescription(AbstractPlayer.PlayerClass c) {
        this.description = setDescription(c);
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
    }

    public void onEquip() {
        ++AbstractDungeon.player.energy.energyMaster;
    }

    public void onUnequip() {
        --AbstractDungeon.player.energy.energyMaster;
    }

    public void atBattleStart() {
        this.counter = 0;
    }

    public void atTurnStart() {
        if(this.counter > 6)
        {
            AbstractDungeon.actionManager.addToBottom(new LoseEnergyAction(1));
        }
        this.counter = 0;
    }

    public void onPlayCard(AbstractCard card, AbstractMonster m) {
        ++this.counter;
        if (this.counter == 6) {
            this.flash();
        }
    }

    public void onVictory() {
        this.counter = -1;
    }

    public AbstractRelic makeCopy() {
        return new PVPVelvetChoker();
    }
}
