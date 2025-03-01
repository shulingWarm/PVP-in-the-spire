package pvp_in_the_spire.ui.CardDesign;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import pvp_in_the_spire.card.AdaptableCard;
import pvp_in_the_spire.card.CardDesign.AdaptableCardManager;
import pvp_in_the_spire.helpers.FontLibrary;
import pvp_in_the_spire.screens.WarningText;
import pvp_in_the_spire.ui.AbstractPage;
import pvp_in_the_spire.ui.BasePanel;
import pvp_in_the_spire.ui.Button.WithUpdate.BaseUpdateButton;
import pvp_in_the_spire.ui.CardDesign.CardModifyItem.AttackModify;
import pvp_in_the_spire.ui.Events.ClickCallback;
import pvp_in_the_spire.ui.InputBoxWithLabel;

//对单独某一个卡牌的配置界面
public class CardConfigPage extends AbstractPage implements ClickCallback {

    //显示主卡牌的界面
    public AdaptableCard mainCard;
    //用于显示配置选项的panel
    public BasePanel optionPanel;
    //用于保存的按钮，这会覆盖原来的卡牌
    public BaseUpdateButton saveButton;
    //用于另存的卡牌
    public BaseUpdateButton saveOtherButton;
    //用于关闭当前页面的按钮
    public BaseUpdateButton cancelButton;
    //另存为相关的tip
    public WarningText warningText;
    //三个用于操作的按钮所在的位置
    public final float BUTTON_X = Settings.WIDTH*0.14f;
    //三个按钮的宽度
    public final float BUTTON_WIDTH = Settings.WIDTH*0.12f;
    //按钮共用的宽度信息
    public final float BUTTON_HEIGHT = Settings.HEIGHT*0.06f;
    //按钮上面的起始位置
    public final float BUTTON_Y_BEGIN = Settings.HEIGHT*0.55f;
    //每个按钮在纵向上的间距
    public final float BUTTON_Y_GAP = Settings.HEIGHT*0.1f;

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
        this.mainCard.current_y = Settings.HEIGHT*0.75f;
        this.mainCard.target_x = Settings.WIDTH*0.2f;
        this.mainCard.target_y = Settings.HEIGHT*0.75f;
        //初始化用于保存的按钮
        this.saveButton = new BaseUpdateButton(
            BUTTON_X,BUTTON_Y_BEGIN, BUTTON_WIDTH,BUTTON_HEIGHT,
            "保存", FontLibrary.getBaseFont(), ImageMaster.PROFILE_SLOT,
            this
        );
        //用于另存为新卡牌的按钮
        this.saveOtherButton = new BaseUpdateButton(
            BUTTON_X,BUTTON_Y_BEGIN - BUTTON_Y_GAP,BUTTON_WIDTH, BUTTON_HEIGHT,
            "另存为新牌", FontLibrary.getBaseFont(), ImageMaster.PROFILE_SLOT, this
        );
        //用于关闭页面的按钮
        this.cancelButton = new BaseUpdateButton(
                BUTTON_X,BUTTON_Y_BEGIN - 2*BUTTON_Y_GAP,BUTTON_WIDTH, BUTTON_HEIGHT,
                "关闭", FontLibrary.getBaseFont(), ImageMaster.PROFILE_SLOT, this
        );
        //初始化警告信息
        this.warningText = new WarningText(
                "另存为成功",
                FontLibrary.getFontWithSize(33),
                Settings.WIDTH * 0.5f,
                Settings.HEIGHT * 0.4f,
                Color.YELLOW
        );
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
        this.saveButton.render(sb);
        this.saveOtherButton.render(sb);
        this.cancelButton.render(sb);
        this.warningText.render(sb);
    }

    @Override
    public void update() {
        optionPanel.update();
        this.mainCard.update();
        this.saveButton.update();
        this.saveOtherButton.update();
        this.cancelButton.update();
    }

    @Override
    public void clickEvent(BaseUpdateButton button) {
        if(button == this.saveOtherButton)
        {
            //另存为新牌的按钮情况
            AdaptableCardManager.getInstance().addNewCard(this.mainCard);
            //显示另存成功的操作
            this.warningText.idFrame = 0;
        }
    }
}
