package pvp_in_the_spire.ui.Button;

import pvp_in_the_spire.screens.UserButton;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;

//通过steam的连接按钮
public class ConnectInSteam extends UserButton {

    public static final UIStrings uiStrings =
        CardCrawlGame.languagePack.getUIString("ConnectInSteam");

    public ConnectInSteam(float x, float y, float width, float height,
      BitmapFont font)
    {
        super(x,y,width,height,uiStrings.TEXT[0],font);
    }

}
