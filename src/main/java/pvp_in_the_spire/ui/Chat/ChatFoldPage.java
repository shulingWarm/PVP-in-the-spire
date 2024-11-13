package pvp_in_the_spire.ui.Chat;

import pvp_in_the_spire.ui.Button.WithUpdate.BaseUpdateButton;
import pvp_in_the_spire.ui.Button.WithUpdate.TwinkleButton;
import pvp_in_the_spire.ui.FoldPage;
import pvp_in_the_spire.ui.TextureManager;
import pvp_in_the_spire.helpers.FontLibrary;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.Settings;

public class ChatFoldPage extends FoldPage {

    public static ChatFoldPage instance = null;

    public static ChatFoldPage getInstance() {
        if(instance == null)
            instance = new ChatFoldPage();
        return instance;
    }

    //对应的chat box,用于接收消息
    public ChatBox chatBox;

    public ChatFoldPage()
    {
        super();
        //初始化需要被渲染的主界面
        this.chatBox = new ChatBox();
        this.mainPage = this.chatBox;
        //下面这个等数据弄好了还需要再重新修改
        //初始化用于把页面打开的按钮
        openButton = new TwinkleButton(Settings.WIDTH*0.004f, Settings.HEIGHT / 2.f,
                Settings.WIDTH * 0.05f,Settings.HEIGHT * 0.05f,
                "", FontLibrary.getBaseFont(), TextureManager.MESSAGE_BUTTON,
                this,"(((((((Tab)))))))");
        //初始化用于关闭页面的按钮
        //先临时把它显示在右上角
        closeButton = new BaseUpdateButton(Settings.WIDTH * 0.24f,
                Settings.HEIGHT * 0.8f,Settings.WIDTH * 0.05f,
                Settings.WIDTH*0.05f,"",FontLibrary.getBaseFont(),
                TextureManager.CLOSE_BUTTON,this);
    }

    //收到消息
    public void receiveMessage(String message, String sender)
    {
        this.chatBox.receiveMessage(sender + ": " + message,
                Color.ORANGE);
        //判断是否处于打开状态
        if(!this.mainPageFlag)
            this.openButton.setTwinkle(true);
    }

    //显示系统信息
    public void systemMessage(String message, boolean setTwinkle)
    {
        this.chatBox.receiveMessage(message,
                Color.SKY);
        if(!this.mainPageFlag && setTwinkle)
        {
            this.openButton.setTwinkle(true);
        }
    }

    @Override
    public void update() {
        super.update();
        if(Gdx.input.isKeyJustPressed(61))
        {
            //重置聊天窗口的状态
            this.invertStage();
        }
    }
}
