package UI.Events;

import WarlordEmblem.network.Lobby.PVPLobby;

//房间按钮被点击时的事件
public interface LobbyButtonCallback {

    public void onLobbyButtonClicked(PVPLobby lobby);

}
