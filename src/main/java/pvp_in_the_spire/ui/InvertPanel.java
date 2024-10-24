package pvp_in_the_spire.ui;

//从下往上滚动的panel
//主要是用来显示聊天信息的
//!!! 注意，这个panel弃用了，本来想给聊天窗口用的，但最后发现聊天窗口里面不是这么个逻辑
// 这个 panel是页面最开始出现在最底下，然后逐步往上累加
public class InvertPanel extends BasePanel {

    public InvertPanel(float x,float y,float width,float height)
    {
        super(x,y,width,height);
        //把滚动条的初始位置设置成1
        this.scrollInitLocation = 1.f;
    }

    //获取上一个page的上边界
    public float getLastPageTop()
    {
        if(pageList.isEmpty())
            return scrollRange[1];
        AbstractPage lastPage = pageList.get(pageList.size()-1);
        return lastPage.y + lastPage.height;
    }

    //反向panel的时候应该从下往上更新
    @Override
    public void updatePageY(float panelBound, AbstractPage page) {
        //获取上一个page的上界
        page.y = getLastPageTop() + pageGap;
    }
}
