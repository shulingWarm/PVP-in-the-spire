package pvp_in_the_spire.player_management;

import pvp_in_the_spire.ui.AbstractPage;
import pvp_in_the_spire.network.PlayerInfo;

//有新的玩家加入的接口
public interface PlayerJoinInterface {

    public void registerPlayer(PlayerInfo player);

    //更新主显示位显示的内容，这是用来更新角色的
    public void setMainCharacter(AbstractPage page);

    //准备进入游戏
    public void enterGame();
}
