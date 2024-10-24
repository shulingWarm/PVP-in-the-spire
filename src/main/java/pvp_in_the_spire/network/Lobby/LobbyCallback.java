package pvp_in_the_spire.network.Lobby;

import pvp_in_the_spire.ui.Events.ConnectOkEvent;
import pvp_in_the_spire.ui.Events.LobbyListCallback;
import pvp_in_the_spire.ui.Events.MemberChangeEvent;
import pvp_in_the_spire.ui.Events.RequestEvent;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamMatchmaking;
import com.codedisaster.steamworks.SteamMatchmakingCallback;
import com.codedisaster.steamworks.SteamResult;

import java.util.ArrayList;

//这是对matchmaking里面的创建房间的实现
public class LobbyCallback implements SteamMatchmakingCallback {

    //等待创建房间成功的回调
    public ConnectOkEvent lobbyCreateCallback = null;
    //请求加入房间时的回调函数，后面也可以考虑把这个改成通用的回调函数
    public RequestEvent requestEvent = null;
    //当接收到lobby列表时的回调函数
    public LobbyListCallback lobbyListCallback = null;
    //房间的成员变化时的回调函数
    public MemberChangeEvent memberChangeEvent = null;
    //当前的房间号
    public SteamID lobbyId;

    @Override
    public void onFavoritesListChanged(int i, int i1, int i2, int i3, int i4, boolean b, int i5) {

    }

    @Override
    public void onLobbyInvite(SteamID steamID, SteamID steamID1, long l) {

    }

    @Override
    public void onLobbyEnter(SteamID steamID, int i, boolean b, SteamMatchmaking.ChatRoomEnterResponse chatRoomEnterResponse) {
        System.out.println("Lobby enter ok");
        //判断是否存在有效的回调函数
        if(this.requestEvent == null)
        {
            return;
        }
        //判断是否成功进入房间
        if(chatRoomEnterResponse == SteamMatchmaking.ChatRoomEnterResponse.Success)
        {
            //调用进入房间成功的回调函数
            this.requestEvent.requestCallback(1);
        }
        else {
            this.requestEvent.requestCallback(0);
        }
    }

    @Override
    public void onLobbyDataUpdate(SteamID steamID, SteamID steamID1, boolean b) {

    }

    @Override
    public void onLobbyChatUpdate(SteamID lobbyId, SteamID enterUserId, SteamID callId, SteamMatchmaking.ChatMemberStateChange chatMemberStateChange) {
        System.out.printf("Update character %s\n",chatMemberStateChange.name());
        //调用人员变化时的回调函数
        if(memberChangeEvent != null)
        {
            memberChangeEvent.onMemberChanged(enterUserId,chatMemberStateChange);
        }
    }

    @Override
    public void onLobbyChatMessage(SteamID steamID, SteamID steamID1, SteamMatchmaking.ChatEntryType chatEntryType, int i) {
        System.out.printf("Receive chat message %d\n",i);
    }

    @Override
    public void onLobbyGameCreated(SteamID steamID, SteamID steamID1, int i, short i1) {

    }

    @Override
    public void onLobbyMatchList(int i) {
        System.out.printf("Receiving lobby %d\n",i);
        //初始化一个pvp房间的列表
        ArrayList<PVPLobby> lobbyList = new ArrayList<>();
        //遍历可以读取到的每个房间
        for(int idLobby = 0;idLobby<i;++idLobby)
        {
            //获取当前房间的id
            SteamID tempId = LobbyManager.matchmaking.getLobbyByIndex(idLobby);
            //新建一个pvp房间添加到列表里
            lobbyList.add(new PVPLobby(tempId));
        }
        //判断是否有回调函数
        if(this.lobbyListCallback != null)
        {
            this.lobbyListCallback.receiveLobbyList(lobbyList);
        }
        else {
            System.out.println("Warning: lobby receive callback is null !!!");
        }
    }

    @Override
    public void onLobbyKicked(SteamID steamID, SteamID steamID1, boolean b) {

    }

    //创建房间成功时的回调
    @Override
    public void onLobbyCreated(SteamResult steamResult, SteamID steamID) {
        //判断是否连接成功
        if(steamResult == SteamResult.OK)
        {
            System.out.println("Create lobby ok");
            this.lobbyId = steamID;
            //判断是否有创建成功时的回调
            if(lobbyCreateCallback != null)
                lobbyCreateCallback.connectOk(true);
        }
        else {
            System.out.println("Create lobby failed");
            this.lobbyId = null;
        }
    }

    @Override
    public void onFavoritesListAccountsUpdated(SteamResult steamResult) {

    }
}
