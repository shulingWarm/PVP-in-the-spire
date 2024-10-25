package pvp_in_the_spire.ui.Events;

//各种通用的请求事件的接口
//例如请求进入房间，请求创建房间之类的操作
public interface RequestEvent {

    //顺便接收一些参数信息
    public void requestCallback(int indexFlag);

}
