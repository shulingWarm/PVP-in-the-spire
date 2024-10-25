package pvp_in_the_spire.powers;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;

//假的天罚形态，除了不获得无实体别的都一样
public class FakeGodPunishment2 extends GodPunishmentPower2 {

    public FakeGodPunishment2(AbstractCreature owner, int amount)
    {
        super(owner,amount);
    }

    public void atEndOfTurn(boolean isPlayer) {
        AbstractPlayer player = AbstractDungeon.player;
        //判断这次是否该获得无实体了
        if(obtainThisTime)
        {
            //判断玩家是否有无实体
            if(!player.hasPower(IntangiblePlayerPower.POWER_ID))
            {
                //获得一次无实体
                obtainThisTime = false;
                this.amount--;
            }
        }
        else {
            obtainThisTime = true;
        }
        updateDescription();
    }

}
