package pvp_in_the_spire.ui.Events;

import pvp_in_the_spire.network.Lobby.PVPLobby;

//房间按钮被点击时的事件
public interface LobbyButtonCallback {

    public void onLobbyButtonClicked(PVPLobby lobby);

}
