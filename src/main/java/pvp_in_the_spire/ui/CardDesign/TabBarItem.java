package pvp_in_the_spire.ui.CardDesign;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.Hitbox;

//tab bar的信息
public class TabBarItem {

    //对应的hit box
    public Hitbox refHitBox;
    //当前渲染的文本、颜色和背景图片
    public TabNameItem tabContent = null;

    public TabBarItem(Hitbox refHitBox)
    {
        this.refHitBox = refHitBox;
    }

    //注册tab name item
    public void registerTabItem(TabNameItem tabNameItem)
    {
        this.tabContent = tabNameItem;
    }

    //渲染过程
    //主要是需要利用tab里面的x坐标
    public void render(SpriteBatch sb, float y, boolean selected)
    {
        if(this.tabContent != null)
        {
            this.tabContent.render(sb,this.refHitBox.cX, y, selected);
        }
    }

}
