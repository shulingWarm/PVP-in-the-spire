package pvp_in_the_spire.ui.Button.WithUpdate;

import pvp_in_the_spire.ui.Events.LobbyButtonCallback;
import pvp_in_the_spire.helpers.FontLibrary;
import pvp_in_the_spire.network.Lobby.PVPLobby;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.ImageMaster;

//用于进入房间的按钮
public class LobbyButton extends BaseUpdateButton {

    //当前按钮中保存的房间
    public PVPLobby lobby;

    //被点击时的回调函数，改为回调房间事件
    public LobbyButtonCallback lobbyButtonCallback;

    public boolean passwordFlag;

    public LobbyButton(float x, float y,
                       float width,float height,
                       PVPLobby lobby,
                       LobbyButtonCallback lobbyButtonCallback
    )
    {
        super(x,y,width,height,lobby.getLobbyName(),
                FontLibrary.getBaseFont(), ImageMaster.PROFILE_SLOT,null);
        //记录当前按钮中保存的房间
        this.lobby = lobby;
        //记录房间按钮特有的回调函数
        this.lobbyButtonCallback = lobbyButtonCallback;
        //判断是否需要密码
        this.passwordFlag = !lobby.getPassword().isEmpty();
    }

    @Override
    public void clickEvent() {
        //回调房间被点击的事件
        this.lobbyButtonCallback.onLobbyButtonClicked(this.lobby);
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        //判断是否需要密码
        if(this.passwordFlag)
        {
            sb.draw(ImageMaster.P_LOCK,this.width * 0.8f + this.x,
                    this.y,this.height,this.height);
        }
    }
}
