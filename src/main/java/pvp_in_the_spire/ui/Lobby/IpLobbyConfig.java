package pvp_in_the_spire.ui.Lobby;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import pvp_in_the_spire.ui.AbstractPage;
import pvp_in_the_spire.ui.CardFilter.CardFilterScreen;
import pvp_in_the_spire.ui.ConfigPageModules.MultiplayerConfigPage;
import pvp_in_the_spire.ui.DelayBox;
import pvp_in_the_spire.AutomaticSocketServer;
import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.actions.ConfigProtocol;
import pvp_in_the_spire.actions.FightProtocol;
import pvp_in_the_spire.patches.RenderPatch;
import pvp_in_the_spire.patches.connection.MeunScreenFadeout;

//这是通过ip实现的 config
//正常来说应该lobbyConfig继承这个东西
//但由于开发之前没有设计好，这里就只能让它去继承之前的lobby config了
public class IpLobbyConfig extends MultiplayerConfigPage {

    public AbstractPage testPage;

    public IpLobbyConfig(boolean isOwner)
    {
        super(isOwner);
        this.open();
        //网络的状态直接就是已经连接上的那种
        this.networkStage = 1;
        this.testPage = CardFilterScreen.instance;
        this.testPage.open();
    }

    //获取名字的时候就获得一个固定的名字
    @Override
    public String getMyName() {
        return "user";
    }

    //进入游戏时的逻辑
    @Override
    public void enterGame() {
        //判断是否有mod需要使用
        this.checkGlobalMods();
        MeunScreenFadeout.connectOk = true;
        //准备正常的游戏协议
        GlobalManager.messageTriggerInterface = new FightProtocol();
        //这次是真的可以了，准备进入游戏
        RenderPatch.delayBox = new DelayBox();
    }

    @Override
    public void networkUpdate() {
        --sendHelloFrame;
        //调用config阶段的监听工作
        ConfigProtocol.readData(AutomaticSocketServer.getServer());
    }
}
