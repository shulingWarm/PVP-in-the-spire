package pvp_in_the_spire.ui.Events;

import pvp_in_the_spire.ui.Text.AdvTextManager;

//输入框里按下回车时的接口
public interface EnterInterface {

    public void enterPressed(String message);

    public void enterPressed(AdvTextManager message);

}
