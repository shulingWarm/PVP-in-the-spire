package WarlordEmblem.character;

import UI.BattleUI.OrbManager;
import UI.BattleUI.OrbManagerInvert;
import WarlordEmblem.actions.MultiPauseAction;
import WarlordEmblem.orbs.MonsterOrb;
import WarlordEmblem.orbs.OrbExternalFunction;
import WarlordEmblem.patches.ActionNetworkPatches;
import basemod.abstracts.CustomMonster;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FocusPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.stances.AbstractStance;
import com.megacrit.cardcrawl.stances.NeutralStance;
import com.megacrit.cardcrawl.vfx.combat.BlockedWordEffect;
import com.megacrit.cardcrawl.vfx.combat.StrikeEffect;

import java.util.Iterator;

//新时代的control monster
//这是为了适应多人联机对战的情况下实现的monster
//之前的ControlMonster里面的屎山太多了
public class PlayerMonster extends AbstractMonster {

    //球位管理器
    public OrbManager orbManager;
    //尾巴的数量
    public int tailNum;
    //用于实际被渲染的角色
    public AbstractPlayer renderPlayer;

    //用于判断当前的monster是否负责做pause操作
    //它是在敌方回合阻塞玩家的出牌的
    public boolean pauseFlag = false;
    //是否有外卡钳
    public boolean hasCaliper = false;

    //姿态
    public AbstractStance stance;

    //玩家的tag
    public int playerTag;

    public PlayerMonster(boolean pauseFlag,float x,float y,int playerTag,boolean sameTeam)
    {
        super("test","PlayerMonster",10,0, 0, 180.0F, 240.0F, (String)null,x,y);
        //随便载入一个贴图，用于演示基本的人物效果
        //这是为了确保渲染父类的时候不报错
        this.loadAnimation("images/characters/watcher/idle/skeleton.atlas", "images/characters/watcher/idle/skeleton.json", 0.0F);
        //初始化球位管理器
        if(sameTeam)
            this.orbManager = new OrbManagerInvert();
        else
            this.orbManager = new OrbManager();
        this.pauseFlag = pauseFlag;
        this.playerTag = playerTag;
        //最开始时初始化为无姿态
        this.stance = new NeutralStance();
    }

    //根据角色信息初始化形象
    public void initAnimation(CharacterInfo characterInfo)
    {
        if(characterInfo == null)
            return;
        characterInfo.resetAliveImg();
        this.renderPlayer = characterInfo.player;
        this.renderPlayer.drawX = this.drawX;
        this.renderPlayer.drawY = this.drawY + Settings.HEIGHT * 0.014f;
        //设置显示战斗信息，不然第一回合看不到意图
        Settings.hideCombatElements = false;
    }

    //初始化基本信息
    public void initHealth(int maxHealth,
       int currentHealth,
       CharacterInfo characterInfo,
       int tailNum, //尾巴的数量
       int maxOrbNum, //初始的球位数量
       boolean hasCaliper
    )
    {
        this.hasCaliper = hasCaliper;
        this.setHp(maxHealth);
        this.currentHealth = currentHealth;
        this.tailNum = tailNum;
        this.initAnimation(characterInfo);
        if(maxOrbNum > 0)
        {
            this.orbManager.increaseMaxOrbSlots(maxOrbNum,false,
                    drawX,drawY,hb_h,hb_x,hb_y);
            this.orbManager.renderFlag = true;
        }
        else{
            this.orbManager.increaseMaxOrbSlots(1,false,
                    drawX,drawY,hb_h,hb_x,hb_y);
        }
        this.showHealthBar();
        this.healthBarUpdatedEvent();
    }

    //生成球位
    public void channelOrb(MonsterOrb orb)
    {
        //设置orb的所属
        orb.setOwner(this);
        //先判断是否成功加入，加入成功的话再应用集中相关的buff
        if(orbManager.channelOrb(orb,drawX,drawY,hb_h))
        {
            //当有的buff需要和生成充能球的事件联动的时候，会在这里触发
            for (AbstractPower p : this.powers) {
                p.onChannel((AbstractOrb) orb);
            }
            AbstractPower power = this.getPower(FocusPower.POWER_ID);
            if(power != null)
            {
                //对当前充能球应用集中
                OrbExternalFunction.applyFocusAny(orb,power.amount);
            }
        }
    }

    //激发充能球的操作
    public void evokeOrb()
    {
        orbManager.evokeOrb(drawX,drawY,hb_h);
    }

    //增加球位
    public void increaseOrbSlot(int slotNum)
    {
        orbManager.increaseMaxOrbSlots(slotNum,true,drawX,drawY,hb_h,
                hb_x,hb_y);
    }

    //减少球位的操作
    //目前只支持把球位减1
    public void decreaseOrbSlot()
    {
        orbManager.decreaseMaxOrbSlots(1,drawX,drawY,hb_h);
    }


    @Override
    public void takeTurn() {
        //如果自己是需要负责阻塞的，那就让它阻塞
        if(this.pauseFlag)
        {
            MultiPauseAction.pauseStage = true;
            //准备开始阻塞
            AbstractDungeon.actionManager.addToBottom(
                new MultiPauseAction()
            );
        }
    }

