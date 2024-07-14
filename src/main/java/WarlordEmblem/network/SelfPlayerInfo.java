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
}
