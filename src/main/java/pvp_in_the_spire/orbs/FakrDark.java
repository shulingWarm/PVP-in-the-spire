package pvp_in_the_spire.orbs;

import pvp_in_the_spire.character.ControlMoster;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.Dark;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.OrbFlareEffect;

//仅仅用于敌人显示的假黑球
public class FakrDark extends Dark {

    public FakrDark()
    {
        super();
    }

    //更新显示的时候是按照敌人的集中数量来显示的
    @Override
    public void applyFocus() {
        if(ControlMoster.instance == null)
            return;
        //获取敌人的集中数量
        AbstractPower power = ControlMoster.instance.getPower("Focus");
        if (power != null) {
            this.passiveAmount = Math.max(0, this.basePassiveAmount + power.amount);
        } else {
            this.passiveAmount = this.basePassiveAmount;
        }

    }

    //修改球的数值显示，单纯就是一个数字的显示，因为所有的数据都由对面来维护，
    //因为这里并不需要处理focus
    @Override
    public void updateDescription() {
        this.description = DESC[0] + this.passiveAmount + DESC[1] + this.evokeAmount + DESC[2];
    }

    //子类的黑球操作，虽然行为是一样的，但放到子类里面来实现就不会触发通信操作
    @Override
    public void onEndOfTurn() {
        float speedTime = 0.6F / (float)AbstractDungeon.player.orbs.size();
        if (Settings.FAST_MODE) {
            speedTime = 0.0F;
        }

        AbstractDungeon.actionManager.addToBottom(new VFXAction(new OrbFlareEffect(this, OrbFlareEffect.OrbFlareColor.DARK), speedTime));
        this.evokeAmount += this.passiveAmount;
        //黑球的处理会让对面来通知处理，自己不需要主动处理
        //this.updateDescription();
    }
}
