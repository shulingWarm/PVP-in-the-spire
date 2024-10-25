package pvp_in_the_spire.ui;


import pvp_in_the_spire.PvPInTheSpireMod;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;

//用于显示版本号的文本
public class VersionText extends TextLabel {

    public VersionText(float x, float y, BitmapFont font)
    {
        super(x,y, Settings.WIDTH*0.04f,Settings.HEIGHT*0.07f, String.valueOf(PvPInTheSpireMod.info.ModVersion),font);
    }

    @Override
    public void render(SpriteBatch sb) {
        //只有当有内容的时候才渲染
        if(this.text != null)
            super.render(sb);
    }
}
