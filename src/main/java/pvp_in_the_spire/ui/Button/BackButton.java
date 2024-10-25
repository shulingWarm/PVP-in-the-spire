package pvp_in_the_spire.ui.Button;

import pvp_in_the_spire.ui.TextureManager;
import pvp_in_the_spire.screens.UserButton;
import pvp_in_the_spire.patches.connection.InputIpBox;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.UIStrings;

//用于表示返回的按钮
public class BackButton extends UserButton {

    public static final UIStrings uiStrings =
            CardCrawlGame.languagePack.getUIString("BackButton");

    public static BitmapFont buttonFont = null;

    //返回按钮需要用到的字体
    public static BitmapFont getFont()
    {
        if(buttonFont==null)
        {
            buttonFont = InputIpBox.generateFont(24);
        }
        return buttonFont;
    }

    public BackButton(BitmapFont font)
    {
        //固定返回按钮的位置
        super(Settings.WIDTH * 0.04f,Settings.HEIGHT * 0.3F,
                Settings.WIDTH * 0.1f, Settings.HEIGHT*0.05f,
                uiStrings.TEXT[0],font);
        //把纹理显示成取消返回的按钮
        this.buttonTexture = TextureManager.BACK_BUTTON;
        //改成直接渲染的模式
        this.useDirectHovering = true;
    }

}
