package pvp_in_the_spire.character;

import pvp_in_the_spire.ui.BattleUI.BattleCardPanel;
import pvp_in_the_spire.ui.BattleUI.OrbManager;
import pvp_in_the_spire.ui.BattleUI.OrbManagerInvert;
import pvp_in_the_spire.events.KillEvent;
import pvp_in_the_spire.pvp_api.Communication;
import pvp_in_the_spire.player_management.PlayerCardManager;
import pvp_in_the_spire.actions.MultiPauseAction;
import pvp_in_the_spire.orbs.MonsterOrb;
import pvp_in_the_spire.patches.ActionNetworkPatches;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.red.Barricade;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.stances.AbstractStance;
import com.megacrit.cardcrawl.stances.NeutralStance;
import com.megacrit.cardcrawl.vfx.combat.BlockedWordEffect;
import com.megacrit.cardcrawl.vfx.combat.StrikeEffect;

import java.util.ArrayList;
import java.util.Iterator;

//新时代的control monster
//这是为了适应多人联机对战的情况下实现的monster
//之前的ControlMonster里面的屎山太多了
public class PlayerMonster extends AbstractMonster {

    //球位管理器
    public OrbManager orbManager;
    //玩家的卡牌管理器
    public PlayerCardManager playerCardManager;
    //尾巴的数量
    public int tailNum;
    //用于实际被渲染的角色
    public AbstractPlayer renderPlayer;
    //用于渲染角色信息的卡片
    public BattleCardPanel battleCardPanel;
    //是否为友军
    public boolean friendFlag;
    //判断是否需要渲染手牌
    public boolean renderCardFlag = false;

    //用于判断当前的monster是否负责做pause操作
    //它是在敌方回合阻塞玩家的出牌的
    public boolean pauseFlag;
    //是否有外卡钳
    public boolean hasCaliper = false;
    //是否允许破甲
    public boolean allowLoseBlock = false;

    //姿态
    public AbstractStance stance;

    //玩家的tag
    public int playerTag;
    //标记为回合结束时的状态
    public boolean endTurnFlag = false;
    //玩家的轮次 这表示这个玩家已经完成了几个轮次
    public int idTurn = 0;

    public PlayerMonster(String name,boolean pauseFlag,float x,float y,int playerTag,boolean sameTeam,
         PlayerCardManager cardManager
    )
    {
        super(name,"PlayerMonster",10,0, 0, 180.0F, 240.0F, (String)null,x,y);
        //随便载入一个贴图，用于演示基本的人物效果
        //这是为了确保渲染父类的时候不报错
        this.loadAnimation("images/characters/watcher/idle/skeleton.atlas", "images/characters/watcher/idle/skeleton.json", 0.0F);
        //初始化球位管理器
        if(sameTeam)
            this.orbManager = new OrbManagerInvert();
        else
            this.orbManager = new OrbManager();
        //初始化卡牌管理器
        this.playerCardManager = cardManager;
        this.pauseFlag = pauseFlag;
        this.playerTag = playerTag;
        this.friendFlag = sameTeam;
        //最开始时初始化为无姿态
        this.stance = new NeutralStance();
        //初始化手牌信息
        this.battleCardPanel = new BattleCardPanel(this.drawX,this.drawY+this.hb_h*1.5f,
                this.playerCardManager.cardRecorder,this);
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
       int maxEnergy, //最大能量
       ArrayList<AbstractRelic> relicList, //玩家的遗物列表
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
        //初始化角色的能量信息
        this.battleCardPanel.energyPanel.init(this.renderPlayer,maxEnergy);
        //初始化玩家的遗物列表
        this.battleCardPanel.initRelicList(relicList);
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
            orb.applyFocus();
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
            System.out.printf("%d take turn\n",playerTag);
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
        //判断是否需要渲染卡牌信息
        if(this.renderCardFlag)
        {
            this.battleCardPanel.render(sb);
        }
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
        //发送命令死亡的事件
        Communication.advanceSendEvent(new KillEvent(this.playerTag),
                this.playerTag);
        //设置玩家死亡
        this.isDead = true;
        this.renderPlayer.playDeathAnimation();
        this.die();
    }

