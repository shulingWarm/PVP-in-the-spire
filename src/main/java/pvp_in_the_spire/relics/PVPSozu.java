package pvp_in_the_spire.relics;

import basemod.abstracts.CustomRelic;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Sozu;

//pvp里面的添水
public class PVPSozu extends CustomRelic {

    public static final String ID = "PVPSozu";

    public PVPSozu() {
        super(ID, "sozu.png", RelicTier.BOSS, LandingSound.FLAT);
    }

    public String getUpdatedDescription() {
        return AbstractDungeon.player != null ? this.setDescription(AbstractDungeon.player.chosenClass) : this.setDescription((AbstractPlayer.PlayerClass)null);
    }

    private String setDescription(AbstractPlayer.PlayerClass c) {
        return this.DESCRIPTIONS[1] + this.DESCRIPTIONS[0];
    }

    public void updateDescription(AbstractPlayer.PlayerClass c) {
        this.description = this.setDescription(c);
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
    }

    @Override
    public void atTurnStart() {
        //回合开始时，如果没有药水，获得一费
        for(AbstractPotion eachPotion : AbstractDungeon.player.potions)
        {
            if(!(eachPotion instanceof PotionSlot))
                return;
        }
        AbstractDungeon.actionManager.addToBottom(
            new GainEnergyAction(1)
        );
    }

    public void onEquip() {
        //药水栏位-1
        AbstractPlayer player = AbstractDungeon.player;
        player.potionSlots--;
        player.potions.remove(player.potions.size()-1);
    }

    public AbstractRelic makeCopy() {
        return new PVPSozu();
    }

}
