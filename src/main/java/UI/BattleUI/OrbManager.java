package UI.BattleUI;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;

import java.util.ArrayList;

public class OrbManager {

    //最大球位
    public int maxOrbs;

    //球的显示位置
    public float drawX;
    public float drawY;
    public float hb_x;
    public float hb_y;
    public float hb_h;
    public float animX;
    public float animY;

    //是否需要被渲染，有时候虽然有球位，但并不需要渲染
    public boolean renderFlag = false;

    //球位的列表
    public ArrayList<AbstractOrb> orbs;

    public OrbManager()
    {
        this.orbs = new ArrayList<>();
    }

    //针对敌人本体的setSlot,它和对玩家的操作是不一样的
    public void setSlot(AbstractOrb orb,int slotNum,int maxOrbs)
    {
        float dist = 160.0F * Settings.scale + (float)maxOrbs * 10.0F * Settings.scale;
        float angle = 100.0F + (float)maxOrbs * 12.0F;
        float offsetAngle = angle / 2.0F;
        angle *= (float)slotNum / ((float)maxOrbs - 1.0F);
        angle += 90.0F - offsetAngle;
        orb.tX = -dist * MathUtils.cosDeg(angle) + this.drawX;
        orb.tY = dist * MathUtils.sinDeg(angle) + this.drawY + this.hb_h / 2.0F;
        if (maxOrbs == 1) {
            orb.tX = this.drawX;
            orb.tY = 160.0F * Settings.scale + this.drawY + this.hb_h / 2.0F;
        }

        orb.hb.move(orb.tX, orb.tY);
    }

    //增加球位的数量
    //添加充能球栏位的操作
    public void increaseMaxOrbSlots(int amount, boolean playSfx) {

        //如果是0就不用处理了
        if(amount == 0)
            return;
        //如果已经有10个了就不用扩充了
        if(this.maxOrbs >= 10)
            return;

        //如果扩充之后超过10,就只扩充到10
        if(this.maxOrbs + amount > 10)
        {
            amount = 10 - this.maxOrbs;
        }

        if (playSfx) {
            CardCrawlGame.sound.play("ORB_SLOT_GAIN", 0.1F);
        }

        this.maxOrbs += amount;

        //添加球时的中心点
        float xCenter = this.drawX + this.hb_x;
        float yCenter = this.drawY + this.hb_y + this.hb_h / 2.0F;

        int i;
        for(i = 0; i < amount; ++i) {
            this.orbs.add(new EmptyOrbSlot(xCenter,yCenter));
        }

        for(i = 0; i < this.orbs.size(); ++i) {
            //这里需要使用特殊的setSlot,默认函数里面的setSlot只能对玩家使用
            setSlot(this.orbs.get(i),i,this.maxOrbs);
        }
    }

    public void render(SpriteBatch sb)
    {
        if(!renderFlag)
            return;
        for(AbstractOrb eachOrb : this.orbs)
        {
            eachOrb.render(sb);
        }
    }

    //对充能球的动画更新
    public void updateOrbAnimation(AbstractOrb orb)
    {
        //临时记录它的中心位置，禁止它更新中心位置
        float saveX = orb.cX;
        float saveY = orb.cY;
        //调用动画效果的更新
        orb.updateAnimation();
        //按照敌人的位置来修改这个更新效果
        orb.cX = MathHelper.orbLerpSnap(saveX, this.animX + orb.tX);
        orb.cY = MathHelper.orbLerpSnap(saveY, this.animY + orb.tY);
    }

    public void update()
    {
        //调用每个充能球执行动画更新的效果
        for(AbstractOrb eachOrb : orbs)
        {
            eachOrb.update();
            //关于充能球的动画更新需要换了，不能使用这个
            //但也不想做特殊的调用，不如调用一个静态函数
            updateOrbAnimation(eachOrb);
        }
    }
}
