package pvp_in_the_spire.ui.CardDesign;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import pvp_in_the_spire.ui.AbstractPage;
import pvp_in_the_spire.ui.Events.CardDesignClickCallback;
import pvp_in_the_spire.ui.PlainBox;

import java.util.Set;

//用于设计卡牌的主界面
public class MainDesignPage extends AbstractPage
    implements CardDesignClickCallback {

    public static MainDesignPage instance;
    //背景框
    public PlainBox background;
    //卡牌库
    public DesignCardLibrary designCardLibrary;
    //对单卡的配置界面
    public CardConfigPage cardConfigPage;

    //目前正在打开的界面
    public AbstractPage subPage = null;

    public static MainDesignPage getInstance()
    {
        if(instance == null)
            instance = new MainDesignPage();
        return instance;
    }

    public MainDesignPage()
    {
        //添加背景板
        background = new PlainBox(Settings.WIDTH*0.8f,
                Settings.HEIGHT*0.9f, Color.valueOf("366D6799"));
        background.x = Settings.WIDTH*0.1f;
        background.y = Settings.HEIGHT*0.05f;
        //初始化卡牌库显示
        this.designCardLibrary = new DesignCardLibrary();
        //初始化卡牌配置的界面
        this.cardConfigPage = new CardConfigPage();
    }

    @Override
    public void render(SpriteBatch sb) {
        this.background.render(sb);
        if(this.subPage != null)
        {
            this.subPage.render(sb);
        }
        else
            this.designCardLibrary.render(sb);
    }

    @Override
    public void open() {
        this.designCardLibrary.open(this);
    }

    @Override
    public void update() {
        if(this.subPage != null)
            this.subPage.update();
        else
            this.designCardLibrary.update();
    }

    @Override
    public void onCardClicked(AbstractCard card) {
        //打开卡牌配置的界面
        this.subPage = this.cardConfigPage;
        //初始化卡牌
        this.cardConfigPage.initPage(card);
    }
}
