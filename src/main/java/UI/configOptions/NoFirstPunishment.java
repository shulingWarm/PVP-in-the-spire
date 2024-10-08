package UI.configOptions;

import UI.ToggleInterface;
import UI.UserToggle;
import WarlordEmblem.GlobalManager;

//没有先手惩罚的选项
public class NoFirstPunishment implements ToggleInterface {

    @Override
    public void triggerToggleButton(UserToggle toggle, int id, boolean stage) {
        //修改地主总是先手的选项
        GlobalManager.landlordNoPunishment = stage;
    }
}
