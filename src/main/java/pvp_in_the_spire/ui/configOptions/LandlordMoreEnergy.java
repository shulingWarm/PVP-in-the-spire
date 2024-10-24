package pvp_in_the_spire.ui.configOptions;

import pvp_in_the_spire.ui.ToggleInterface;
import pvp_in_the_spire.ui.UserToggle;
import pvp_in_the_spire.GlobalManager;

//令地主多获得一费
public class LandlordMoreEnergy implements ToggleInterface {

    @Override
    public void triggerToggleButton(UserToggle toggle, int id, boolean stage) {
        //令地主开局多获得一费
        GlobalManager.landlordEnergyFlag = stage;
    }
}
