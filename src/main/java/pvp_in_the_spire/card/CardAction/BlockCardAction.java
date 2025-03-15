package pvp_in_the_spire.card.CardAction;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//获得卡牌格挡的action
public class BlockCardAction extends AbstractCardAction {

    //具体的格挡值
    public int blockAmount;

    public BlockCardAction(int blockAmount) {
        super("BlockCardAction");
        this.blockAmount = blockAmount;
    }

    @Override
    public void doCardAction(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(
            new GainBlockAction(p, this.blockAmount)
        );
    }

    @Override
    public void saveCardAction(DataOutputStream stream) {
        //记录格挡值
        try {
            stream.writeInt(this.blockAmount);
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
            this.blockAmount = stream.readInt();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean tryDirectApply(AbstractCard card) {
        //判断卡牌里面是不是本来就有格挡
        if(card.baseBlock > 0)
        {
            card.baseBlock = this.blockAmount;
            card.block = this.blockAmount;
            return true;
        }
        return false;
    }

    @Override
    public AbstractCardAction makeCopy() {
        return new BlockCardAction(this.blockAmount);
    }
}
