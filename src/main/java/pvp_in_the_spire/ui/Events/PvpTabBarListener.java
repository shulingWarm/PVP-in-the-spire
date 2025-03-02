package pvp_in_the_spire.ui.Events;

import com.megacrit.cardcrawl.screens.mainMenu.ColorTabBar;
import pvp_in_the_spire.ui.CardDesign.PvpColorTabBar;

public interface PvpTabBarListener {
    //更改tab时调用的接口
    public void changeShowCardPackage(String packageName);

    //为了兼容游戏原有的tab bar
    public void changeColorTabBar(PvpColorTabBar.CurrentTab newSelection);
}
