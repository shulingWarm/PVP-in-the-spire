package pvp_in_the_spire.ui.configOptions;

import pvp_in_the_spire.ui.ToggleInterface;
import pvp_in_the_spire.ui.UserToggle;
import pvp_in_the_spire.GlobalManager;

//地主多获得一条尾巴
public class LandlordMoreTail implements ToggleInterface {

    @Override
    public void triggerToggleButton(UserToggle toggle, int id, boolean stage) {
        //地主多获得尾巴
        GlobalManager.landlordMoreTail = stage;
    }
}
