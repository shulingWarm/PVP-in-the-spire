package WarlordEmblem.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.CuriosityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

//虚假的好奇power 自己这一端只是个显示
public class FakeCuriosPower extends CuriosityPower {

    public FakeCuriosPower(AbstractCreature owner, int amount)
    {
        super(owner,amount);
    }

    //重新实现它的使用牌的操作，使用牌的时候不要触发这个东西
    public void onUseCard(AbstractCard card, UseCardAction action) {

    }

}
