package WarlordEmblem.network;

import UI.ConfigPageModules.SelfPlayerPage;
import WarlordEmblem.Events.UpdateCharacterEvent;
import WarlordEmblem.GlobalManager;
import WarlordEmblem.PVPApi.Communication;
import WarlordEmblem.PlayerManagement.CharacterManager;
import WarlordEmblem.character.CharacterInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

//这是用来管理本地的PlayerInfo的
public class SelfPlayerInfo extends PlayerInfo {

    //当前选择的class id
    public int idClass = 0;
    //自身的self player page
    public SelfPlayerPage selfPlayerPage = null;

    //config页面改为使用玩家独立的渲染页面
    @Override
    public void initConfigPage() {
        this.selfPlayerPage = new SelfPlayerPage();
        this.configPage = this.selfPlayerPage;
    }

    public SelfPlayerInfo()
    {
        super(GlobalManager.myPlayerTag);
        initConfigPage();
        //设置当前选择的class id
        CharacterManager.initArrayList();
        this.idClass = CharacterManager.getClassId(GlobalManager.defaultClass);
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

    //更新选择的角色
    public void changeCharacter(int changeDir)
    {
        this.idClass = CharacterManager.adjustId(idClass + changeDir);
        AbstractPlayer.PlayerClass tempClass = CharacterManager.getClassById(this.idClass);
        //发送更新角色的事件
        Communication.sendEvent(new UpdateCharacterEvent(tempClass));
        this.updateCharacter(tempClass);
    }

    //本地玩家不需要执行这个操作
    @Override
    public void loadInfoToMonster() {

    }

    @Override
    public void resetPlayerTexture() {

    }

    //不管什么时候都按照dead来算
    @Override
    public boolean isDead() {
        return GlobalManager.getBattleInfo().selfDeadFlag;
    }
}
