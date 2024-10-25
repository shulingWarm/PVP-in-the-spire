package pvp_in_the_spire.ui.Events;

import pvp_in_the_spire.network.Lobby.PVPLobby;

import java.util.ArrayList;

//接收steam房间列表成功时的回调函数
public interface LobbyListCallback {

    public void receiveLobbyList(ArrayList<PVPLobby> lobbyList);

}
