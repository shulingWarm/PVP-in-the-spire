package WarlordEmblem.powers;

import WarlordEmblem.card.GodPunishment;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;

//假的天罚形态，不获得无实体，只做显示
public class FakeGodPunishment extends GodPunishmentPower {

    public FakeGodPunishment(AbstractCreature owner, int amount)
    {
        super(owner,amount);
    }

    public void atStartOfTurn() {
        //减少amount
        this.amount--;
        if(this.amount<=0)
        {
            //恢复回合数
            this.amount = triggerTime;
        }
        updateDescription();
    }

}
