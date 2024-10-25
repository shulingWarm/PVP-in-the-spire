package pvp_in_the_spire.ui.Button.WithUpdate;

import pvp_in_the_spire.ui.Events.ClickCallback;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

//带闪烁效果的按钮
public class TwinkleButton extends BaseUpdateButton {

    //目前的闪烁状态
    public boolean twinkleFlag = false;

    //当前的颜色值
    public Color twinkleColor;

    //判断目前是上升阶段还是下降阶段
    public boolean isAlphaUp = false;

    //更新颜色时随时间的步长
    public static final float ALPHA_STEP = 1.5f;

    public static final float TIP_OFF_X = 140.0F * Settings.scale;
    private static final float TIP_Y = (float)Settings.HEIGHT - 120.0F * Settings.scale;

    //用于显示提示的字符
    public String tipString;

    public TwinkleButton(float x, float y,float width,float height,
                            String text,
                            BitmapFont font,
                            Texture img,
                            ClickCallback callback,
                            String tipString //鼠标悬停时的提示词
    )
    {
        super(x,y,width,height,text,font,img,callback);
        //初始化用于闪烁的颜色
        twinkleColor = new Color(0.7f,0.4f,0,1);
        this.tipString = tipString;
    }

    @Override
    public void update() {
        super.update();
        //判断是否需要更新闪烁的值
        if(this.twinkleFlag)
        {
            //判断是否为上升阶段
            if(isAlphaUp)
            {
                this.twinkleColor.r += Gdx.graphics.getDeltaTime() * ALPHA_STEP;
                if(this.twinkleColor.r >= 1)
                {
                    this.twinkleColor.r = 1;
                    this.isAlphaUp = false;
                }
            }
            else {
                this.twinkleColor.r -= Gdx.graphics.getDeltaTime() * ALPHA_STEP;
                if(this.twinkleColor.r < 0.3f)
                {
                    this.twinkleColor.r = 0.3f;
                    this.isAlphaUp = true;
                }
            }
            this.twinkleColor.g = twinkleColor.r;
        }
        //判断是否需要显示tip
        if(this.hb.hovered && this.tipString != null)
        {
            //显示tip string
            TipHelper.renderGenericTip((float) InputHelper.mX, (float) InputHelper.mY,"", this.tipString);
        }
    }

    //设置是否进入闪烁状态
    public void setTwinkle(boolean twinkle)
    {
        this.twinkleFlag = twinkle;
    }

    @Override
    public Color getInactiveColor() {
        if(!twinkleFlag)
            return super.getInactiveColor();
        return this.twinkleColor;
    }
}
