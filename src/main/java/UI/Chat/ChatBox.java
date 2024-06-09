package UI.Chat;

import UI.AbstractPage;
import UI.InputBox;
import UI.PlainBox;
import WarlordEmblem.helpers.FontLibrary;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;

import java.lang.reflect.WildcardType;

//最基本的聊天框，里面用来显示各种聊天要素
public class ChatBox extends AbstractPage {

    //聊天框的宽度和高度
    public static final float BOX_WIDTH = Settings.WIDTH * 0.3f;
    public static final float BOX_HEIGHT = Settings.HEIGHT * 0.7f;
    public static final float BOX_X = 0;
    public static final float BOX_Y = Settings.HEIGHT * 0.15f;

    //纯色背景
    public PlainBox background;

    //聊天框
    public InputBox inputBox;

    public ChatBox()
    {
        //初始化背景框
        this.background = new PlainBox(BOX_WIDTH,BOX_HEIGHT,
                Color.valueOf("366D6799"));
        //把背景移动到中间的位置
        this.background.x = BOX_X;
        this.background.y = BOX_Y;

        //初始化输入框
        this.inputBox = new InputBox(
            BOX_X,BOX_Y,BOX_WIDTH,BOX_HEIGHT * 0.15f
        );
    }

    public void open()
    {
        this.inputBox.open();
    }


    @Override
    public void render(SpriteBatch sb) {
        //渲染背景
        this.background.render(sb);
        //渲染输入框
        this.inputBox.render(sb);
    }

    @Override
    public void update() {
        this.background.update();
        this.inputBox.update();
    }
}
