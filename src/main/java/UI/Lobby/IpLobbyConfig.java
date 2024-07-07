package UI.Lobby;

import UI.DelayBox;
import WarlordEmblem.AutomaticSocketServer;
import WarlordEmblem.Room.FriendManager;
import WarlordEmblem.actions.ConfigProtocol;
import WarlordEmblem.network.SteamConnector;
import WarlordEmblem.patches.RenderPatch;
import WarlordEmblem.patches.connection.MeunScreenFadeout;
import com.megacrit.cardcrawl.core.CardCrawlGame;

//这是通过ip实现的 config
//正常来说应该lobbyConfig继承这个东西
//但由于开发之前没有设计好，这里就只能让它去继承之前的lobby config了
public class IpLobbyConfig extends LobbyConfig {

    public IpLobbyConfig()
    {
        super(CardCrawlGame.chosenCharacter);
        //网络的状态直接就是已经连接上的那种
        this.networkStage = 1;
        //改成允许使用准备按钮
        this.readyButton.disabled = false;
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
        //初始化友军管理器
        FriendManager.initGlobalManager();
        //这次是真的可以了，准备进入游戏
        RenderPatch.delayBox = new DelayBox();
    }

    @Override
    public void networkUpdate() {
        --sendHelloFrame;
        //调用config阶段的监听工作
        ConfigProtocol.readData(AutomaticSocketServer.getServer());
        //判断是不是到了一个重复发送hello的周期
        if(sendHelloFrame <= 0)
        {
            sendHelloFrame = 100;
            //判断自己是不是还不知道对方的信息
            if(oppositeBox == null)
            {
                AutomaticSocketServer server = AutomaticSocketServer.getServer();
                this.requestOppositeCharacter(server.streamHandle);
                server.send();
            }
        }
    }
}
