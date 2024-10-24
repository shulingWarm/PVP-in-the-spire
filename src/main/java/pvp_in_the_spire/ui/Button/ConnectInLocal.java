package pvp_in_the_spire.ui.Button;

import pvp_in_the_spire.screens.UserButton;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;

//通过局域网进行进行连接的按钮
public class ConnectInLocal extends UserButton {

    //界面的文本
    public static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("ConnectInLocal");

    public ConnectInLocal(float x, float y, float width, float height,
      BitmapFont font)
    {
        super(x,y,width,height,uiStrings.TEXT[0],font);
    }

}
