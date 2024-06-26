package UI.Events;

import UI.Text.AdvTextManager;

//输入框里按下回车时的接口
public interface EnterInterface {

    public void enterPressed(String message);

    public void enterPressed(AdvTextManager message);

}
