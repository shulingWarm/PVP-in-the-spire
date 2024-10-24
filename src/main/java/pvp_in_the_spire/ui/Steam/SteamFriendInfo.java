package pvp_in_the_spire.ui.Steam;

import com.codedisaster.steamworks.SteamID;

//steam好友的信息
public class SteamFriendInfo {

    public SteamID steamID;

    public String friendName;

    public SteamFriendInfo(SteamID id,String name)
    {
        //记录信息
        this.steamID = id;
        this.friendName = name;
    }

}
