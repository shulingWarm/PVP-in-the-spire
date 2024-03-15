package WarlordEmblem.powers;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.HexPower;

//假的邪咒，只是用来显示的
public class FakeHexPower extends RealHexPower {

    public FakeHexPower(AbstractCreature owner, int amount)
    {
        super(owner,amount);
    }

    //显示端不需要对此生效
    public void onUseCard(AbstractCard card, UseCardAction action) {

    }

}
