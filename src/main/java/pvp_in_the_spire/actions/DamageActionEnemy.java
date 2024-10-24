package pvp_in_the_spire.actions;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.ThornsPower;
import com.megacrit.cardcrawl.vfx.GainPennyEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

//从那边发送过来的伤害事件，完全复制了原版的伤害事件
//重新构造一次只是为了不让它重新触发patch
public class DamageActionEnemy extends AbstractGameAction {

    private DamageInfo info;
    private int goldAmount;
    private static final float DURATION = 0.1F;
    private static final float POST_ATTACK_WAIT_DUR = 0.1F;
    private boolean skipWait;
    private boolean muteSfx;
    //是否为第2个处理周期，如果属于荆棘伤害的话，就延迟处理
    public boolean waitThorn = true;
    //是否已经发送过执行信息
    public boolean haveSendDischarge = false;

    public DamageActionEnemy(AbstractCreature target, DamageInfo info, AbstractGameAction.AttackEffect effect) {
        this.goldAmount = 0;
        this.skipWait = false;
        this.muteSfx = false;
        this.info = info;
        this.setValues(target, info);
        this.actionType = AbstractGameAction.ActionType.DAMAGE;
        this.attackEffect = effect;
        this.duration = 0.1F;
    }

    public DamageActionEnemy(AbstractCreature target, DamageInfo info, int stealGoldAmount) {
        this(target, info, AbstractGameAction.AttackEffect.SLASH_DIAGONAL);
        this.goldAmount = stealGoldAmount;
    }

    public DamageActionEnemy(AbstractCreature target, DamageInfo info) {
        this(target, info, AbstractGameAction.AttackEffect.NONE);
    }

    public DamageActionEnemy(AbstractCreature target, DamageInfo info, boolean superFast) {
        this(target, info, AbstractGameAction.AttackEffect.NONE);
        this.skipWait = superFast;
    }

    public DamageActionEnemy(AbstractCreature target, DamageInfo info, AbstractGameAction.AttackEffect effect, boolean superFast) {
        this(target, info, effect);
        this.skipWait = superFast;
    }

    public DamageActionEnemy(AbstractCreature target, DamageInfo info, AbstractGameAction.AttackEffect effect, boolean superFast, boolean muteSfx) {
        this(target, info, effect, superFast);
        this.muteSfx = muteSfx;
    }


    public void update() {
        //判断目标是不是有荆棘，如果有荆棘的话需要延迟处理
        if(this.target.hasPower(ThornsPower.POWER_ID) &&
            this.info.type == DamageType.NORMAL)
        {
            //判断是否需要等待伤害
            if(waitThorn)
            {
                //在尾部添加一个新的自己
                DamageActionEnemy nextAction = new DamageActionEnemy(target,info,attackEffect);
                //但下一次就不用等荆棘了
                nextAction.waitThorn = false;
                this.isDone = true;
                AbstractDungeon.actionManager.addToBottom(nextAction);
                return;
            }
        }
        System.out.println("damage action enemy run");
        if (this.shouldCancelAction() && this.info.type != DamageInfo.DamageType.THORNS) {
            this.isDone = true;
        } else {
            if (this.duration == 0.1F) {
                if (this.info.type != DamageInfo.DamageType.THORNS && (this.info.owner.isDying || this.info.owner.halfDead)) {
                    this.isDone = true;
                    return;
                }

                AbstractDungeon.effectList.add(new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY, this.attackEffect, this.muteSfx));
                if (this.goldAmount != 0) {
                    this.stealGold();
                }
            }

            this.tickDuration();
            if (this.isDone) {
                if (this.attackEffect == AbstractGameAction.AttackEffect.POISON) {
                    this.target.tint.color.set(Color.CHARTREUSE.cpy());
                    this.target.tint.changeColor(Color.WHITE.cpy());
                } else if (this.attackEffect == AbstractGameAction.AttackEffect.FIRE) {
                    this.target.tint.color.set(Color.RED);
                    this.target.tint.changeColor(Color.WHITE.cpy());
                }

                this.target.damage(this.info);
                if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                    AbstractDungeon.actionManager.clearPostCombatActions();
                }

                if (!this.skipWait && !Settings.FAST_MODE) {
                    this.addToTop(new WaitAction(0.1F));
                }
            }

        }
    }

    private void stealGold() {
        if (this.target.gold != 0) {
            CardCrawlGame.sound.play("GOLD_JINGLE");
            if (this.target.gold < this.goldAmount) {
                this.goldAmount = this.target.gold;
            }

            AbstractCreature var10000 = this.target;
            var10000.gold -= this.goldAmount;

            for(int i = 0; i < this.goldAmount; ++i) {
                if (this.source.isPlayer) {
                    AbstractDungeon.effectList.add(new GainPennyEffect(this.target.hb.cX, this.target.hb.cY));
                } else {
                    AbstractDungeon.effectList.add(new GainPennyEffect(this.source, this.target.hb.cX, this.target.hb.cY, this.source.hb.cX, this.source.hb.cY, false));
                }
            }

        }
    }

}
