package pvp_in_the_spire.orbs;

import pvp_in_the_spire.AutomaticSocketServer;
import pvp_in_the_spire.actions.FightProtocol;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DarkOrbEvokeAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.OrbStrings;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.Dark;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.DarkOrbActivateEffect;
import com.megacrit.cardcrawl.vfx.combat.DarkOrbPassiveEffect;
import com.megacrit.cardcrawl.vfx.combat.OrbFlareEffect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MonsterDark extends MonsterOrb{

    public static final String ORB_ID = "Dark";
    private static final OrbStrings orbString;
    public static final String[] DESC;
    private static final float ORB_BORDER_SCALE = 1.2F;
    private float vfxTimer = 0.5F;
    private static final float VFX_INTERVAL_TIME = 0.25F;

    //发送黑球的结束回合的操作
    public static void sendDarkEndTurn(AbstractOrb orb, DataOutputStream streamHandle)
    {
        //判断有没有这个球
        int idOrb = OrbMapping.getPlayerOrbNum(orb);
        if(idOrb<0)
            return;
        try
        {
            //发送数据头
            streamHandle.writeInt(FightProtocol.DARK_END_TURN);
            //发送球的编号
            streamHandle.writeInt(idOrb);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //解码黑球回合结束的操作
    public static void darkEndTurnDecode(DataInputStream streamHandle)
    {
        //读取球的标号
        try
        {
            int idOrb = streamHandle.readInt();
            //判断有没有这个球
            AbstractOrb orb = OrbMapping.getMonsterOrb(idOrb);
            if(orb!=null)
            {
                orb.onEndOfTurn();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //对球信息的解码
    public static void orbDescriptionDecode(DataInputStream streamHandle)
    {
        try
        {
            //获取球的标号
            int idOrb = streamHandle.readInt();
            //获取球的回合结束数据
            int passiveNum = streamHandle.readInt();
            //获取球的激发数值
            int evokeNum = streamHandle.readInt();
            //如果球的标号异常就不处理
            if(idOrb<0)
                return;
            //获得对应的球
            AbstractOrb orb = OrbMapping.getMonsterOrb(idOrb);
            //如果没有得到球就不处理了
            if(orb==null)
            {
                return;
            }
            //修改对应的球的数值
            orb.passiveAmount = passiveNum;
            orb.evokeAmount = evokeNum;
            //通知修改这个球的数值
            orb.updateDescription();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //发送新的关于球的描述
    public static void orbDescriptionEncode(AbstractOrb orb,DataOutputStream streamHandle)
    {
        //获取玩家里面的关于这个球的标号
        int idOrb = OrbMapping.getPlayerOrbNum(orb);
        //如果没有这个标号的话就算了
        if(idOrb==-1)
        {
            return;
        }
        try
        {
            //发送数据头
            streamHandle.writeInt(FightProtocol.ORB_UPDATE_DES);
            //发送球的标号
            streamHandle.writeInt(idOrb);
            //发送更新后的球的回合结束的数值
            streamHandle.writeInt(orb.passiveAmount);
            //发送球的激发数值
            streamHandle.writeInt(orb.evokeAmount);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public MonsterDark() {
        this.ID = "Dark";
        this.img = ImageMaster.ORB_DARK;
        this.name = orbString.NAME;
        this.baseEvokeAmount = 6;
        this.evokeAmount = this.baseEvokeAmount;
        this.basePassiveAmount = 6;
        this.passiveAmount = this.basePassiveAmount;
        this.updateDescription();
        this.channelAnimTimer = 0.5F;
    }

    public void updateDescription() {
        this.applyFocus();
        this.description = DESC[0] + this.passiveAmount + DESC[1] + this.evokeAmount + DESC[2];
        AutomaticSocketServer server = AutomaticSocketServer.getServer();
        //发送新的球的描述
        orbDescriptionEncode(this,server.streamHandle);
        server.send();
    }

    public void onEvoke() {
        AbstractDungeon.actionManager.addToTop(new DarkOrbEvokeAction(new DamageInfo(AbstractDungeon.player, this.evokeAmount, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.FIRE));
    }

    public void onEndOfTurn() {
        float speedTime = 0.6F / (float)AbstractDungeon.player.orbs.size();
        if (Settings.FAST_MODE) {
            speedTime = 0.0F;
        }

        AbstractDungeon.actionManager.addToBottom(new VFXAction(new OrbFlareEffect(this, OrbFlareEffect.OrbFlareColor.DARK), speedTime));
        this.evokeAmount += this.passiveAmount;
        this.updateDescription();
        AutomaticSocketServer server = AutomaticSocketServer.getServer();
        sendDarkEndTurn(this,server.streamHandle);
        server.send();
    }

    public void triggerEvokeAnimation() {
        CardCrawlGame.sound.play("ORB_DARK_EVOKE", 0.1F);
        AbstractDungeon.effectsQueue.add(new DarkOrbActivateEffect(this.cX, this.cY));
    }

    public void applyFocus() {
        AbstractPower power = AbstractDungeon.player.getPower("Focus");
        if (power != null) {
            this.passiveAmount = Math.max(0, this.basePassiveAmount + power.amount);
        } else {
            this.passiveAmount = this.basePassiveAmount;
        }

    }

    public void updateAnimation() {
        super.updateAnimation();
        this.angle += Gdx.graphics.getDeltaTime() * 120.0F;
        this.vfxTimer -= Gdx.graphics.getDeltaTime();
        if (this.vfxTimer < 0.0F) {
            AbstractDungeon.effectList.add(new DarkOrbPassiveEffect(this.cX, this.cY));
            this.vfxTimer = 0.25F;
        }

    }

    public void render(SpriteBatch sb) {
        sb.setColor(this.c);
        sb.draw(this.img, this.cX - 48.0F, this.cY - 48.0F + this.bobEffect.y, 48.0F, 48.0F, 96.0F, 96.0F, this.scale, this.scale, this.angle, 0, 0, 96, 96, false, false);
        this.shineColor.a = this.c.a / 3.0F;
        sb.setColor(this.shineColor);
        sb.setBlendFunction(770, 1);
        sb.draw(this.img, this.cX - 48.0F, this.cY - 48.0F + this.bobEffect.y, 48.0F, 48.0F, 96.0F, 96.0F, this.scale * 1.2F, this.scale * 1.2F, this.angle / 1.2F, 0, 0, 96, 96, false, false);
        sb.draw(this.img, this.cX - 48.0F, this.cY - 48.0F + this.bobEffect.y, 48.0F, 48.0F, 96.0F, 96.0F, this.scale * 1.5F, this.scale * 1.5F, this.angle / 1.4F, 0, 0, 96, 96, false, false);
        sb.setBlendFunction(770, 771);
        this.renderText(sb);
        this.hb.render(sb);
    }

    protected void renderText(SpriteBatch sb) {
        FontHelper.renderFontCentered(sb, FontHelper.cardEnergyFont_L, Integer.toString(this.evokeAmount), this.cX + NUM_X_OFFSET, this.cY + this.bobEffect.y / 2.0F + NUM_Y_OFFSET - 4.0F * Settings.scale, new Color(0.2F, 1.0F, 1.0F, this.c.a), this.fontScale);
        FontHelper.renderFontCentered(sb, FontHelper.cardEnergyFont_L, Integer.toString(this.passiveAmount), this.cX + NUM_X_OFFSET, this.cY + this.bobEffect.y / 2.0F + NUM_Y_OFFSET + 20.0F * Settings.scale, this.c, this.fontScale);
    }

    public void playChannelSFX() {
        CardCrawlGame.sound.play("ORB_DARK_CHANNEL", 0.1F);
    }

    public AbstractOrb makeCopy() {
        return new Dark();
    }

    static {
        orbString = CardCrawlGame.languagePack.getOrbString("Dark");
        DESC = orbString.DESCRIPTION;
    }

}
