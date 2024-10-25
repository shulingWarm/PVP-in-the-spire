package pvp_in_the_spire.ui.Button.WithUpdate;

import pvp_in_the_spire.ui.AbstractPage;
import pvp_in_the_spire.ui.Events.ClickCallback;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

//这个是基础的带update功能的按钮，与之前的那个只会单独判断是否hover的按钮不同
public class BaseUpdateButton extends AbstractPage {

    public Texture img;
    public Hitbox hb;
    public com.badlogic.gdx.graphics.Color activeColor;
    public com.badlogic.gdx.graphics.Color inactiveColor;
    public boolean pressed;
    public String text;
    //渲染用的字体
    BitmapFont font;
    //点击事件的回调函数
    ClickCallback callback;

    public BaseUpdateButton(float x, float y,float width,float height,
        String text,
        BitmapFont font,
        Texture img,
        ClickCallback callback
        ) {
        //记录点击时的回调
        this.callback = callback;
        this.activeColor = com.badlogic.gdx.graphics.Color.WHITE;
        this.inactiveColor = new com.badlogic.gdx.graphics.Color(0.6F, 0.6F, 0.6F, 1.0F);
        this.pressed = false;
        this.font = font;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.img = img;
        this.hb = new Hitbox(x, y, width, height);
        //记录要显示的文本内容
        this.text = text;
    }

    //按钮的点击事件
    public void clickEvent()
    {
        //调用回调函数
        callback.clickEvent(this);
    }

    //控件的位置移动
    @Override
    public void move(float xChange, float yChange) {
        super.move(xChange, yChange);
        //更新hit box目前的位置
        this.hb.x = this.x;
        this.hb.y = this.y;
    }

    public void update() {
        this.hb.update(this.x, this.y);
        if (this.hb.hovered && InputHelper.justClickedLeft) {
            this.pressed = true;
            InputHelper.justClickedLeft = false;
            //调用按钮点击事件
            this.clickEvent();
        }

    }

    //获取一般的渲染color
    public Color getInactiveColor()
    {
        return this.inactiveColor;
    }

    public void render(SpriteBatch sb) {
        if (this.hb.hovered) {
            sb.setColor(this.activeColor);
        } else {
            sb.setColor(this.getInactiveColor());
        }

        if(this.img != null)
            sb.draw(this.img, this.x, this.y,
                    this.width,
                    this.height);
        sb.setColor(Color.WHITE);
        //画按钮上的字体
        float fontX = x + 0.5F*width;
        FontHelper.renderFontCentered(sb,font,text,fontX,y+0.5F*height);
        this.hb.render(sb);
    }
}