    @Override
    public void loseBlock() {
        if(allowLoseBlock)
            super.loseBlock();
    }

    //强制失去block
    public void forceLoseBlock(boolean checkCaliper)
    {
        //先看是否需要判断壁垒
        if(checkCaliper && this.hasPower(Barricade.ID))
        {
            return;
        }
        //判断是否有外卡钳
        if(this.hasCaliper && checkCaliper)
        {
            super.loseBlock(15);
        }
        else
            super.loseBlock();
    }

    //判断是否需要根据buff改变伤害值
    public static boolean isReceiveDamage()
    {
        return ActionNetworkPatches.stopSendAttack;
    }

    //根据各种信息改变damage
    public int changeDamageInfo(DamageInfo info,int damageAmount)
    {
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
        return damageAmount;
    }

    @Override
    public void damage(DamageInfo info) {
        //允许打破护甲
        this.allowLoseBlock = true;
        //如果对面已经逃跑了，不再受到任何伤害
        if(ActionNetworkPatches.disableCombatTrigger)
        {
            return;
        }
        //处理当前状态下的愤怒姿态
        //如果这是接收到的就没必要判断了
        if(!isReceiveDamage())
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

            damageAmount = changeDamageInfo(info,damageAmount);

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
                makeItDie();
            }

        }
        this.allowLoseBlock = false;
        //伤害事件处理结束后，把info信息发送给对面
        ActionNetworkPatches.onAttackSend(info,this);
    }

    //修改姿态
    public void changeStance(AbstractStance stance)
    {
        this.stance = stance;
    }

    //处理回合结束时的buff更新
    public void endOfTurnTrigger()
    {
        System.out.printf("%d end turn\n",this.playerTag);
        //增加已经完成的回合计数
        ++idTurn;
        //标记为回合结束时的状态
        this.endTurnFlag = true;
        for(AbstractPower eachPower : powers)
        {
            eachPower.atEndOfTurnPreEndTurnCards(false);
        }
        for(AbstractPower eachPower : powers)
        {
            eachPower.atEndOfTurn(false);
        }
    }

    //大回合结束时的触发
    public void endOfRoundTrigger()
    {
        for(AbstractPower eachPower : powers)
        {
            eachPower.atEndOfRound();
        }
    }

    //设置当前的能量
    public void setCurrEnergy(int currEnergy)
    {
        this.battleCardPanel.setEnergy(currEnergy);
    }

    //回合开始时，标记为已经开始回合
    @Override
    public void applyStartOfTurnPowers() {
        super.applyStartOfTurnPowers();
        this.endTurnFlag = false;
    }

    public void updatePotionList(ArrayList<AbstractPotion> potionList)
    {
        this.battleCardPanel.potionPanel.updatePotion(potionList);
    }

    //获取即将抽到的牌的列表
    public ArrayList<AbstractCard> getDrawingCards()
    {
        return this.battleCardPanel.cardBox.shownCards.drawingCards;
    }

    //标记为显示手牌
    public void setRenderCard(boolean renderFlag)
    {
        this.renderCardFlag = renderFlag;
    }

    //判断是否为回合结束时的状态
    public boolean isEndTurn()
    {
        return this.isDead || this.endTurnFlag;
    }

    //获取当前玩家的 id turn
    public int getIdTurn() {
        //如果玩家已经死了，那就返回-1,这种情况下是不用判断的
        if(this.isDead)
            return -1;
        return idTurn;
    }

    @Override
    public void update() {
        super.update();
        //对充能球位置的更新
        this.orbManager.update(animX,animY);
        this.stance.update();
        //对能量框相关的更新
        if(renderCardFlag)
            this.battleCardPanel.update();
    }
}
