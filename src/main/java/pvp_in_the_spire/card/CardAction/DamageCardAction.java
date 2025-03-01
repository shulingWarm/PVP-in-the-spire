package pvp_in_the_spire.card.CardAction;

import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.lwjgl.Sys;
import pvp_in_the_spire.card.AdaptableCard;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DamageCardAction extends AbstractCardAction{

    //伤害的数值
    int amount;
    //伤害的类型
    DamageInfo.DamageType damageType;

    //定义伤害值和伤害类型
    public DamageCardAction(int amount)
    {
        super("Damage");
        this.amount = amount;
        this.damageType = DamageInfo.DamageType.NORMAL;
    }

    @Override
    public boolean tryDirectApply(AbstractCard card) {
        if(card.baseDamage > 0)
        {
            card.baseDamage = this.amount;
            card.damage = this.amount;
            return true;
        }
        return false;
    }

    @Override
    public void saveCardAction(DataOutputStream stream) {
        try {
            stream.writeInt(this.amount);
            //另外记录伤害的类型
            stream.writeUTF(this.damageType.name());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void loadCardAction(DataInputStream stream) {
        try {
            this.amount = stream.readInt();
            this.damageType = DamageInfo.DamageType.valueOf(
                stream.readUTF()
            );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractCardAction copyAction() {
        return new DamageCardAction(this.amount);
    }

    @Override
    public AbstractCardAction makeCopy() {
        return new DamageCardAction(this.amount);
    }

    @Override
    public void adjustRepeatAction(AbstractCardAction action) {
        //把之前的action转换成相同的action
        DamageCardAction tempAction = (DamageCardAction) action;
        //记录它的amount
        amount = tempAction.amount;
    }

    @Override
    public void doCardAction(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new DamageAction(
            m,new DamageInfo(p,this.amount,this.damageType)
        ));
    }
}
