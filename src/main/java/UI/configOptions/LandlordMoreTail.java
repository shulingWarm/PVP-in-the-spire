package UI.configOptions;

import UI.ToggleInterface;
import UI.UserToggle;
import WarlordEmblem.GlobalManager;

//地主多获得一条尾巴
public class LandlordMoreTail implements ToggleInterface {

    @Override
    public void triggerToggleButton(UserToggle toggle, int id, boolean stage) {
        //地主多获得尾巴
        GlobalManager.landlordMoreTail = stage;
    }
}
