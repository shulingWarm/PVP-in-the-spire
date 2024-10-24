package pvp_in_the_spire.ui;

//添加page的时候在最下面添加
public class BottomInsertPanel extends BasePanel {

    public BottomInsertPanel(float x,float y,float width,float height)
    {
        super(x,y,width,height);
        //设置滚动条的默认值
        scrollInitLocation = 1.f;
        //重新更新当前的滚动条位置
        this.scrolledUsingBar(1.f);
    }

    @Override
    public void initScrollRange() {
        scrollRange[0] = this.y;
        scrollRange[1] = this.y + this.height / 2;
    }

    //添加page时的操作
    @Override
    public void addNewPage(AbstractPage page) {
        //临时记录旧的滚动位置
        float oldScrollLocation = this.scrollPercent;
        scrolledUsingBar(scrollInitLocation);
        //增加上界的范围
        scrollRange[1] += (page.height + pageGap);
        //更新page的位置
        for(AbstractPage eachPage : this.pageList)
        {
            eachPage.move(0,(page.height+pageGap));
        }
        //把新的page放到底部
        page.x = this.x + xGap;
        page.y = this.y + 2.f*pageGap;
        pageList.add(page);
        //再把滚动条放回旧的位置
        scrolledUsingBar(oldScrollLocation);
    }
}
