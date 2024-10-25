package pvp_in_the_spire.ui.BattleUI;

import pvp_in_the_spire.ui.AbstractPage;
import pvp_in_the_spire.ui.BasePanel;
import pvp_in_the_spire.ui.RelicPage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.relics.AbstractRelic;

//敌方的relic的panel
public class MonsterRelicPanel extends BasePanel {

    public MonsterRelicPanel()
    {
        super(Settings.WIDTH*0.935f, Settings.HEIGHT*0.1f,Settings.WIDTH*0.2f,Settings.HEIGHT*0.8f);
    }

    //添加遗物
    public void addRelic(AbstractRelic relic)
    {
        //添加新的panel信息
        this.addNewPage(new RelicPage(relic));
    }

    @Override
    public void render(SpriteBatch sb) {
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
