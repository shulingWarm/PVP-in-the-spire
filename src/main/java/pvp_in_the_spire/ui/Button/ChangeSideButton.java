package pvp_in_the_spire.ui.Button;

import pvp_in_the_spire.screens.UserButton;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;

//更换阵营的按钮
public class ChangeSideButton extends UserButton {

    public static final UIStrings uiStrings =
            CardCrawlGame.languagePack.getUIString("ChangeSideButton");

    public ChangeSideCallback changeSideCallback;

    public ChangeSideButton(float x, float y, float width, float height,
                            BitmapFont font,ChangeSideCallback changeSideCallback
                            )
    {
        super(x,y,width,height,uiStrings.TEXT[0],font);
        this.changeSideCallback = changeSideCallback;
    }

    @Override
    public void clickEvent() {
        //回调换边操作
        this.changeSideCallback.changeSideTrigger();
    }
}
