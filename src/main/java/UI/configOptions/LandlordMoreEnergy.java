package UI.configOptions;

import UI.ToggleInterface;
import UI.UserToggle;
import WarlordEmblem.GlobalManager;

//令地主多获得一费
public class LandlordMoreEnergy implements ToggleInterface {

    @Override
    public void triggerToggleButton(UserToggle toggle, int id, boolean stage) {
        //令地主开局多获得一费
        GlobalManager.landlordEnergyFlag = stage;
    }
}
