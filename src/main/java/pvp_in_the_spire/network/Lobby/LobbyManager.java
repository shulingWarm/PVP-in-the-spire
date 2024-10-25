package pvp_in_the_spire.network.Lobby;

import pvp_in_the_spire.ui.Events.LobbyListCallback;
import pvp_in_the_spire.AutomaticSocketServer;
import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.LobbyChatServer;
import pvp_in_the_spire.SteamSocketServer;
import pvp_in_the_spire.patches.PanelScreenPatch;
import pvp_in_the_spire.patches.steamConnect.SteamManager;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamMatchmaking;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.screens.mainMenu.MenuPanelScreen;

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
        //移除所有的通信句柄
        LobbyChatServer.instance.removeAllPlayer();
        //移除player manager里面的内容
        GlobalManager.playerManager.selfLeave();
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

    //获取房间中的所有玩家
    //不包括自身
    public static ArrayList<SteamID> getLobbyPlayers()
    {
        //自身的account
        int selfAccount = SteamManager.getSelfSteamId().getAccountID();
        ArrayList<SteamID> steamIdList = new ArrayList<>();
        int playerNum = matchmaking.getNumLobbyMembers(currentLobby.lobbyId);
        System.out.printf("Lobby member num: %d\n",playerNum);
        for(int idPlayer=0;idPlayer<playerNum;++idPlayer)
        {
            SteamID tempId = matchmaking.getLobbyMemberByIndex(currentLobby.lobbyId,idPlayer);
            if(tempId.getAccountID() != selfAccount)
            {
                steamIdList.add(tempId);
            }
        }
        return steamIdList;
    }

    //成功进入到房间时的情况
    public static void onLobbyEnter(PVPLobby lobby)
    {
        //把已经进入的房间记为当前的房间
        currentLobby = lobby;
        initLobbyChatServer();
        //从lobby中获取所有房间中已有的玩家
        ArrayList<SteamID> steamIdList = getLobbyPlayers();
        System.out.printf("Valid member num %d\n",steamIdList.size());
        for(SteamID eachId : steamIdList)
        {
            LobbyChatServer.instance.registerPlayer(eachId);
        }
    }

    //判断自己当前是不是owner
    public static boolean amIOwner()
    {
        SteamID ownerId = matchmaking.getLobbyOwner(currentLobby.lobbyId);
        return ownerId.getAccountID() == SteamManager.getSelfSteamId().getAccountID();
    }

    //初始化lobby chat server
    public static void initLobbyChatServer()
    {
        AutomaticSocketServer.globalServer = new LobbyChatServer();
    }

    //调用这个函数的时候已经在主界面了，这个时候直接强制回到lobby里面
    public static void backLobby()
    {
        System.out.println("calling back lobby!!");
        //初始化除了网络之外的全局参数
        GlobalManager.initGameGlobal();
        //把当前的页面换成panel,然后还是正常渲染lobby页面
        CardCrawlGame.mainMenuScreen.panelScreen.open(MenuPanelScreen.PanelScreen.PLAY);
        PanelScreenPatch.lobbyFlag = true;
        //重新设置每个Player在config里面的位置
        GlobalManager.playerManager.resetPlayerConfigLocation();
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
