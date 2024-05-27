package WarlordEmblem.network.Lobby;

import UI.Events.LobbyListCallback;
import WarlordEmblem.AutomaticSocketServer;
import WarlordEmblem.SteamSocketServer;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamMatchmaking;

import java.util.ArrayList;

//steam创建房间相关的管理器
public class LobbyManager {

    //steam原生的创建房间的管理器
    public static SteamMatchmaking matchmaking = null;
    //与创建房间相关的回调函数
    public static LobbyCallback callback = null;
    //当前所在的房间
    public static PVPLobby currentLobby = null;

    //lobby的各种属性
    public static final String NAME = "name";
    public static final String PASSWORD = "password";
    public static final String MOD_TAG = "PVP_IN_SPIRE";

    //初始化房间管理器
    public static void initManager()
    {
        //判断是否已经初始化过
        if(callback != null)
            return;
        callback = new LobbyCallback();
        //初始化matchmaking的接口
        matchmaking = new SteamMatchmaking(callback);
    }

    //设置房间的属性
    public static void initLobbyProperty(
            SteamID lobbyId,
            String lobbyName,
            String lobbyPassword
    )
    {
        //设置基本的房间属性 这是在茫茫steam里面找房间用的
        matchmaking.setLobbyData(lobbyId,MOD_TAG,MOD_TAG);
        //设置房间的名称
        matchmaking.setLobbyData(lobbyId,NAME,lobbyName);
        matchmaking.setLobbyData(lobbyId,PASSWORD,lobbyPassword);
    }

    //离开当前的房间
    public static void leaveRoom()
    {
        matchmaking.leaveLobby(currentLobby.lobbyId);
    }

    //令当前的房间消失
    public static void destroyRoom(SteamID lobbyId)
    {
        //把房间标记成废弃
        matchmaking.setLobbyData(lobbyId,MOD_TAG,"invalid");
    }

    //初始化目前可以获取到的所有房间
    public static void refreshAccessibleLobby(LobbyListCallback lobbyListCallback)
    {
        //指定接收到房间列表时的回调函数
        callback.lobbyListCallback = lobbyListCallback;
        //添加找房间时的条件限定
        matchmaking.addRequestLobbyListStringFilter(MOD_TAG,MOD_TAG,SteamMatchmaking.LobbyComparison.Equal);
        //指定在国际范围内搜索
        matchmaking.addRequestLobbyListDistanceFilter(SteamMatchmaking.LobbyDistanceFilter.Worldwide);
        //添加搜索指令
        matchmaking.requestLobbyList();
    }

    //成功进入到房间时的情况
    public static void onLobbyEnter(PVPLobby lobby)
    {
        //把已经进入的房间记为当前的房间
        currentLobby = lobby;
        //初始化p2p连接
        initP2PConnection();
    }

    //初始化全局的p2p连接
    public static void initP2PConnection(SteamID id)
    {
        //初始化全局的server
        AutomaticSocketServer.globalServer = new SteamSocketServer(id);
    }

    public static void initP2PConnection()
    {
        //直接获取第一个位置的steam id
        SteamID oppositeId = matchmaking.getLobbyMemberByIndex(currentLobby.lobbyId,
                0);
        initP2PConnection(oppositeId);
    }


}
