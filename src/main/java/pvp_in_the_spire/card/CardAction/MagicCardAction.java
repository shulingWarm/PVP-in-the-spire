package pvp_in_the_spire.card.CardAction;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MagicCardAction extends AbstractCardAction{

    public int magicNumber;

    public MagicCardAction(int magicNumber)
    {
        super("MagicCardAction");
        this.magicNumber = magicNumber;
    }

    @Override
    public void doCardAction(AbstractPlayer p, AbstractMonster m) {
        //不做任何action
    }

    @Override
    public void saveCardAction(DataOutputStream stream) {
        try {
            stream.writeInt(this.magicNumber);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void loadCardAction(DataInputStream stream) {
        try
        {
            this.magicNumber = stream.readInt();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void adjustRepeatAction(AbstractCardAction action) {
        MagicCardAction tempAction = (MagicCardAction) action;
        this.magicNumber = tempAction.magicNumber;
    }

    @Override
    public boolean tryDirectApply(AbstractCard card) {
        if(card.baseMagicNumber > 0)
        {
            card.magicNumber = this.magicNumber;
            card.baseMagicNumber = this.magicNumber;
            return true;
        }
        return false;
    }

    @Override
    public AbstractCardAction makeCopy() {
        return new MagicCardAction(this.magicNumber);
    }
}
