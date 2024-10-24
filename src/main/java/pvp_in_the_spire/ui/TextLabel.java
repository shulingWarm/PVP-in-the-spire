package pvp_in_the_spire.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.FontHelper;

//通过的文本显示
public class TextLabel extends AbstractPage {

    //用于显示的文本
    public String text;
    public BitmapFont font;

    //是否左对齐
    public boolean isLeftAlign = false;

    //参考颜色
    public Color color = Color.WHITE;

    public TextLabel(float x, float y, float width, float height, String text,
                     BitmapFont font)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.font = font;
    }

    @Override
    public void render(SpriteBatch sb) {
        float fontX = x + 0.5F*width;
        //判断是否左对齐
        if(isLeftAlign)
        {
            FontHelper.renderFontLeft(sb,font,text,x,y+0.5f*this.height, color);
        }
        else {
            FontHelper.renderFontCentered(sb,font,text,fontX,y+0.5F*height,color);
        }
    }
}
