package pvp_in_the_spire.screens;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.badlogic.gdx.graphics.Color;

//用于显示警告信息的工具，其实只是处理一个显示的位置
public class WarningText {

    //要显示的字符串
    String shownText;

    BitmapFont font;

    //文字显示的位置
    float x;
    float y;

    //文本显示的颜色
    Color color;

    //目前的帧数
    public int idFrame=10000;

    //结束scale变化的时间
    int endScaleTime = 25;

    //开始变化透明度的时间
    int changeAlphaBegin = 80;

    //结束变化透明度的时间
    int changeAlphaEnd = 130;

    public WarningText(String text,BitmapFont font,float x,float y,
       Color color)
    {
        this.shownText = text;
        //记录要显示的字体
        this.font = font;
        this.x = x;
        this.y = y;
        this.color = color.cpy();
    }

    //获得目前的scale
    float getScale()
    {
        //如果超过6就是正常的scale
        if(idFrame>=endScaleTime)
        {
            return 1;
        }
        //在此之前做一个缩放的动画
        return 3.F - (2.F/endScaleTime)*idFrame;
    }

    //获得目前的alpha变化
    float getAlpha()
    {
        //如果还没开始变化透明度，就返回1
        if(idFrame<changeAlphaBegin)
            return 1;
        //如果已经变化完了就返回0
        if(idFrame>changeAlphaEnd)
            return 0;
        //计算变化过程中的数值
        return (float)(changeAlphaEnd - idFrame) /
            (float)(changeAlphaEnd - changeAlphaBegin);
    }

    //判断是否需要渲染
    boolean needRender()
    {
        //如果alpha已经变成0,那就不用了
        return idFrame<=changeAlphaEnd;
    }

    //渲染操作
    public void render(SpriteBatch sb)
    {
        //如果不需要渲染就直接返回
        if(!needRender())
        {
            return;
        }
        //更改透明度
        color.a = getAlpha();
        //把文字渲染在中间
        FontHelper.renderFontCentered(sb,font,shownText,x,y,color,getScale());
        //更新渲染帧
        idFrame++;
    }
}
