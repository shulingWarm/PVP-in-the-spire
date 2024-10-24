package pvp_in_the_spire.ui.BattleUI;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;

import java.util.ArrayList;
import java.util.Collections;

public class OrbManager {

    //最大球位
    public int maxOrbs;

    //是否需要被渲染，有时候虽然有球位，但并不需要渲染
    public boolean renderFlag = false;

    //球位的列表
    public ArrayList<AbstractOrb> orbs;

    public OrbManager()
    {
        this.orbs = new ArrayList<>();
    }

    float getOrbTx(float dist,float angle,float drawX)
    {
        return -dist * MathUtils.cosDeg(angle) + drawX;
    }

    //针对敌人本体的setSlot,它和对玩家的操作是不一样的
    public void setSlot(AbstractOrb orb,int slotNum,int maxOrbs,
        float drawX,float drawY,float hb_h
    )
    {
        float dist = 160.0F * Settings.scale + (float)maxOrbs * 10.0F * Settings.scale;
        float angle = 100.0F + (float)maxOrbs * 12.0F;
        float offsetAngle = angle / 2.0F;
        angle *= (float)slotNum / ((float)maxOrbs - 1.0F);
        angle += 90.0F - offsetAngle;
        //这里是玩家orb和敌方orb的关键区别

        orb.tX = getOrbTx(dist,angle,drawX);
        orb.tY = dist * MathUtils.sinDeg(angle) + drawY + hb_h / 2.0F;
        if (maxOrbs == 1) {
            orb.tX = drawX;
            orb.tY = 160.0F * Settings.scale + drawY + hb_h / 2.0F;
        }

        orb.hb.move(orb.tX, orb.tY);
    }

    //增加球位的数量
    //添加充能球栏位的操作
    public void increaseMaxOrbSlots(int amount, boolean playSfx,
        float drawX,float drawY,float hb_h,float hb_x,float hb_y
    ) {

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
        float xCenter = drawX + hb_x;
        float yCenter = drawY + hb_y + hb_h / 2.0F;

        int i;
        for(i = 0; i < amount; ++i) {
            this.orbs.add(new EmptyOrbSlot(xCenter,yCenter));
        }

        for(i = 0; i < this.orbs.size(); ++i) {
            //这里需要使用特殊的setSlot,默认函数里面的setSlot只能对玩家使用
            setSlot(this.orbs.get(i),i,this.maxOrbs,drawX,drawY,hb_h);
        }
    }

    //减少球位的操作
    //其实这个函数只能用来把球数减1
    public void decreaseMaxOrbSlots(int amount,float drawX,
                float drawY,float hb_h) {
        if (this.maxOrbs > 0) {
            this.maxOrbs -= amount;
            if (this.maxOrbs < 0) {
                this.maxOrbs = 0;
            }

            if (!this.orbs.isEmpty()) {
                this.orbs.remove(this.orbs.size() - 1);
            }

            for(int i = 0; i < this.orbs.size(); ++i) {
                setSlot(this.orbs.get(i),i,this.maxOrbs,drawX,drawY,hb_h);
            }
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
    public void updateOrbAnimation(AbstractOrb orb,float animX,float animY)
    {
        //临时记录它的中心位置，禁止它更新中心位置
        float saveX = orb.cX;
        float saveY = orb.cY;
        //调用动画效果的更新
        orb.updateAnimation();
        //按照敌人的位置来修改这个更新效果
        orb.cX = MathHelper.orbLerpSnap(saveX, animX + orb.tX);
        orb.cY = MathHelper.orbLerpSnap(saveY, animY + orb.tY);
    }

    public boolean channelOrb(AbstractOrb orbToSet,float drawX,float drawY,
           float hb_h
    ) {
        //渲染球位
        this.renderFlag = true;

        if (this.maxOrbs > 0) {

            int index = -1;

            int plasmaCount;
            for(plasmaCount = 0; plasmaCount < this.orbs.size(); ++plasmaCount) {
                if (this.orbs.get(plasmaCount) instanceof EmptyOrbSlot) {
                    index = plasmaCount;
                    break;
                }
            }

            //如果找不到可以放的位置就直接退出就行，先激发一个再放球这个操作对面会控制完成的
            if (index != -1) {
                orbToSet.cX = this.orbs.get(index).cX;
                orbToSet.cY = this.orbs.get(index).cY;
                this.orbs.set(index, orbToSet);
                //setSlot需要使用针对敌人单位的setSlot
                setSlot(this.orbs.get(index),index,this.maxOrbs,drawX,drawY,hb_h);
                orbToSet.playChannelSFX();
                return true;
            }

        }
        return false;
    }

    //激发充能球的操作
    public void evokeOrb(float drawX,float drawY,float hb_h) {
        if (!this.orbs.isEmpty() && !(this.orbs.get(0) instanceof EmptyOrbSlot)) {
            //球的攻击操作都是由对方来触发的，这里的激发只需要把球删除了就行
            AbstractOrb orbSlot = new EmptyOrbSlot();

            //把空的球槽换到最后面
            int i;
            for(i = 1; i < this.orbs.size(); ++i) {
                Collections.swap(this.orbs, i, i - 1);
            }

            this.orbs.set(this.orbs.size() - 1, orbSlot);

            for(i = 0; i < this.orbs.size(); ++i) {
                //依次调用setSlot但需要调用敌人版本的激发
                setSlot(orbs.get(i),i,this.maxOrbs,
                        drawX,drawY,hb_h);
            }
        }

    }

    public void update(float animX,float animY)
    {
        //调用每个充能球执行动画更新的效果
        for(AbstractOrb eachOrb : orbs)
        {
            eachOrb.update();
            //关于充能球的动画更新需要换了，不能使用这个
            //但也不想做特殊的调用，不如调用一个静态函数
            updateOrbAnimation(eachOrb,animX,animY);
        }
    }
}
