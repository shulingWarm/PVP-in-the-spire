package pvp_in_the_spire.ui.configOptions;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.ui.ToggleInterface;
import pvp_in_the_spire.ui.UserToggle;

public class LoserCardOption implements ToggleInterface {
    @Override
    public void triggerToggleButton(UserToggle toggle, int id, boolean stage) {
        GlobalManager.loserCardFlag = stage;
    }
}
