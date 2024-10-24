package pvp_in_the_spire.ui.BattleUI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.MathHelper;

//敌人的能量框
public class MonsterEnergyPanel {

    //用于渲染能量的角色
    public AbstractPlayer player = null;
    //当前的能量信息
    public int currentEnergy = 0;
    //最大的能量
    public int masterEnergy = 0;
    //能量框被渲染的位置
    float x;
    float y;
    //字体的大小
    float fontScale = 1.f;

    public MonsterEnergyPanel(float x,float y)
    {
        this.x = x;
        this.y = y;
    }

    public void init(AbstractPlayer player,int masterEnergy)
    {
        this.player = player;
        this.masterEnergy = masterEnergy;
    }

    public void setCurrentEnergy(int currentEnergy) {
        this.currentEnergy = currentEnergy;
    }

    public void update()
    {
        if(this.player == null)
            return;
        //如果字体正处于放在状态，对字体大小做一下更新
        if(this.fontScale > 1.f)
        {
            this.fontScale = MathHelper.scaleLerpSnap(this.fontScale,1.f);
        }
        //更新能量框的动画
        this.player.updateOrb(this.currentEnergy);
    }

    public void render(SpriteBatch sb)
    {
        if(player == null)
            return;
        this.player.renderOrb(sb,this.currentEnergy>0,this.x,this.y);
        //准备用于渲染的文字
        String energyString = this.currentEnergy + "/" + this.masterEnergy;
        //用于渲染的字体
        BitmapFont font = this.player.getEnergyNumFont();
        font.getData().setScale(this.fontScale);
        //把文字渲染在中间
        FontHelper.renderFontCentered(sb,font,energyString,this.x,this.y, Color.WHITE);
    }

}
