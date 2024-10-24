package pvp_in_the_spire.character;

//导入kaka
import pvp_in_the_spire.ui.PotionPanel;
import pvp_in_the_spire.ui.RelicPanel;
import pvp_in_the_spire.AutomaticSocketServer;
import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.room.FriendManager;
import pvp_in_the_spire.actions.*;
import pvp_in_the_spire.helpers.ClassNameHelper;
import pvp_in_the_spire.helpers.LocationHelper;
import pvp_in_the_spire.helpers.RandMonsterHelper;
import pvp_in_the_spire.orbs.OrbExternalFunction;
import pvp_in_the_spire.patches.ActionNetworkPatches;
import pvp_in_the_spire.patches.CardShowPatch.CardBox;
import pvp_in_the_spire.patches.CardShowPatch.HandCardSend;
import pvp_in_the_spire.reward.EnemyGold;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.*;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.*;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FocusPower;
import com.megacrit.cardcrawl.powers.InvinciblePower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.stances.AbstractStance;
import com.megacrit.cardcrawl.stances.NeutralStance;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.vfx.SpeechBubble;
import pvp_in_the_spire.SocketServer;
import com.megacrit.cardcrawl.vfx.combat.BlockedWordEffect;
import com.megacrit.cardcrawl.vfx.combat.DeckPoofEffect;
import com.megacrit.cardcrawl.vfx.combat.HbBlockBrokenEffect;
import com.megacrit.cardcrawl.vfx.combat.StrikeEffect;

//死循环的action
//用来等待的action

//手动获得尼利的宝典

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

//这是一个可以用来手动控制的怪物
public class ControlMoster extends AbstractMonster {
    public static final String ID = "ControlMoster";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;

    public static final String[] MOVES;
    public static final String[] DIALOG;
    private boolean firstMove;
    private boolean saidPower;
    private boolean talky;

    //这个敌人的实例，它目前最多会有一个实例，所以这样做也可以
    //等后面如果需要处理多个人的情况，这里还要再修改
    //它始终等于最近刚刚初始化过的那个实例
    public static ControlMoster instance;

    //初始允许的最大充能球栏位
    public int masterMaxOrbs = 3;
    //目前的充能球总个数
    public int maxOrbs = 0;
    //全部的充能球的具体情况
    public ArrayList<AbstractOrb> orbs;

    //和服务器的通信进程
    public SocketServer server;
    //玩家的初始生成
    public int playerHealth = 150;
    //实际的目前的姿态
    public AbstractStance stance;
    //剩余的瓶中精灵的数量
    public int fairyPotionNum = 0;
    //剩余的尾巴的数量
    public int tailNum = 0;
    //这个东西表示的似乎是姿态对应的特效
    public TextureAtlas eyeAtlas = null;
    public AnimationState eyeState;
    public AnimationStateData eyeStateData;
    //据说这是一个姿态的骨架
    public Skeleton eyeSkeleton;
    //另一种姿态的骨架
    public Bone eyeBone;
    //是否为观者
    boolean watcherFlag = false;

    //用于显示卡牌的UI,把敌人目前的手牌显示在敌人的头上
    CardBox cardBox=null;

    //敌人的遗物列表
    public RelicPanel relicPanel = new RelicPanel();
    //显示药水的栏位
    public PotionPanel potionPanel = new PotionPanel();
    //用于被渲染的人物角色
    public AbstractPlayer renderPlayer=null;

    //敌人能量框
    public static final float ENERGY_PANEL_X = Settings.WIDTH*0.8f;
    public static final float ENERGY_PANEL_Y = Settings.HEIGHT*0.85f;
    //我方上一次的能量，这个是为了同步双方的能量显示
    public int lastSendEnergy = 0;
    //能量框的字体大小
    public static float energyFontScale = 1.f;
    //是否渲染球位
    public boolean renderOrb = false;
    //判断是否已经添加过友军了
    public boolean addedFriendFlag = false;

