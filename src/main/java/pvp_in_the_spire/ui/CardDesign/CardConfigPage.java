package pvp_in_the_spire.ui.CardDesign;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import pvp_in_the_spire.helpers.FontLibrary;
import pvp_in_the_spire.ui.AbstractPage;
import pvp_in_the_spire.ui.BasePanel;
import pvp_in_the_spire.ui.InputBoxWithLabel;

//对单独某一个卡牌的配置界面
public class CardConfigPage extends AbstractPage {

    //显示主卡牌的界面
    public AbstractCard mainCard;
    //用于显示配置选项的panel
    public BasePanel optionPanel;

    public CardConfigPage()
    {
        //初始化panel
        this.optionPanel = new BasePanel(Settings.WIDTH*0.3f,
                Settings.HEIGHT*0.2f,Settings.WIDTH*0.5f,
                Settings.HEIGHT*0.75f);
        //初始化panel
        initPanel();
    }

    //初始化panel 其实就是初始化各种选项
    public void initPanel()
    {
        //随便在里面添加两个input box,这个仅仅是测试显示效果
        for(int i=0;i<3;++i)
        {
            //临时的input box
            InputBoxWithLabel tempInputBox = new InputBoxWithLabel(
                    Settings.WIDTH * 0.2f,
                    Settings.HEIGHT * 0.5f,
                    Settings.WIDTH * 0.3f,
                    Settings.HEIGHT * 0.025f,
                    "测试输入框",
                    FontLibrary.getFontWithSize(33),
                    true
            );
            tempInputBox.height = tempInputBox.height*3f;
            this.optionPanel.addNewPage(tempInputBox);
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        optionPanel.render(sb);
    }

    @Override
    public void update() {
        optionPanel.update();
    }
}
