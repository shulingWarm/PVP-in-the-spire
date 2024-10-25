package pvp_in_the_spire.powers;

import pvp_in_the_spire.patches.ActionNetworkPatches;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.TimeWarpTurnEndEffect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//debuff版本的老头表
//把power上给每个玩家，然后拥有这个power的玩家打牌的时候会执行相应的打牌计数
public class TimeWarpDebuff extends CommunicatePower {

    public static final String POWER_ID = "TimeWarpDebuff";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESC;

    //用于判断是否需要计数
    public boolean needCount;

    //剩余触发次数
    public int triggerTime;
    //每回合的最多出牌数
    public int maxCardNum;

    //构造函数，需要指定操作目标和层数
    public TimeWarpDebuff(AbstractCreature creature, int amount)
    {
        this.name = NAME;
        this.ID = POWER_ID + amount;
        this.loadRegion("time");
        //这里和之前的时间扭曲不一样，这里是debuff
        this.type = PowerType.DEBUFF;
        //记录本地的owner
        this.owner = creature;
        this.amount = amount;
        //记录剩余触发次数为1
        this.triggerTime = 1;
        this.maxCardNum = amount;
        //是否执行出牌计数取决于owner是否为玩家
        this.needCount = (creature == AbstractDungeon.player);
        this.updateDescription();
    }

    @Override
    public String getMapId() {
        return POWER_ID;
    }

    public void updateDescription() {
        this.description = DESC[0] + this.maxCardNum + DESC[1] + triggerTime + DESC[2];
    }

    @Override
    public void atStartOfTurn() {
        //将amount设置为待出牌的数据
        this.amount = maxCardNum;
        updateDescription();
    }

    @Override
    public void setAmount(int newAmount, boolean sendFlag) {
        super.setAmount(newAmount, sendFlag);
        if(this.amount <= 0)
        {
            this.triggerTime--;
        }
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        //如果不需要计数，这里是不用动的
        if(!needCount)
        {
            return;
        }
        //更新power的数值
        this.setAmount(this.amount-1,true);
        //如果amount到0的时候就强制结束出牌
        if(this.amount <= 0)
        {
            this.playApplyPowerSfx();
            //强制提前结束回合
            AbstractDungeon.actionManager.callEndTurnEarlySequence();
            CardCrawlGame.sound.play("POWER_TIME_WARP", 0.05F);
            AbstractDungeon.effectsQueue.add(new BorderFlashEffect(Color.GOLD, true));
            AbstractDungeon.topLevelEffectsQueue.add(new TimeWarpTurnEndEffect());
            //减少待触发的次数
            if(triggerTime <= 0)
            {
                this.powerManager.removePower(
                    this.getCommunicateId(),true
                );
            }
        }
        this.updateDescription();
    }

    @Override
    public void encode(DataOutputStream stream) {
        try
        {
            //对当前的owner做编码
            ActionNetworkPatches.creatureEncode(
                stream,this.owner,true
            );
            stream.writeInt(this.maxCardNum);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public CommunicatePower decode(DataInputStream stream) {
        //解码出临时的creature
        AbstractCreature tempCreature = ActionNetworkPatches.creatureDecode(
            stream,false
        );
        if(tempCreature == null)
            return null;
        //读取层数
        try
        {
            int powerAmount = stream.readInt();
            return new TimeWarpDebuff(tempCreature,powerAmount);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings("TimeWarpDebuff");
        NAME = powerStrings.NAME;
        DESC = powerStrings.DESCRIPTIONS;
    }
}
