package pvp_in_the_spire.ui.CardDesign;

import com.badlogic.gdx.graphics.Texture;

import java.awt.*;
import java.util.Random;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;

//每个选项卡的名字和颜色信息
//这是为了扩展玩家自定义卡包来显示的
public class TabNameItem {

    public String tabName;
    public Color tabColor;
    public Texture texture;

    public TabNameItem(String tabName, Color tabColor, Texture texture)
    {
        this.tabName = tabName;
        this.tabColor = tabColor;
        this.texture = texture;
    }

    //tab内容的渲染过程
    public void render(SpriteBatch sb, float x, float y, boolean selected)
    {
        sb.draw(this.texture, x - 137.0F, y - 34.0F + 53.0F * Settings.scale, 137.0F, 34.0F, 274.0F, 68.0F, Settings.xScale, Settings.scale, 0.0F, 0, 0, 274, 68, false, false);
        Color c = Settings.GOLD_COLOR;
        if (selected) {
            c = Color.GRAY;
        }

        FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, this.tabName, x, y + 50.0F * Settings.scale, c, 0.9F);
    }

}
