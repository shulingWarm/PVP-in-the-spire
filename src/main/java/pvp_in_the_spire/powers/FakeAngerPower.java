package pvp_in_the_spire.powers;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AngerPower;

//假的猛男power,只是做个显示
public class FakeAngerPower extends AngerPower {

    public FakeAngerPower(AbstractCreature owner, int amount)
    {
        super(owner,amount);
    }

    public void onUseCard(AbstractCard card, UseCardAction action) {

    }

}
