package pvp_in_the_spire.ui.Text;

import pvp_in_the_spire.ui.InputBox;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

//多行文本的输入器
public class MultiRowInputBox extends InputBox {

    //多行文本的管理器
    public AdvTextManager textManager;

    public MultiRowInputBox(float x,float y,float width,float height,
                    BitmapFont font)
    {
        super(x,y,width,height,font);
        //初始化文本管理器
        textManager = new AdvTextManager(width,font);
    }

    @Override
    public void enterPressed() {
        //判断是否有回车响应
        if(this.enterInterface != null)
        {
            this.enterInterface.enterPressed(this.textManager);
            //重置textManager
            this.textManager = new AdvTextManager(this.textManager.width,
                    this.textManager.font);
            //删除textField里面的内容
            this.textField = "";
        }
    }

    //维护退格操作
    @Override
    public void backspace() {
        textManager.backspace();
        textField = textManager.getLastLine();
    }

    //追加字符
    @Override
    public void appendStr(String str) {
        textManager.appendStr(str);
        textField = textManager.getLastLine();
    }
}
