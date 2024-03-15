package UI.configOptions;

import UI.AbstractPage;
import UI.ConfigPage;
import UI.Events.ConfigChangeEvent;
import UI.PlainBox;
import WarlordEmblem.actions.ConfigProtocol;
import WarlordEmblem.patches.connection.InputIpBox;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.ImageMaster;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//自定义配置时的页面
public class AbstractConfigOption extends AbstractPage{

    //一个纯色平板
    public PlainBox plainBox;

    //是否处于上升状态
    public boolean alphaUp = false;

    //更新亮度时的时间幅度
    public static final float TIME_SCALE = 10.0f;

    public static BitmapFont optionFont = null;

    //option的标号，用于辅助通信过程
    public int optionId = 0;

    //目前是否可以交互
    public boolean enableFlag = true;

    public static BitmapFont getOptionFont()
    {
        if(optionFont==null)
        {
            optionFont = InputIpBox.generateFont(24);
        }
        return optionFont;
    }

    public AbstractConfigOption()
    {
        //初始化纯色平板
        plainBox = new PlainBox(this.width,this.height, Color.GOLD.cpy());
        plainBox.color.a = 0;
        plainBox.x = this.x;
        plainBox.y = this.y;
        plainBox.texture = ImageMaster.WHITE_SQUARE_IMG;
    }

    //当config内容发生改变时的回调
    public void receiveConfigChange(DataInputStream streamHandle)
    {

    }

    //设置这个option是否可交互
    public void setEnable(boolean enableFlag)
    {
        this.enableFlag = enableFlag;
    }

    //发光特效，主要是给子类用的
    public void highlight()
    {
        //改成发光上升状态
        this.alphaUp = true;
    }

    //设置option的id
    public void setOptionId(int newId)
    {
        this.optionId = newId;
    }

    //发送config变化的信息
    public void sendConfigChange(DataOutputStream streamHandle,int sendingData)
    {
        //发送config变化的数据头
        try
        {
            streamHandle.writeInt(ConfigProtocol.CHANGE_CONFIG);
            //发送自己的id
            streamHandle.writeInt(optionId);
            //发送要写入的数据
            streamHandle.writeInt(sendingData);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //渲染背景
    public void renderHighlight(SpriteBatch sb)
    {
        //判断是否需要更新
        if((!this.alphaUp) && plainBox.color.a<=0)
        {
            return;
        }
        //把highlight和现在保持一致
        plainBox.copyLocation(this);
        //初始化更新数值
        float alphaChange = Gdx.graphics.getDeltaTime() * TIME_SCALE;
        //如果处于下降的就换成负数
        if(!this.alphaUp)
        {
            alphaChange = -alphaChange;
        }
        //更新数值
        plainBox.color.a += alphaChange;
        //判断是否已经大于1
        if(plainBox.color.a>=1)
        {
            plainBox.color.a = 1;
            this.alphaUp = false;
        }
        else if(plainBox.color.a<=0)
        {
            plainBox.color.a = 0;
        }
        plainBox.render(sb);
    }

    @Override
    public void render(SpriteBatch sb) {
        renderHighlight(sb);
    }
}
