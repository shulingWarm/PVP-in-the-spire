package pvp_in_the_spire.powers;

import pvp_in_the_spire.ui.TextureManager;
import pvp_in_the_spire.patches.ActionNetworkPatches;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.ArrayList;

//药丸的buff,其实是一个窗口，把身上的负面效果移除一回合
public class PelletsPower extends AbstractPower {

    public static final String POWER_ID = "PelletsPower";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESC;

    //目标身上所有负面效果的容器
    public ArrayList<AbstractPower> powerList = new ArrayList<>();

    public PelletsPower(AbstractCreature target)
    {
        //临时记录一个正向效果，到最后直接更换就可以了
        ArrayList<AbstractPower> goodPower = new ArrayList<>();
        //遍历它的每个power
        for(AbstractPower eachPower : target.powers)
        {
            if(eachPower.type == PowerType.DEBUFF)
            {
                powerList.add(eachPower);
            }
            else {
                goodPower.add(eachPower);
            }
        }
        //直接覆盖原始的power
        target.powers = goodPower;
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = target;
        this.amount = 0;
        this.updateDescription();
        //buff的图标
        this.region128 = new TextureAtlas.AtlasRegion(TextureManager.PELLETS_BUFF_128,
            0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(TextureManager.PELLETS_BUFF_48,
            0, 0, 32, 32);
        this.type = PowerType.DEBUFF;
    }

    public void updateDescription() {
        StringBuilder tempBuilder = new StringBuilder();
        tempBuilder.append(DESC[0]);
        //遍历每一种buff
        for(AbstractPower eachPower : this.powerList)
        {
            tempBuilder.append(" NL ");
            tempBuilder.append(eachPower.name);
            if(eachPower.amount != 0)
            {
                tempBuilder.append(" *");
                tempBuilder.append(eachPower.amount);
            }
        }
        this.description = tempBuilder.toString();
    }

    //在回合开始时，把负面效果叠加回去
    @Override
    public void atStartOfTurn() {
        //这种情况下不需要发送buff信息
        ActionNetworkPatches.BuffInfoSend.stopTrigger = true;
        //移除自身
        addToTop(new RemoveSpecificPowerAction(this.owner,this.owner,this));
        //遍历每一种power
        for(AbstractPower eachPower : powerList)
        {
            addToTop(new ApplyPowerAction(this.owner,this.owner,eachPower));
        }
        ActionNetworkPatches.BuffInfoSend.stopTrigger = false;
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings("PelletsPower");
        NAME = powerStrings.NAME;
        DESC = powerStrings.DESCRIPTIONS;
    }

}
