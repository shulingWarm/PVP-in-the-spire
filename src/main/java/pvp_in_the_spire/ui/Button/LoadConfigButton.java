package pvp_in_the_spire.ui.Button;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import pvp_in_the_spire.ui.Button.WithUpdate.BaseUpdateButton;
import pvp_in_the_spire.ui.Events.ClickCallback;
import pvp_in_the_spire.ui.Events.ConfigSaveCallback;
import pvp_in_the_spire.ui.Events.LoadConfigCallback;

//专门用于载入
public class LoadConfigButton extends BaseUpdateButton {

    //用于加载配置的回调函数
    public ConfigSaveCallback configCallback;

    public LoadConfigButton(float x, float y,float width,float height,
        String text,
        BitmapFont font,
        ConfigSaveCallback configCallback
    )
    {
        super(x,y,width,height,text,font, ImageMaster.PROFILE_SLOT,null);
        this.configCallback = configCallback;
    }

    //要在这个地方调用专门的加载配置的回调函数
    @Override
    public void clickEvent() {
        this.configCallback.loadConfig(this.text);
    }
}
