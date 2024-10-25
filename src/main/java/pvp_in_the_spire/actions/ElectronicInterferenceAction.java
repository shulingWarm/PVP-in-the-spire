package pvp_in_the_spire.actions;

import pvp_in_the_spire.powers.FakeDropEnergy;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

//电磁干扰的action,用于给对方叠buff
public class ElectronicInterferenceAction extends AbstractGameAction {

    private boolean freeToPlayOnce = false;
    private boolean upgraded;
    private AbstractPlayer p;
    public AbstractMonster targetMonster;
    private int energyOnUse;

    public ElectronicInterferenceAction(AbstractPlayer p,
        AbstractMonster m, boolean freeToPlayOnce, int energyOnUse, boolean upgraded) {
        this.p = p;
        //记录敌人目标
        this.targetMonster = m;
        this.freeToPlayOnce = freeToPlayOnce;
        this.duration = Settings.ACTION_DUR_XFAST;
        this.actionType = ActionType.SPECIAL;
        this.energyOnUse = energyOnUse;
        this.upgraded = upgraded;
    }

    public void update() {
        int effect = EnergyPanel.totalCount;
        if (this.energyOnUse != -1) {
            effect = this.energyOnUse;
        }

        if (this.p.hasRelic("Chemical X")) {
            effect += 2;
            this.p.getRelic("Chemical X").flash();
        }

        if (this.upgraded) {
            ++effect;
        }

        if (effect > 0) {
            this.addToBot(new ApplyPowerAction(
                targetMonster,p,new FakeDropEnergy(targetMonster,effect),effect
            ));
            if (!this.freeToPlayOnce) {
                this.p.energy.use(EnergyPanel.totalCount);
            }
        }

        this.isDone = true;
    }

}
