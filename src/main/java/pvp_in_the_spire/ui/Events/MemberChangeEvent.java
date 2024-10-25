package pvp_in_the_spire.ui.Events;

import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamMatchmaking;

//房间内成员变化对应的事件
public interface MemberChangeEvent {

    public void onMemberChanged(SteamID personId, SteamMatchmaking.ChatMemberStateChange memberStage);

}
