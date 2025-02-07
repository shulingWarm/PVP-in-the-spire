package pvp_in_the_spire.ui.CardDesign;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import pvp_in_the_spire.card.AdaptableCard;
import pvp_in_the_spire.helpers.FontLibrary;
import pvp_in_the_spire.ui.AbstractPage;
import pvp_in_the_spire.ui.BasePanel;
import pvp_in_the_spire.ui.CardDesign.CardModifyItem.AttackModify;
import pvp_in_the_spire.ui.InputBoxWithLabel;

//对单独某一个卡牌的配置界面
public class CardConfigPage extends AbstractPage {

    //显示主卡牌的界面
    public AdaptableCard mainCard;
    //用于显示配置选项的panel
    public BasePanel optionPanel;

    //初始化卡牌
    public void initPage(AbstractCard mainCard)
    {
        //初始化panel
        this.optionPanel = new BasePanel(Settings.WIDTH*0.3f,
                Settings.HEIGHT*0.2f,Settings.WIDTH*0.5f,
                Settings.HEIGHT*0.75f);
        this.mainCard = new AdaptableCard(mainCard);
        //更改卡牌显示的位置
        this.mainCard.current_x = Settings.WIDTH*0.2f;
        this.mainCard.current_y = Settings.HEIGHT*0.6f;
        this.mainCard.target_x = Settings.WIDTH*0.2f;
        this.mainCard.target_y = Settings.HEIGHT*0.6f;
        //初始化panel
        initPanel();
    }

    public CardConfigPage()
    {

    }

    //初始化panel 其实就是初始化各种选项
    public void initPanel()
    {
        //添加伤害类的修改框
        CardDesignInputBox tempBox = new CardDesignInputBox(
            Settings.WIDTH*0.2f,Settings.HEIGHT*0.5f,
            Settings.WIDTH*0.3f, Settings.HEIGHT*0.025f,
            "伤害",FontLibrary.getFontWithSize(32),
            this.mainCard,new AttackModify()
        );
        tempBox.height*=3;
        this.optionPanel.addNewPage(tempBox);
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
        this.mainCard.render(sb);
    }

    @Override
    public void update() {
        optionPanel.update();
        this.mainCard.update();
    }
}
