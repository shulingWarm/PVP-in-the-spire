package pvp_in_the_spire.ui.CardDesign;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pvp_in_the_spire.ui.AbstractPage;

// power的图标
public class PowerIcon extends AbstractPage {

    public Texture img = null;
    public TextureAtlas.AtlasRegion region48 = null;

    public PowerIcon(AbstractPower power, float x, float y)
    {
        //如果里面有图标信息的话，优先使用图片信息
        if(power.img != null)
        {
            this.img = power.img;
        }
        else
        {
            this.region48 = power.region48;
        }
        this.x = x;
        this.y = y;
        //初始化宽度和高度，虽然这个宽高可能整体上不重要
        this.width = Settings.WIDTH*0.07f;
        this.height = this.width;
    }

    @Override
    public void render(SpriteBatch sb) {
        if(this.img != null)
        {
            sb.draw(this.img, x - 12.0F, y - 12.0F, 16.0F, 16.0F, 32.0F, 32.0F, Settings.scale * 1.5F, Settings.scale * 1.5F, 0.0F, 0, 0, 32, 32, false, false);
        }
        else if(this.region48 != null)
        {
            sb.draw(this.region48, x - (float)this.region48.packedWidth / 2.0F, y - (float)this.region48.packedHeight / 2.0F, (float)this.region48.packedWidth / 2.0F, (float)this.region48.packedHeight / 2.0F, (float)this.region48.packedWidth, (float)this.region48.packedHeight, Settings.scale, Settings.scale, 0.0F);
        }
    }
}
