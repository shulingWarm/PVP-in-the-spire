package pvp_in_the_spire.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

//用来以网格的形式维护页面的操作
public class GridPanel extends AbstractPage {

    public ArrayList<AbstractPage> pageList;

    //一行一共有几个格子
    public int gridWidth;
    //每个网格单元的大小
    public float cellWidth;
    public float cellHeight;

    //交换两个panel的内容
    public static void exchangePanel(GridPanel panel1,GridPanel panel2)
    {
        ArrayList<AbstractPage> tempList = panel1.pageList;
        panel1.pageList = panel2.pageList;
        panel2.pageList = tempList;
        //重置两个panel的位置
        panel1.allocateLocation();
        panel2.allocateLocation();
    }


    public GridPanel(int gridWidth,
         float cellWidth,float cellHeight,
         float x, float y
    )
    {
        pageList = new ArrayList<>();
        //记录网格的宽度以及每个格子的shape
        this.gridWidth = gridWidth;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.x = x;
        this.y = y;
    }

    public void allocatePageLocation(AbstractPage page,
                                     int idRow,int idCol
    )
    {
        float x = (float)idCol * cellWidth + this.x;
        float y = this.y - cellHeight * (idRow + 1);
        page.moveTo(x,y);
    }

    public void allocateLocation()
    {
        //当前正在访问的page
        int idPage = 0;
        //遍历每一列
        for(int idRow=0;idPage<pageList.size();++idRow)
        {
            //遍历每一列
            for(int idCol=0;idCol<gridWidth;++idCol)
            {
                if(idPage >= this.pageList.size())
                    break;
                //给当前的page分配位置
                allocatePageLocation(
                    this.pageList.get(idPage),idRow,idCol
                );
                ++idPage;
            }
        }
    }

    //添加新的page的逻辑
    public void addPage(AbstractPage page)
    {
        //目前的行
        int idRow = pageList.size() / this.gridWidth;
        int idCol = pageList.size() % this.gridWidth;
        this.pageList.add(page);
        //计算page的位置
        allocatePageLocation(page,idRow,idCol);
    }

    //移除特定的页面
    public void removePage(AbstractPage page)
    {
        pageList.remove(page);
        allocateLocation();
    }

    //移除所有的page
    public void removeAllPages()
    {
        this.pageList.clear();
    }

    @Override
    public void render(SpriteBatch sb) {
        //依次渲染每个page
        for(AbstractPage eachPage : pageList)
            eachPage.render(sb);
    }

    //依次更新每个page
    @Override
    public void update() {
        for(AbstractPage eachPage : pageList)
            eachPage.update();
    }
}
