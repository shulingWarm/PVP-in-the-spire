package pvp_in_the_spire.ui;

import pvp_in_the_spire.helpers.FontLibrary;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.FontHelper;

//带标签的输入框
public class InputBoxWithLabel extends AbstractPage {

    //显示在输入框左边的文本
    public TextLabel label;

    //输入框
    public InputBox inputBox;

    public InputBoxWithLabel(float x,float y,float width,float height,
         String text, //显示在输入框旁边的文本
         BitmapFont font,
         boolean clickableFlag //输入框是否可以点击，适用于有多个点击框的时候
    )
    {
        //记录当前页面的位置
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        //记录label
        label = new TextLabel(x,y,width,height,text, font);
        //记录成左对齐
        label.isLeftAlign = true;

        //临时计算label文本的宽度
        float labelWidth = FontHelper.getSmartWidth(font,
            text,100000.f,0.f,1);

        //初始化输入框
        if(clickableFlag)
        {
            this.inputBox = new ClickableInputBox(
                    this.x + labelWidth,
                    this.y + this.height * 0.5f,
                    this.width - labelWidth, this.height,
                    font
            );
        }
        else {
            this.inputBox = new InputBox(
                    this.x + labelWidth,
                    this.y + this.height * 0.5f,
                    this.width - labelWidth, this.height,
                    font
            );
        }
    }

    //不带字体的输入
    public InputBoxWithLabel(float x,float y,float width,float height,
                             String text //显示在输入框旁边的文本
    )
    {
        this(x,y,width,height,text,FontLibrary.getBaseFont(),false);
    }

    //获取文本信息
    public String getText()
    {
        return this.inputBox.textField;
    }


    //触发编辑状态
    public void triggerEdit()
    {
        //打开输入框里面的编辑状态
        this.inputBox.open();
    }

    //渲染过程
    @Override
    public void render(SpriteBatch sb) {
        this.label.render(sb);
        //初始化输入框
        this.inputBox.render(sb);
    }

    @Override
    public void update() {
        this.label.update();
        this.inputBox.update();
    }
}
