package pvp_in_the_spire.screens.midExit;

import pvp_in_the_spire.helpers.FontLibrary;
import pvp_in_the_spire.patches.connection.InputIpBox;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;

//试图中途退出游戏时显示的界面
public class MidExitScreen {

    //用单例模式来处理，如果新建的话最多也只新建一个
    public static MidExitScreen screenInstance = null;

    //渲染时使用的颜色
    public Color color;

    //结束游戏的按钮
    public ExitButton exitButton;
    //取消退出的按钮
    public ExitCancelButton cancelButton;

    //强制退出的标志，如果是强制退出的话，那么就不准取消这个页面
    public boolean forceExitFlag = false;

    //调整为强制取消退出
    public void makeForceExit()
    {
        forceExitFlag=true;
        exitButton.forceExitFlag = true;
        //禁止选退出
        cancelButton.disabled = true;
    }

    //渲染文字时使用的字体
    public static BitmapFont buttonFont=null;

    public static void prepareButtonFont()
    {
        if(buttonFont==null)
        {
            buttonFont = FontLibrary.getFontWithSize(40);
        }
    }

    //接收退出游戏的信号
    public static void receiveExitInfo()
    {
        //判断目前有没有玩家退出的窗口
        if(screenInstance==null)
        {
            onOpen();
        }
        screenInstance.makeForceExit();
    }

    public MidExitScreen()
    {
        color =  new Color(0.6F, 0.965F, 0.886F, 1.0F);
        //准备渲染按钮上的文字使用的字体
        prepareButtonFont();
        exitButton=new ExitButton((float)Settings.WIDTH*3.0F/8.0F,(float)Settings.HEIGHT*7.0F/15.0F,
                (float)Settings.WIDTH/4,
                (float)Settings.HEIGHT/5,buttonFont);
        //取消退出的按钮
        cancelButton = new ExitCancelButton((float)Settings.WIDTH*3.0F/8.0F,(float)Settings.HEIGHT/3,
                (float)Settings.WIDTH/4,
                (float)Settings.HEIGHT/5,buttonFont);
    }

    //渲染画面时的操作
    public void render(SpriteBatch sb)
    {
        sb.setColor(color);
        sb.draw(ImageMaster.OPTION_CONFIRM, (float) Settings.WIDTH / 3.0F, Settings.HEIGHT/3.0F, Settings.WIDTH/3.0F,Settings.HEIGHT/3.0F);
        //渲染退出时用的按钮
        exitButton.render(sb);
        cancelButton.render(sb);
    }

    //试图打开这个页面的操作，当第二次点击时会直接释放这个实例
    public static void onOpen()
    {
        if(screenInstance==null)
        {
            screenInstance = new MidExitScreen();
        }
        else if(!screenInstance.forceExitFlag) {
            screenInstance = null;
        }
    }

    //执行画面的渲染逻辑
    public static void onRender(SpriteBatch sb)
    {
        if(screenInstance!=null)
        {
            //如果已经宣告取消了就删掉这个实体
            if(screenInstance.cancelButton.clicked &&
                !(screenInstance.forceExitFlag))
            {
                screenInstance=null;
            }
            else {
                screenInstance.render(sb);
            }
        }
    }

}
