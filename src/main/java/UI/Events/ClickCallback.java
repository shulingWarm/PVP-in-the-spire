package UI.Events;

import UI.Button.WithUpdate.BaseUpdateButton;

//按钮点击时的回调函数
public interface ClickCallback {

    //点击事件 回传button是为了方便让调用者知道是哪个按钮被点击了
    public void clickEvent(BaseUpdateButton button);

}
