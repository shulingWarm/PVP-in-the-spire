package pvp_in_the_spire.screens;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;

public class FakeSettingScreen {

    Color tempColor;

    String strForShow;

    int idRender=1;

    public FakeSettingScreen()
    {
        tempColor = new Color(1.0F, 0.965F, 0.886F, 0.6F);
        strForShow = new String("waiting for the enemy");
    }

    //渲染这个背景
    public void render(SpriteBatch sb)
    {
        if(idRender == 100)
        {
            idRender = 1;
            strForShow = new String("waiting for the enemy");
        }
        if(idRender % 25 ==0)
        {
            strForShow += " .";
        }
        idRender++;
        sb.setColor(tempColor);
        sb.draw(ImageMaster.OPTION_CONFIRM, (float)Settings.WIDTH / 2.0F - 310.0F, Settings.OPTION_Y - 207.0F, 180.0F, 207.0F, 660.0F, 414.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 360, 414, false, false);
        FontHelper.energyNumFontRed.draw(sb,strForShow,(float)Settings.WIDTH / 2.0F - 220.0F, Settings.OPTION_Y);
    }

}

