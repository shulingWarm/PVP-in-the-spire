package pvp_in_the_spire.ui.CardDesign;

import com.badlogic.gdx.graphics.Texture;

import java.awt.*;
import java.util.Random;
import com.badlogic.gdx.graphics.Color;

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

}
