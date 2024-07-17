package WarlordEmblem.network;

import UI.CharacterBox;
import UI.ConfigPageModules.CharacterConfigPage;
import WarlordEmblem.GlobalManager;
import WarlordEmblem.character.CharacterInfo;
import WarlordEmblem.character.PlayerMonster;
import WarlordEmblem.patches.AnimationRecorder;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

//玩家的信息
//以前的这乱七八槽的都是在socketserver里面放着，后面这些东西要改地方了
public class PlayerInfo {

    //这主要是config页面里面玩家的UI
    public CharacterConfigPage configPage = null;

    //玩家的角色信息
    public CharacterInfo characterInfo;

    //当前的玩家保有的monster信息
    public PlayerMonster playerMonster;

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

    //能量上限
    public int maxEnergy;

    //玩家的tag,这是玩家的唯一标志
    public int playerTag;

    //目前所属的team id
    public int idTeam = -1;

    public void initConfigPage()
    {
        //初始化config页面，虽然最开始可能会是空的
        this.configPage = new CharacterConfigPage();
    }

    public PlayerInfo(int playerTag)
    {
        this.playerTag = playerTag;
        initConfigPage();
    }

    //判断是不是本地Player
    public boolean isSelfPlayer()
    {
        return this.playerTag == GlobalManager.myPlayerTag;
    }

    //设置角色信息
    public void setCharacterInfo(String name,
                                 String version,
                                 AbstractPlayer.PlayerClass playerClass
    )
    {
        //用玩家类型初始化角色信息
        this.characterInfo = new CharacterInfo(playerClass);
        this.configPage.setPlayerInfo(characterInfo,name,version);
    }

    //获取name
    public String getName()
    {
        return configPage.getName();
    }

    public String getVersion()
    {
        return configPage.getVersion();
    }

    public void setReadyFlag(boolean readyFlag)
    {
        //指定config里面的ready状态
        configPage.setReady(readyFlag);
    }

    //获得player的class
    public AbstractPlayer.PlayerClass getPlayerClass()
    {
        return this.characterInfo.getPlayerClass();
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

    //将当前player的信息打包成monster
    public AbstractMonster generateMonster(int idMonster)
    {
        //每次调用时都会生成一个新的monster
        this.playerMonster = new PlayerMonster(idMonster == 0,
                (260.f)*idMonster,100*idMonster);
        return playerMonster;
    }

    //获取friend monster
    //目前的过程基本和敌方的monster是一致的，等有需要改变的地方再说
    public PlayerMonster getFriendMonster()
    {
        this.playerMonster = new PlayerMonster(false,-1170,100);
        return playerMonster;
    }

    //把信息载入到monster里面
    public void loadInfoToMonster()
    {
        this.playerMonster.initHealth(
            this.maxHealth,
                this.currentHealth,
                characterInfo,
                this.tailNum,
                this.beginOrbNum
        );
    }

    //重置角色大小
    public void resetPlayerTexture()
    {
        AnimationRecorder.resetCreatureScale(
            this.characterInfo.player,1.f
        );
        CharacterBox.initPlayerAnimation(this.characterInfo.player);
    }

}
