package WarlordEmblem.network;

import UI.ConfigPageModules.SelfPlayerPage;
import WarlordEmblem.GlobalManager;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

//这是用来管理本地的PlayerInfo的
public class SelfPlayerInfo extends PlayerInfo {

    //config页面改为使用玩家独立的渲染页面
    @Override
    public void initConfigPage() {
        this.configPage = new SelfPlayerPage();
    }

    public SelfPlayerInfo()
    {
        super(GlobalManager.myPlayerTag);
        //设置玩家的信息
        setCharacterInfo(GlobalManager.myName,GlobalManager.VERSION,
                GlobalManager.defaultClass);
    }

    //初始化进入游戏的时间
    public void initEnterTime()
    {
        this.enterTime = System.currentTimeMillis() - GlobalManager.playerManager.beginGameTime;
        //更新player的进入时间
        GlobalManager.playerManager.updateEnterTime(this,this.enterTime);
    }

    //本地玩家不需要执行这个操作
    @Override
    public void loadInfoToMonster() {

    }

    @Override
    public void resetPlayerTexture() {

    }
}
