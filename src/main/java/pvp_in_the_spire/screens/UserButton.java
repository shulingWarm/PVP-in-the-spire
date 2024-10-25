package pvp_in_the_spire.screens;

import pvp_in_the_spire.ui.AbstractPage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;

//为了方便后面使用，定义一个自己的button
public class UserButton extends AbstractPage {

    //按钮上的文本
    public String text;
    public BitmapFont font;
    //按钮下面的纹理
    public Texture buttonTexture;
    public Color fontColor;
    //是否可以使用
    public boolean disabled = false;
    //按钮的默认颜色
    public Color defaultColor;
    //悬停在按钮上的颜色
    public Color hoverColor;
    //按钮被禁用时的颜色
    public Color disableColor;
    //是否使用朴素的hovering判断
    public boolean useDirectHovering = false;

    public UserButton(float x,float y,float width,float height,
          String text,BitmapFont font)
    {
        //记录按钮的位置属性
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;

        //准备按钮上用的字体
        this.font = font;
        //纹理直接使用资源库里面的纹理
        buttonTexture = ImageMaster.END_TURN_BUTTON;
        //按钮上的字体颜色
        fontColor = new Color(1,1,1,1);
        //按钮的颜色
        defaultColor = Color.WHITE;
        //悬停时的颜色
        hoverColor = Settings.GOLD_COLOR;
        //禁用时的颜色
        disableColor = Color.GRAY;
    }

    //对按钮的渲染
    public void render(SpriteBatch sb)
    {
        //如果已经禁用了，换成禁用的颜色
        if(disabled)
        {
            sb.setColor(disableColor);
        }
        //悬停的时候会改变颜色
        else if(isHovering())
        {
            sb.setColor(hoverColor);
        }
        else
            sb.setColor(defaultColor);
        //画按钮的纹理
        sb.draw(buttonTexture,x,y,width,height);
        //画按钮上的字体
        float fontX = x + 0.5F*width;
        FontHelper.renderFontCentered(sb,font,text,fontX,y+0.5F*height);
        //判断是否发生了点击，如果点击了就调用一下点击事件
        if(isJustClicked())
        {
            clickEvent();
        }
    }

    //按钮的点击事件
    public void clickEvent()
    {
        
    }

    //判断鼠标指针是否悬停
    public boolean isHovering()
    {
        //如果已经被禁用了，那就永远不用判断悬停
        if(disabled)
            return false;
        float touchX = Gdx.input.getX();
        float touchY = Gdx.graphics.getHeight() - Gdx.input.getY(); // LibGDX 的 Y 坐标原点在屏幕的顶部
        if(useDirectHovering)
        {
            return touchX >= x && touchX<= x+width &&
                    touchY>=y && touchY <= y+height;
        }
        return touchX >= x && touchX <= x + width && touchY >= y + 0.25*height && touchY <= y + 0.75*height;
    }

    //判断是否刚刚发生了点击
    public boolean isJustClicked()
    {
        //点击的时候首先要在范围内
        if(isHovering())
        {
            return Gdx.input.justTouched();
        }
        return false;
    }

}
