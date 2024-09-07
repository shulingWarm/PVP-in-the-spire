package UI.Chat;

import UI.*;
import UI.Button.WithUpdate.BaseUpdateButton;
import UI.Events.ClickCallback;
import UI.Events.EnterInterface;
import UI.Text.AdvTextManager;
import UI.Text.MultiRowInputBox;
import UI.Text.MultiRowLabel;
import WarlordEmblem.AutomaticSocketServer;
import WarlordEmblem.Events.ChatMessageEvent;
import WarlordEmblem.PVPApi.Communication;
import WarlordEmblem.helpers.FontLibrary;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;

import java.lang.reflect.WildcardType;

//最基本的聊天框，里面用来显示各种聊天要素
public class ChatBox extends AbstractPage
    implements ClickCallback,
        EnterInterface
{

    //聊天框的宽度和高度
    public static final float BOX_WIDTH = Settings.WIDTH * 0.3f;
    public static final float BOX_HEIGHT = Settings.HEIGHT * 0.7f;
    public static final float BOX_X = 0;
    public static final float BOX_Y = Settings.HEIGHT * 0.15f;
    //发送按钮的宽度
    public static final float SEND_BUTTON_WIDTH = BOX_WIDTH * 0.2f;

    //纯色背景
    public PlainBox background;

    //聊天框
    public MultiRowInputBox inputBox;

    //用于表示发送的按钮
    public BaseUpdateButton sendButton;

    //显示聊天内容的panel
    public BasePanel messagePanel;

    //判断是否为打开的状态
    public boolean isOpen = false;

    public ChatBox()
    {
        //初始化背景框
        this.background = new PlainBox(BOX_WIDTH,BOX_HEIGHT,
                Color.valueOf("366D67FF"));
        //把背景移动到中间的位置
        this.background.x = BOX_X;
        this.background.y = BOX_Y;

        //初始化输入框
        this.inputBox = new MultiRowInputBox(
            BOX_X + BOX_WIDTH * 0.04f,BOX_Y,
                BOX_WIDTH - SEND_BUTTON_WIDTH,
                BOX_HEIGHT * 0.05f,
                FontLibrary.getFontWithSize(34)
        );
        //设置为允许输入所有字符
        inputBox.allowAllSymbol = true;
        //响应回车的事件
        inputBox.enterInterface = this;

        //初始化发送按钮
        this.sendButton = new BaseUpdateButton(
            BOX_WIDTH - SEND_BUTTON_WIDTH * 0.8f,
                BOX_Y - SEND_BUTTON_WIDTH / 3,
                SEND_BUTTON_WIDTH * 0.6f,
                SEND_BUTTON_WIDTH * 0.6f,"",
                FontLibrary.getBaseFont(),
                TextureManager.SEND_BUTTON,
                this
        );
        //初始化panel
        this.messagePanel = new BottomInsertPanel(
            BOX_X,BOX_Y + inputBox.height,
            BOX_WIDTH,BOX_HEIGHT- inputBox.height
        );
    }

    public void open()
    {
        this.isOpen = true;
        this.inputBox.open();
        //把消息框拉到最下面
        messagePanel.scrolledUsingBar(1);
    }

    @Override
    public void close() {
        this.isOpen = false;
        //关闭输入框的内容
        this.inputBox.close();
    }

    @Override
    public void render(SpriteBatch sb) {
        //渲染背景
        this.background.render(sb);
        this.messagePanel.render(sb);
        //渲染输入框
        this.inputBox.render(sb);
        this.sendButton.render(sb);
    }

    @Override
    public void update() {
        this.background.update();
        this.messagePanel.update();
        this.inputBox.update();
        this.sendButton.update();
    }


    @Override
    public void clickEvent(BaseUpdateButton button) {
        //调用输入框的回车事件
        this.inputBox.enterPressed();
    }

    @Override
    public void enterPressed(String message) {
        //目前不需要实现这个函数
    }

    @Override
    public void enterPressed(AdvTextManager message) {
        //新建多行文本
        MultiRowLabel label = new MultiRowLabel(
            message,0,0,Color.WHITE
        );
        //把页面添加到panel里面
        this.messagePanel.addNewPage(label);
        //判断现在是否可以发送消息
        if(AutomaticSocketServer.globalServer != null)
        {
            Communication.sendEvent(new ChatMessageEvent(message));
        }
    }

    //用于处理收到的消息
    public void receiveMessage(String message)
    {
        //新建一个消息管理器
        AdvTextManager textManager = new AdvTextManager(
            this.inputBox.width,this.inputBox.font
        );
        textManager.appendStr(message);
        textManager.freeze();
        //新建多行文本
        MultiRowLabel tempLabel = new MultiRowLabel(
            textManager,0,0,Color.ORANGE
        );
        this.messagePanel.addNewPage(tempLabel);
    }
}
