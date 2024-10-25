package pvp_in_the_spire.network;

import pvp_in_the_spire.ui.ConfigPageModules.SelfPlayerPage;
import pvp_in_the_spire.events.UpdateCharacterEvent;
import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.Communication;
import pvp_in_the_spire.player_management.CharacterManager;
import pvp_in_the_spire.PvPInTheSpireMod;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

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
        setCharacterInfo(GlobalManager.myName,String.valueOf(PvPInTheSpireMod.info.ModVersion),
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

    //对于自身的玩家什么都不需要做
    @Override
    public void resetPlayerLocation() {
        if(this.configPage != null)
        {
            this.configPage.resetReadyStage();
            //同时还要设置一下房主的状态
            this.configPage.setOwnerUI(this.isLobbyOwner);
        }
    }

    @Override
    public AbstractCreature getCreature() {
        return AbstractDungeon.player;
    }

    @Override
    public void setAsDead() {
        GlobalManager.getBattleInfo().selfDeadFlag = true;
    }
}