    //初始化姿态特效
    //这都是从watcher的初始化里面学的
    public void initEyeState()
    {
        //初始化stance 弄成calm只是为了测试效果
        this.eyeAtlas = new TextureAtlas(Gdx.files.internal("images/characters/watcher/eye_anim/skeleton.atlas"));
        SkeletonJson json = new SkeletonJson(this.eyeAtlas);
        json.setScale(Settings.scale);
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("images/characters/watcher/eye_anim/skeleton.json"));
        this.eyeSkeleton = new Skeleton(skeletonData);
        this.eyeSkeleton.setColor(Color.WHITE);
        this.eyeStateData = new AnimationStateData(skeletonData);
        this.eyeState = new AnimationState(this.eyeStateData);
        this.eyeStateData.setDefaultMix(0.2F);
        //临时设置成愤怒姿态看看是什么效果
        this.eyeState.setAnimation(0, "None", true);
        //另一种初始化姿态骨架
        this.eyeBone = this.skeleton.findBone("eye_anchor");
    }

    //更改敌人的stance
    public void changeStance(AbstractStance stance)
    {
        //修改stance对象
        this.stance = stance;
        //把动画设置成对应的key,虽然不知道这有什么用
        //只有观者才做这方面的渲染
        if(watcherFlag)
        {
            if(stance.ID.equals("Neutral"))
                this.eyeState.setAnimation(0, "None", true);
            else
                this.eyeState.setAnimation(0, stance.ID, true);
        }
    }

    //把敌人初始化成观者
    void initAsWatcher()
    {
        this.loadAnimation("images/characters/watcher/idle/skeleton.atlas", "images/characters/watcher/idle/skeleton.json", 0.0F);
        this.setHp(144);
        masterMaxOrbs = 0;
        //我方猎人140
        playerHealth = 144;
    }

    //根据角色信息初始化形象
    public void initAnimation(CharacterInfo characterInfo)
    {
        if(characterInfo == null)
            return;
        characterInfo.resetAliveImg();
        this.renderPlayer = characterInfo.player;
        this.renderPlayer.drawX = this.drawX;
        this.renderPlayer.drawY = this.drawY;
        //设置显示战斗信息，不然第一回合看不到意图
        Settings.hideCombatElements = false;
//        this.atlas = characterInfo.getAtlas();
//        //初始化骨架
//        this.skeleton = characterInfo.getSkeleton();
//        this.stateData = characterInfo.getStateData();
//        //初始化动画状态
//        this.state = characterInfo.getState();
//        //如果是观者的话初始化眼睛
//        if(SocketServer.monsterChar == AbstractPlayer.PlayerClass.WATCHER)
//        {
//            this.initEyeState();
//        }
    }

    //初始化角色的生命上限和形象之类的
    public void initHealthAndTexture()
    {
        this.setHp(SocketServer.monsterMaxHealth);
        this.currentHealth = SocketServer.monsterCurrentHealth;
        //记录尾巴和瓶中精灵的数量
        this.tailNum = SocketServer.tailNum;
        this.fairyPotionNum = SocketServer.fairyPotionNum;
        //设置形象
        initAnimation(SocketServer.oppositeCharacter);
        //添加球位数量
        if(SocketServer.beginOrbNum>0)
        {
            increaseMaxOrbSlots(SocketServer.beginOrbNum,false);
            //记录渲染球位
            this.renderOrb = true;
        }
        else
        {
            //也给它增加一个球位，但不渲染
            //等有球了再渲染
            increaseMaxOrbSlots(1,false);
        }
        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        //初始化敌人的手牌显示框
        cardBox = new CardBox(this.drawX,this.drawY+this.hb_h*1.5f, HandCardSend.monsterCardList,this);
    }

    public ControlMoster(float x, float y, boolean talk) {
        super(SocketServer.oppositeName, "Cultist", 150, -4.0F, -16.0F, 220.0F, 290.0F, (String)null, x, y);
        //这一句是故意写上去让它报错的，看下到底有没有使用过ControlMonster.
        System.out.println(orbs.size());
        //强制让敌人与玩家保持对称
        AbstractPlayer player = AbstractDungeon.player;
        this.drawX = LocationHelper.xInvert(player.drawX);
        this.drawY = LocationHelper.yInvert(player.drawY);
        this.firstMove = true;
        this.saidPower = false;
        //初始化充能球的列表
        orbs = new ArrayList<>();

        this.dialogX = -50.0F * Settings.scale;
        this.dialogY = 50.0F * Settings.scale;

        this.damage.add(new DamageInfo(this, 40));
        this.talky = talk;
        if (Settings.FAST_MODE) {
            this.talky = false;
        }
        //初始化姿态变量，但刚开始并没有什么意义 只是为了防止空指针报错
        stance = new NeutralStance();
        initAsWatcher();
        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        this.stateData.setMix("Hit", "Idle", 0.1F);
        this.flipHorizontal = true;
        e.setTimeScale(0.6F);
        this.type = AbstractMonster.EnemyType.NORMAL;

        //在构造的时候初始化那个静态变量，让它等于自己
        instance = this;
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

    //对充能球应用集中
    public void updateOrbDescription()
    {
        AbstractPower power = this.getPower("Focus");
        int powerAmount = 0;
        if(power != null)
            powerAmount = power.amount;
        //遍历每个球，对不同的球做不同的处理
        for(AbstractOrb eachOrb : this.orbs)
        {
            if(eachOrb instanceof Lightning)
            {
                OrbExternalFunction.applyFocusLighting((Lightning)eachOrb,powerAmount);
                OrbExternalFunction.updateDescriptionLighting((Lightning)eachOrb);
            }
            else if(eachOrb instanceof Frost)
            {
                OrbExternalFunction.applyFocusBlock((Frost) eachOrb,powerAmount);
                OrbExternalFunction.updateDescriptionBlock((Frost) eachOrb);
            }
            //黑球的更新目前完全由另一个函数接管了
//            else if(eachOrb instanceof Dark)
//            {
//                OrbExternalFunction.applyFocusDark((Dark)eachOrb,power.amount);
//                OrbExternalFunction.updateDescriptionDark((Dark)eachOrb);
//            }
        }
    }

    //在列表里面放置充能球的操作
    public void channelOrb(AbstractOrb orbToSet) {

        //渲染球位
        this.renderOrb = true;

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
                ((AbstractOrb)orbToSet).cX = ((AbstractOrb)this.orbs.get(index)).cX;
                ((AbstractOrb)orbToSet).cY = ((AbstractOrb)this.orbs.get(index)).cY;
                this.orbs.set(index, orbToSet);
                //setSlot需要使用针对敌人单位的setSlot
                setSlot(this.orbs.get(index),index,this.maxOrbs);
                ((AbstractOrb)orbToSet).playChannelSFX();
                Iterator var6 = this.powers.iterator();

                //当有的buff需要和生成充能球的事件联动的时候，会在这里触发
                while(var6.hasNext()) {
                    AbstractPower p = (AbstractPower)var6.next();
                    p.onChannel((AbstractOrb)orbToSet);
                }
                //判断是否有集中
                AbstractPower power = this.getPower(FocusPower.POWER_ID);
                if(power != null)
                {
                    //对当前充能球应用集中
                    OrbExternalFunction.applyFocusAny(orbToSet,power.amount);
                }
            }

        }
    }

    //触发充能球的激发动画
    public void triggerEvokeAnimation(int slot) {
        if (this.maxOrbs > 0) {
            ((AbstractOrb)this.orbs.get(slot)).triggerEvokeAnimation();
        }
    }

    //对球的激发操作
    public void evokeOrb() {
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
                setSlot(orbs.get(i),i,this.maxOrbs);
            }
        }

    }

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

    //减少球位的操作
    //其实这个函数只能用来把球数减1
    public void decreaseMaxOrbSlots(int amount) {
        if (this.maxOrbs > 0) {
            this.maxOrbs -= amount;
            if (this.maxOrbs < 0) {
                this.maxOrbs = 0;
            }

            if (!this.orbs.isEmpty()) {
                this.orbs.remove(this.orbs.size() - 1);
            }

            for(int i = 0; i < this.orbs.size(); ++i) {
                setSlot(this.orbs.get(i),i,this.maxOrbs);
            }
        }
    }

    //触发循环操作
    //主要是为了处理循环的buff
    public void loopOrbStartTurn(int amount)
    {
        //如果没球就更不用处理了
        if(this.orbs.isEmpty())
            return;
        //第一个球
        AbstractOrb firstOrb = orbs.get(0);
        //判断第一个球是不是黑球，不是黑球就不用处理了
        if(firstOrb instanceof Dark)
        {
            //触发若干次
            for(int i=0;i<amount;++i)
            {
                OrbExternalFunction.darkOnEndOfTurn((Dark) firstOrb);
            }
        }
    }


    //怪物的初始化操作，其它函数会调用这个东西
    @Override
    public void init()
    {
        //调用父类的操作
        super.init();
        //调用提升充能球栏位的操作
        increaseMaxOrbSlots(masterMaxOrbs,false);
    }

    //渲染敌人的能量框
    public void renderEnergyPanel(SpriteBatch sb)
    {
        //如果没有player就直接退出
        if(this.renderPlayer==null)
            return;
        //渲染敌人的能量框
        this.renderPlayer.renderOrb(sb,SocketServer.currentEnergy>0,ENERGY_PANEL_X,ENERGY_PANEL_Y);
        //能量框中的数字
        String energyMsg = SocketServer.currentEnergy + "/" + SocketServer.masterEnergy;
        BitmapFont font = this.renderPlayer.getEnergyNumFont();
        font.getData().setScale(energyFontScale);
        FontHelper.renderFontCentered(sb, font, energyMsg, ENERGY_PANEL_X, ENERGY_PANEL_Y, Color.WHITE);
    }

    //渲染画面的操作，可能需要这个操作来让充能球显示出来
    @Override
    public void render(SpriteBatch sb)
    {
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
        //渲染stance
        stance.render(sb);
        //画出充能球的效果
        if (this.renderOrb) {
            //渲染每个球
            for(AbstractOrb eachOrb : this.orbs)
            {
                eachOrb.render(sb);
            }
        }
        //设置和姿态相关的效果
        if(this.watcherFlag)
        {
            this.eyeState.update(Gdx.graphics.getDeltaTime());
            this.eyeState.apply(this.eyeSkeleton);
            this.eyeSkeleton.updateWorldTransform();
            this.eyeSkeleton.setPosition(this.skeleton.getX() + this.eyeBone.getWorldX(), this.skeleton.getY() + this.eyeBone.getWorldY());
            this.eyeSkeleton.setColor(this.tint.color);
            this.eyeSkeleton.setFlip(this.flipHorizontal, this.flipVertical);
            //对姿态渲染时的似乎的一种专属操作
            sb.end();
            CardCrawlGame.psb.begin();
            sr.draw(CardCrawlGame.psb, this.eyeSkeleton);
            CardCrawlGame.psb.end();
            sb.begin();
        }
        //判断是否需要渲染敌人的手牌
        //如果自己拿了圆顶就不再显示了
        if(cardBox!=null)
        {
            //渲染敌人的牌
            cardBox.render(sb);
        }
        //渲染遗物列表
        relicPanel.render(sb);
        //渲染药水列表
        potionPanel.render(sb);
        //渲染敌人的能量框
        this.renderEnergyPanel(sb);
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

    //能量信息的编码
    public static void energyChangeEncode(DataOutputStream streamHandle,int newEnergy)
    {
        //发送数据头
        try
        {
            streamHandle.writeInt(FightProtocol.ENERGY_UPDATE);
            streamHandle.writeInt(newEnergy);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //对能量信息的解码
    public static void energyChangeDecode(DataInputStream streamHandle)
    {
        try
        {
            SocketServer.currentEnergy = streamHandle.readInt();
            //更改字体大小
            energyFontScale = 2.f;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //更新敌人的能量框
    public void updatePlayerEnergyPanel()
    {
        //如果字体处于放大状态，对字体大小做一下更新
        if(energyFontScale!=1.f)
        {
            energyFontScale = MathHelper.scaleLerpSnap(energyFontScale,1.f);
        }
        //更新能量条的显示
        if(this.renderPlayer!=null)
            this.renderPlayer.updateOrb(SocketServer.currentEnergy);
        //获取当前的能量
        int currEnergy = EnergyPanel.getCurrentEnergy();
        //判断能量是否有更新
        if(currEnergy==lastSendEnergy)
            return;
        lastSendEnergy = currEnergy;
        SocketServer server = AutomaticSocketServer.getServer();
        energyChangeEncode(server.streamHandle,lastSendEnergy);
        server.send();
    }

    //用于更新动画的操作
    @Override
    public void update()
    {
        //调用父类的update代码，这是更新效果的关键
        super.update();
        //调用每个充能球执行动画更新的效果
        for(AbstractOrb eachOrb : orbs)
        {
            eachOrb.update();
            //关于充能球的动画更新需要换了，不能使用这个
            //但也不想做特殊的调用，不如调用一个静态函数
            updateOrbAnimation(eachOrb);
        }
        //对姿态做对应的更新 其实里面就是更新动画
        stance.update();
        //更新显示遗物列表
        relicPanel.update();
        //更新药水
        potionPanel.update();
        this.updatePlayerEnergyPanel();
        //玩家的血条更新，如果玩家明明已经0血了，就触发一下它的伤害 但不需要传播
        if(AbstractDungeon.player.currentHealth <= 0 &&
            !(AbstractDungeon.player.isDying ||
                AbstractDungeon.player.isDead))
        {
            //不必发送伤害信息
            ActionNetworkPatches.stopSendAttack = true;
            InvinciblePower invinciblePower = (InvinciblePower) AbstractDungeon.player.getPower(InvinciblePower.POWER_ID);
            if(invinciblePower!=null)
            {
                invinciblePower.amount = 999;
            }
            AbstractDungeon.player.damage(new DamageInfo(AbstractDungeon.player,1));
            ActionNetworkPatches.stopSendAttack = false;
        }
    }

    //怪物受到伤害时的事件 这里对死亡事件做了特殊的处理
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
                //判断是否还有瓶中精灵的数量
                if(fairyPotionNum>0)
                {
                    --fairyPotionNum;
                    //回复的数量
                    int healAmount = (int)(0.3F * (float)maxHealth);
                    this.heal(healAmount);
                }
                //判断是否有尾巴
//                else if(tailNum>0)
//                {
//                    //处理一个尾巴
//                    --tailNum;
//                    int healAmount = (int)(0.5F * (float)maxHealth);
//                    this.heal(healAmount);
//                    //移除所有的debuff
//                    AbstractDungeon.actionManager.addToBottom(
//                            new RemoveDebuffsAction(this)
//                    );
//                }
                else {
                    //什么都没有的情况下执行死亡
                    this.makeItDie();
                }
            }

        }
        //伤害事件处理结束后，把info信息发送给对面
        ActionNetworkPatches.onAttackSend(info,this);
    }

    //移除所有的debuff
    public void removeAllDebuff()
    {
        Iterator var1 = this.powers.iterator();

        while(var1.hasNext()) {
            AbstractPower p = (AbstractPower)var1.next();
            if (p.type == AbstractPower.PowerType.DEBUFF) {
                this.addToTop(new RemoveSpecificPowerAction(this, this, p));
            }
        }
    }

    //令敌人死亡，执行对应的死亡操作
    public void makeItDie()
    {
        //发送审判信息，告诉强制斩杀
        SocketServer server = AutomaticSocketServer.getServer();
        ActionNetworkPatches.judgementEncode(server.streamHandle,this,10000);
        server.send();
        //在奖励里面强行添加一个敌人掉落的金钱
        int goldNum = (int)(SocketServer.oppositeGold * SocketServer.loseGoldRate);
        if(goldNum>0)
        {
            AbstractDungeon.getCurrRoom().rewards.add(new EnemyGold(
                    goldNum
            ));
        }
        //清理爪牙
        FriendManager.instance.makeMinionSuicide();
        //如果已经没有尾巴了，说明它就是已经死了，那么进入胜利画面
        if(SocketServer.tailNum==0)
        {
            //淡出音乐
            CardCrawlGame.music.fadeAll();
            //准备执行胜利画面
            GlobalManager.prepareWin = true;
            this.onBossVictoryLogic();
            //结束延时显示
            //RenderPatch.delayBox = null;
        }
        this.die();
        if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            AbstractDungeon.actionManager.cleanCardQueue();
            AbstractDungeon.effectList.add(new DeckPoofEffect(64.0F * Settings.scale, 64.0F * Settings.scale, true));
            AbstractDungeon.effectList.add(new DeckPoofEffect((float)Settings.WIDTH - 64.0F * Settings.scale, 64.0F * Settings.scale, false));
            AbstractDungeon.overlayMenu.hideCombatPanels();
        }

        if (this.currentBlock > 0) {
            this.loseBlock();
            AbstractDungeon.effectList.add(new HbBlockBrokenEffect(this.hb.cX - this.hb.width / 2.0F + BLOCK_ICON_X, this.hb.cY - this.hb.height / 2.0F + BLOCK_ICON_Y));
        }
        //准备下一回合为后手
        SocketServer.oppositePlayerReady = false;
        SocketServer.firstHandFlag = false;
        //打开禁用某些战斗的操作
        ActionNetworkPatches.disableCombatTrigger = true;
        ActionNetworkPatches.HealEventSend.disableSend = true;
        //如果要继续渲染的话，显示对方死亡的图片
        if(this.renderPlayer!=null)
        {
            this.renderPlayer.playDeathAnimation();
            this.renderPlayer.isDead = true;
        }
    }


    public ControlMoster(float x, float y) {
        this(x, y, true);
    }

    public void takeTurn() {

        //新建网络数据的读取协议，用于判断等待读取的结束条件
//        KaKaProtocol protocol = new KaKaProtocol(this);
//
//        AbstractDungeon.actionManager.addToBottom(new PauseAction(AbstractDungeon.actionManager,server,protocol));
//

        if(SocketServer.USE_NETWORK && this.intent!=Intent.STUN)
        {
            //新建双端对战的网络读取协议
            FightProtocol protocol = new FightProtocol();
            //把里面的结束控制的标志符改成false,说明该敌人的回合了
            FightProtocol.endReadFlag = false;
            //添加循环等待对方信号的操作
            AbstractDungeon.actionManager.addToBottom(new PauseAction(AbstractDungeon.actionManager,
                    server,protocol));
            //添加一个敌人，并且只添加一次
            if(GlobalManager.friendMonsterFlag && !addedFriendFlag)
            {
                //判断有没有可添加的monster
                String refMonsterName = FriendManager.instance.oppositeFriendName;
                AbstractMonster tempMonster = null;
                if(!refMonsterName.isEmpty())
                {
                    tempMonster = ClassNameHelper.createMonster(refMonsterName);
                }
                //判断是否已经有有效的monster了
                if(tempMonster == null)
                {
                    //取出一个随机的monster
                    tempMonster = RandMonsterHelper.getRandMonster();
                }
                AbstractDungeon.actionManager.addToBottom(
                    new AddFriendMonsterAction(tempMonster)
                );
                //记录已经添加过友军了
                addedFriendFlag = true;
            }
        }
    }

    private void playSfx() {
        int roll = MathUtils.random(2);
        if (roll == 0) {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_CULTIST_1A"));
        } else if (roll == 1) {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_CULTIST_1B"));
        } else {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_CULTIST_1C"));
        }

    }

    private void playDeathSfx() {
        int roll = MathUtils.random(2);
        if (roll == 0) {
            CardCrawlGame.sound.play("VO_CULTIST_2A");
        } else if (roll == 1) {
            CardCrawlGame.sound.play("VO_CULTIST_2B");
        } else {
            CardCrawlGame.sound.play("VO_CULTIST_2C");
        }

    }

    public void die() {
        this.playDeathSfx();
        this.state.setTimeScale(0.1F);
        this.useShakeAnimation(5.0F);
        if (this.talky && this.saidPower) {
            AbstractDungeon.effectList.add(new SpeechBubble(this.hb.cX + this.dialogX, this.hb.cY + this.dialogY, 2.5F, DIALOG[2], false));
            ++this.deathTimer;
        }

        super.die();
    }

    protected void getMove(int num) {
        this.setMove((byte)1,Intent.MAGIC,-1);
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Cultist");
        NAME = monsterStrings.NAME;
        MOVES = monsterStrings.MOVES;
        DIALOG = monsterStrings.DIALOG;
    }
}
