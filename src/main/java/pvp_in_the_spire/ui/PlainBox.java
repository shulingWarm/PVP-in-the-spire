package pvp_in_the_spire.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;

//一个纯色的背景块
public class PlainBox extends AbstractPage {

    //需要被显示的纹理
    public Texture texture = ImageMaster.OPTION_CONFIRM;

    //默认的颜色
    public Color color= new Color(1.0F, 0.965F, 0.886F,0.98F);

    public PlainBox()
    {
        this.x = 0;
        this.y=0;
        this.width = Settings.WIDTH;
        this.height = Settings.HEIGHT;
    }

    public PlainBox(float width,float height,Color color)
    {
        this();
        //设置宽度
        this.width = width;
        this.height = height;
        this.color = color;
    }

    public void render(SpriteBatch sb)
    {
        //设置颜色
        sb.setColor(color);
        //渲染底图
        sb.draw(this.texture,this.x,this.y,width,height);
    }

}
