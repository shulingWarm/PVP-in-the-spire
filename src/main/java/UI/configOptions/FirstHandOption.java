package UI.configOptions;

import UI.ToggleInterface;
import UI.UserToggle;
import WarlordEmblem.GlobalManager;

//用于设置总是先手的选项
public class FirstHandOption implements ToggleInterface {
    @Override
    public void triggerToggleButton(UserToggle toggle, int id, boolean stage) {
        //设置地主先手的选项
        GlobalManager.landlordFirstHandFlag = stage;
    }
}
