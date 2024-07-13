package WarlordEmblem.network;

import UI.ConfigPageModules.CharacterConfigPage;
import WarlordEmblem.character.CharacterInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

//玩家的信息
//以前的这乱七八槽的都是在socketserver里面放着，后面这些东西要改地方了
public class PlayerInfo {

    //这主要是config页面里面玩家的UI
    public CharacterConfigPage configPage = null;

    //玩家的角色信息
    public CharacterInfo characterInfo;

    //对方的最大生命值，这是用来初始化敌人生命的
    public int maxHealth;
    public int currentHealth;

    //剩余尾巴个数
    public int tailNum;

    //靴子的数量，这是用于判定是否把伤害增加到5用的
    public int bootNum;

    //是否有外卡钳
    public int hasCaliper;

    //瓶中精灵的数量
    public int fairyPotionNum;

    //目前手里的钱数
    public int goldNum;

    //初始球位
    public int beginOrbNum;

    //进入房间的时间，用于判断谁是先手
    public long enterTime;

    //用于判断对方是否已经准备好了
    public boolean isReady;

    //玩家的名字
    public String name;

    //能量上限
    public int maxEnergy;

    //构造的时候必须传入configPage,这个东西需要确保是已经分配好位置的
    public PlayerInfo(CharacterConfigPage configPage)
    {
        this.configPage = configPage;
    }

    //更新角色信息
    public void updateCharacter(AbstractPlayer.PlayerClass playerClass)
    {
        //判断是否需要更新
        if(characterInfo!= null && characterInfo.getPlayerClass() != playerClass)
        {
            characterInfo = new CharacterInfo(playerClass);
            //更新config页面里面的角色数据
            this.configPage.updateCharacter(this.characterInfo);
        }
    }


}