    @Override
    protected void getMove(int i) {
        this.setMove((byte)1,Intent.MAGIC,-1);
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        //判断是否有人物需要渲染
        if(this.renderPlayer!=null)
        {
            if(this.isDead)
            {
                this.renderPlayer.render(sb);
            }
            else
                this.renderPlayer.renderPlayerImage(sb);
        }
        //渲染充能球
        this.orbManager.render(sb);
        this.stance.render(sb);
    }

    //这属于战斗结束时的操作了，最后再说
    public void makeItDie()
    {

    }

    @Override
    public void loseBlock() {
        //什么都不做
    }

    //强制失去block
    public void forceLoseBlock()
    {
        //判断是否有外卡钳
        if(this.hasCaliper)
            super.loseBlock(15);
        else
            super.loseBlock();
    }

    @Override
    public void damage(DamageInfo info) {
        //如果对面已经逃跑了，不再受到任何伤害
        if(ActionNetworkPatches.disableCombatTrigger)
        {
            return;
        }
        //处理当前状态下的愤怒姿态
        info.output = (int)this.stance.atDamageReceive(info.output,info.type);
        if (info.output > 0 &&
                (this.hasPower("Intangible") ||
                        this.hasPower("IntangiblePlayer"))
        ) {
            info.output = 1;
        }

        int damageAmount = info.output;
        if (!this.isDying && !this.isEscaping) {
            if (damageAmount < 0) {
                damageAmount = 0;
            }

            boolean hadBlock = true;
            if (this.currentBlock == 0) {
                hadBlock = false;
            }

            boolean weakenedToZero = damageAmount == 0;
            damageAmount = this.decrementBlock(info, damageAmount);
            Iterator var5;
            AbstractRelic r;
            if (info.owner == AbstractDungeon.player) {
                for(var5 = AbstractDungeon.player.relics.iterator(); var5.hasNext(); damageAmount = r.onAttackToChangeDamage(info, damageAmount)) {
                    r = (AbstractRelic)var5.next();
                }
            }


            AbstractPower p;
            if (info.owner != null) {
                for(var5 = info.owner.powers.iterator(); var5.hasNext(); damageAmount = p.onAttackToChangeDamage(info, damageAmount)) {
                    p = (AbstractPower)var5.next();
                }
            }

            for(var5 = this.powers.iterator(); var5.hasNext(); damageAmount = p.onAttackedToChangeDamage(info, damageAmount)) {
                p = (AbstractPower)var5.next();
            }

            if (info.owner == AbstractDungeon.player) {
                var5 = AbstractDungeon.player.relics.iterator();

                while(var5.hasNext()) {
                    r = (AbstractRelic)var5.next();
                    r.onAttack(info, damageAmount, this);
                }
            }

            var5 = this.powers.iterator();

            while(var5.hasNext()) {
                p = (AbstractPower)var5.next();
                p.wasHPLost(info, damageAmount);
            }

            if (info.owner != null) {
                var5 = info.owner.powers.iterator();

                while(var5.hasNext()) {
                    p = (AbstractPower)var5.next();
                    p.onAttack(info, damageAmount, this);
                }
            }

            for(var5 = this.powers.iterator(); var5.hasNext(); damageAmount = p.onAttacked(info, damageAmount)) {
                p = (AbstractPower)var5.next();
            }

            this.lastDamageTaken = Math.min(damageAmount, this.currentHealth);
            boolean probablyInstantKill = this.currentHealth == 0;

            if (damageAmount > 0) {
                if (info.owner != this) {
                    this.useStaggerAnimation();
                }

                if (damageAmount >= 99 && !CardCrawlGame.overkill) {
                    CardCrawlGame.overkill = true;
                }

                this.currentHealth -= damageAmount;
                if (!probablyInstantKill) {
                    AbstractDungeon.effectList.add(new StrikeEffect(this, this.hb.cX, this.hb.cY, damageAmount));
                }

                if (this.currentHealth < 0) {
                    this.currentHealth = 0;
                }

                this.healthBarUpdatedEvent();
            } else if (!probablyInstantKill) {
                if (weakenedToZero && this.currentBlock == 0) {
                    if (hadBlock) {
                        AbstractDungeon.effectList.add(new BlockedWordEffect(this, this.hb.cX, this.hb.cY, TEXT[30]));
                    } else {
                        AbstractDungeon.effectList.add(new StrikeEffect(this, this.hb.cX, this.hb.cY, 0));
                    }
                } else if (Settings.SHOW_DMG_BLOCK) {
                    AbstractDungeon.effectList.add(new BlockedWordEffect(this, this.hb.cX, this.hb.cY, TEXT[30]));
                }
            }

            if (this.currentHealth <= 0) {
                //临时把生命改成0
                this.currentHealth = 0;
                this.makeItDie();
            }

        }
        //伤害事件处理结束后，把info信息发送给对面
        ActionNetworkPatches.onAttackSend(info,this);
    }

    //修改姿态
    public void changeStance(AbstractStance stance)
    {
        this.stance = stance;
    }

    @Override
    public void update() {
        super.update();
        //对充能球位置的更新
        this.orbManager.update(animX,animY);
        this.stance.update();
    }
}
