package pvp_in_the_spire.ui.CardDesign;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import pvp_in_the_spire.ui.AbstractPage;
import pvp_in_the_spire.ui.BasePanel;

//这里用于添加一个卡牌打出时，给这个卡牌附加的能量信息
public class CardPowerPage extends AbstractPage {
    //用于显示能量修改框的panel
    public BasePanel powerPanel;

    public CardPowerPage()
    {
        //初始化panel
        this.powerPanel = new BasePanel(Settings.WIDTH*0.3f,
                Settings.HEIGHT*0.1f,Settings.WIDTH*0.4f,
                Settings.HEIGHT*0.8f);
        //初始化panel
        this.initializePanel();
    }

    public void initializePanel()
    {

    }

    @Override
    public void render(SpriteBatch sb) {
        powerPanel.render(sb);
    }

    @Override
    public void update() {
        powerPanel.update();
    }
}
