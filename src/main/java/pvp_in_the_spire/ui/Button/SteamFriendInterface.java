package pvp_in_the_spire.ui.Button;

import pvp_in_the_spire.ui.Steam.SteamFriendInfo;

//steam好友的回调接口，当一个按钮被点击后，通知它的回调函数
public interface SteamFriendInterface {

    //发生steam好友的点击事件时的回调函数
    public void friendSelectTrigger(SteamFriendInfo info);

}
