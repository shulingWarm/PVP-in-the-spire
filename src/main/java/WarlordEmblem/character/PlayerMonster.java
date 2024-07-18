package WarlordEmblem.character;

import UI.BattleUI.OrbManager;
import WarlordEmblem.actions.MultiPauseAction;
import WarlordEmblem.orbs.OrbExternalFunction;
import basemod.abstracts.CustomMonster;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FocusPower;

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

    //玩家的tag
    public int playerTag;

    public PlayerMonster(boolean pauseFlag,float x,float y,int playerTag)
    {
        super("test","PlayerMonster",10,0, 0, 180.0F, 240.0F, (String)null,x,y);
        //随便载入一个贴图，用于演示基本的人物效果
        //这是为了确保渲染父类的时候不报错
        this.loadAnimation("images/characters/watcher/idle/skeleton.atlas", "images/characters/watcher/idle/skeleton.json", 0.0F);
        //初始化球位管理器
        this.orbManager = new OrbManager();
        this.pauseFlag = pauseFlag;
        this.playerTag = playerTag;
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
       int maxOrbNum //初始的球位数量
    )
    {
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
    public void channelOrb(AbstractOrb orb)
    {
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
    }

    @Override
    public void update() {
        super.update();
        //对充能球位置的更新
        this.orbManager.update(animX,animY);
    }
}
