package pvp_in_the_spire.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBar;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBarListener;

import java.util.ArrayList;

//最基本的可以无限拉伸的列表
public class BasePanel extends AbstractPage implements ScrollBarListener {

    //每个项目的默认间隔
    public float pageGap = Settings.HEIGHT * 0.005F;
    //panel的x方向的gap
    public float xGap = Settings.WIDTH * 0.01F;

    //目前的y偏移量
    public float currentYOffset = 0;
    //滚动条的上下范围
    public float[] scrollRange = new float[2];
    //滚动条的显示位置
    ScrollBar scrollBar;

    //目前的滚轮百分比
    public float scrollPercent = 0.F;

    //目前已经添加的page的列表
    public ArrayList<AbstractPage> pageList = new ArrayList<>();

    //滚动条的初始位置
    public float scrollInitLocation = 0;

    //初始化的时候需要给定完整的位置和宽高
    public BasePanel(float x,float y,float width,float height)
    {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        //最开始的时候滚动条的范围是屏幕顶部
        initScrollRange();
        //初始化滚动条
        scrollBar = new ScrollBar(this,this.x+this.width,this.y + this.height/2,this.height);
        //设置输入的捕捉信息
        //Gdx.input.setInputProcessor(this);

    }

    //初始化滚动条的范围
    public void initScrollRange()
    {
        scrollRange[0] = this.y + this.height;
        scrollRange[1] = this.y + this.height;
    }

    //清空列表
    public void clearPanel()
    {
        this.pageList.clear();
    }

    //获取新的page.y
    public void updatePageY(float panelBound,AbstractPage page)
    {
        page.moveTo(page.x,panelBound - pageGap - page.height);
    }

    //在panel里面添加新的项目
    public void addNewPage(AbstractPage page)
    {
        float minBound = scrollRange[0];
        //如果是加入的第1个，则需要重置滚动条
        if(pageList.isEmpty())
        {
            //这种情况需要更新滚动条，把它放到顶部
            scrolledUsingBar(scrollInitLocation);
        }
        //更新这个page的上边缘位置
        page.moveTo(this.x + xGap,page.y);
        updatePageY(minBound,page);
        scrollRange[0] = page.y;
        pageList.add(page);
        //更新滚动条的显示
        scrolledUsingBar(this.scrollPercent);
    }


    @Override
    public boolean scrolled(int amount) {
        if(amount!=0)
        {
            if(amount>0)
            {
                scrollPercent+=0.02F;
            }
            else {
                scrollPercent-=0.02F;
            }
            if(scrollPercent>1)
                scrollPercent=1;
            if(scrollPercent<0)
                scrollPercent=0;
            //通知更新滚轮的位置
            scrolledUsingBar(scrollPercent);
        }
        return false;
    }

    //对所有的page做移动
    public void movePages(float yOffset)
    {
        //遍历所有的页面，执行页面运动
        for(AbstractPage eachPage : pageList)
        {
            eachPage.move(0,yOffset);
        }
    }

    public void scrolledUsingBar(float newPercent)
    {
        //之前的y值
        float oldY = this.currentYOffset;
        //计算目前的y偏移量
        this.currentYOffset = MathHelper.valueFromPercentBetween(scrollRange[0],scrollRange[1],1-newPercent);
        //更新滚动条显示的位置
        this.scrollBar.parentScrolledToPercent(newPercent);
        //更新页面的位置
        movePages(oldY - this.currentYOffset);
        //记录目前的滚动条位置
        this.scrollPercent = newPercent;
    }

    //更新时更新panel里面的每个选项
    @Override
    public void update() {
        for(AbstractPage eachPage : pageList)
        {
            //如果y超过下边界就不再显示了
            if(eachPage.y < this.y + this.height &&
                    eachPage.y > this.y)
            {
                eachPage.update();
            }
        }
        //处理滚动条的更新
        boolean isDragging = scrollBar.update();
        //如果没有拖动，就看一下滚动条
        if(!isDragging)
        {
            if(InputHelper.scrolledDown)
            {
                this.scrolled(1);
            }
            else if(InputHelper.scrolledUp)
            {
                this.scrolled(-1);
            }
        }
    }

    public void render(SpriteBatch sb)
    {
        //添加对滚动条的渲染
        scrollBar.render(sb);
        //渲染每个page
        //为了确保下拉菜单的情况下可以正常显示，显示的时候改成倒序
        for(int idPage=pageList.size()-1;idPage>=0;--idPage)
        {
            AbstractPage eachPage = pageList.get(idPage);
            //如果y超过下边界就不再显示了
            if(eachPage.y < this.y + this.height &&
                eachPage.y > this.y)
            {
                eachPage.render(sb);
            }
        }
    }

}
