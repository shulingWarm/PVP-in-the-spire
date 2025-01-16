package pvp_in_the_spire.ui.CardDesign;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import pvp_in_the_spire.ui.AbstractPage;
import pvp_in_the_spire.ui.PlainBox;

//用于设计卡牌的主界面
public class MainDesignPage extends AbstractPage {

    public static MainDesignPage instance;
    //背景框
    public PlainBox background;

    public static MainDesignPage getInstance()
    {
        if(instance == null)
            instance = new MainDesignPage();
        return instance;
    }

    public MainDesignPage()
    {
        //添加背景板
        background = new PlainBox(Settings.WIDTH*0.5f,
                Settings.HEIGHT*0.9f, Color.valueOf("366D6799"));
    }

    @Override
    public void render(SpriteBatch sb) {
        this.background.render(sb);
    }
}
