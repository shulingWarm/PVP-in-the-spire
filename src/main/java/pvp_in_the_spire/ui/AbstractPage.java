package pvp_in_the_spire.ui;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

//抽象的UI界面，除了有个渲染功能别的什么都没有
public class AbstractPage extends InputAdapter {

    //当前的位置
    public float x=0;
    public float y=0;
    public float width=0;
    public float height=0;

    //是否已经被关闭
    public boolean judgeIsClosed()
    {
        return false;
    }

    public void render(SpriteBatch sb)
    {

    }

    //页面的更新
    public void update()
    {

    }

    //需要子类实现的函数，对里面的内容做相应的位移
    public void move(float xChange,float yChange)
    {
        x+=xChange;
        y+=yChange;
    }

    public void moveTo(float targetX,float targetY)
    {
        this.move(targetX-x,targetY-y);
    }

    public void copyLocation(AbstractPage otherPage)
    {
        this.x = otherPage.x;
        this.y = otherPage.y;
        this.height = otherPage.height;
        this.width = otherPage.width;
    }

    //关闭页面
    public void close()
    {

    }

    public void open()
    {}


}
