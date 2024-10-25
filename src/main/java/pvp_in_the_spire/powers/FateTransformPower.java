package pvp_in_the_spire.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

//观者的预见把状态牌转移给对面的buff
public class FateTransformPower extends AbstractPower {

    public static final String POWER_ID = "fateTransformPower";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESC;

    public FateTransformPower(AbstractCreature owner)
    {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = 0;
        this.updateDescription();
        //借用一下虔信的图片
        this.loadRegion("devotion");
        this.type = PowerType.BUFF;
    }

    //多次使用时什么都不需要做
    public void stackPower(int stackAmount) {
    }

    public void updateDescription() {
        this.description = DESC[0];
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings("burnTransformPower");
        NAME = powerStrings.NAME;
        DESC = powerStrings.DESCRIPTIONS;
    }

}
