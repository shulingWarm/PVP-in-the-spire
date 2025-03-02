package pvp_in_the_spire.ui.CardDesign;

import com.badlogic.gdx.graphics.Color;
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

}
