package UI.Lobby;

import UI.ConfigPageModules.MultiplayerConfigPage;
import UI.DelayBox;
import WarlordEmblem.AutomaticSocketServer;
import WarlordEmblem.GlobalManager;
import WarlordEmblem.Room.FriendManager;
import WarlordEmblem.actions.ConfigProtocol;
import WarlordEmblem.actions.FightProtocol;
import WarlordEmblem.network.SteamConnector;
import WarlordEmblem.patches.RenderPatch;
import WarlordEmblem.patches.connection.MeunScreenFadeout;
import com.megacrit.cardcrawl.core.CardCrawlGame;

//这是通过ip实现的 config
//正常来说应该lobbyConfig继承这个东西
//但由于开发之前没有设计好，这里就只能让它去继承之前的lobby config了
public class IpLobbyConfig extends MultiplayerConfigPage {

    public IpLobbyConfig(boolean isOwner)
    {
        super(isOwner);
        this.open();
        //网络的状态直接就是已经连接上的那种
        this.networkStage = 1;
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
        //RenderPatch.delayBox = new DelayBox();
    }

    @Override
    public void networkUpdate() {
        --sendHelloFrame;
        //调用config阶段的监听工作
        ConfigProtocol.readData(AutomaticSocketServer.getServer());
    }
}
