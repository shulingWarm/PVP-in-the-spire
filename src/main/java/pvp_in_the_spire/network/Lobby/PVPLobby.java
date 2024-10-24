package pvp_in_the_spire.network.Lobby;

import com.codedisaster.steamworks.SteamID;

//用于存储房间里面的各种信息
public class PVPLobby {

    private String name;
    private String password;
    public SteamID lobbyId;

    //这种构造方式属于是各种属性都还没有被初始化
    //等需要的时候再初始化
    public PVPLobby(SteamID lobbyId)
    {
        this.lobbyId = lobbyId;
        this.name = null;
        this.password = null;
    }

    public String getPassword()
    {
        //判断密码是不是null
        if(password == null)
        {
            this.password = LobbyManager.matchmaking.getLobbyData(
                this.lobbyId,LobbyManager.PASSWORD
            );
        }
        return this.password;
    }

    //获取房间名
    public String getLobbyName()
    {
        if(this.name == null)
        {
            this.name = LobbyManager.matchmaking.getLobbyData(
                    this.lobbyId,LobbyManager.NAME
            );
        }
        return this.name;
    }


    //构造的时候需要指定当前的密码和房间名
    public PVPLobby(
            String name,
            String password,
            SteamID lobbyId //这个是用来进行steam连接的关键
    )
    {
        this.name = name;
        this.password = password;
        this.lobbyId = lobbyId;
    }

}
