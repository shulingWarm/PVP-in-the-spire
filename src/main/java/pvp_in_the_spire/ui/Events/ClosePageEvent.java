package pvp_in_the_spire.ui.Events;

import pvp_in_the_spire.ui.AbstractPage;

//关闭一个页面时的事件
public interface ClosePageEvent {

    //关闭页面时的反馈事件
    public void closePageEvent(AbstractPage page);

}
