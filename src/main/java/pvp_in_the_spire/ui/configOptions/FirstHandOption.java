package pvp_in_the_spire.ui.configOptions;

import pvp_in_the_spire.ui.ToggleInterface;
import pvp_in_the_spire.ui.UserToggle;
import pvp_in_the_spire.GlobalManager;

//用于设置总是先手的选项
public class FirstHandOption implements ToggleInterface {
    @Override
    public void triggerToggleButton(UserToggle toggle, int id, boolean stage) {
        //设置地主先手的选项
        GlobalManager.landlordFirstHandFlag = stage;
    }
}
