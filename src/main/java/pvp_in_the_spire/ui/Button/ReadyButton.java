package pvp_in_the_spire.ui.Button;

import pvp_in_the_spire.screens.UserButton;
import pvp_in_the_spire.actions.ConfigProtocol;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.io.DataOutputStream;
import java.io.IOException;

//config界面表示准备的button
public class ReadyButton extends UserButton {

    public static final UIStrings uiStrings =
            CardCrawlGame.languagePack.getUIString("ReadyButton");

    public boolean readyFlag = false;

    //准备事件的回调函数
    public ReadyButtonCallback readyButtonCallback=null;

    public ReadyButton(float x, float y, float width, float height,
       BitmapFont font)
    {
        super(x,y,width,height,uiStrings.TEXT[0],font);
    }

    public void sendReadyInfo(DataOutputStream streamHandle)
    {
        //写入config更新的数据头
        try
        {
            streamHandle.writeInt(ConfigProtocol.READY_INFO);
            //如果目前已经准备了就写入1
            if(this.readyFlag)
            {
                streamHandle.writeInt(1);
            }
            else {
                streamHandle.writeInt(0);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //根据目前的状态更新显示的字符
    public void updateButtonText()
    {
        if(readyFlag)
        {
            this.text = uiStrings.TEXT[1];
        }
        else {
            this.text = uiStrings.TEXT[0];
        }
    }

    //手动设置准备的状态
    public void setReadyFlag(boolean readyFlag)
    {
        this.readyFlag = readyFlag;
        updateButtonText();
    }


    //点击事件
    @Override
    public void clickEvent() {
        setReadyFlag(!readyFlag);
        //如果有回调函数，就执行回调
        if(this.readyButtonCallback!=null)
        {
            readyButtonCallback.pressReady(readyFlag);
        }
    }
}
