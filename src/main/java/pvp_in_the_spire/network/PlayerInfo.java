package pvp_in_the_spire.network;

import pvp_in_the_spire.ui.CharacterBox;
import pvp_in_the_spire.ui.ConfigPageModules.CharacterConfigPage;
import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.player_management.PlayerCardManager;
import pvp_in_the_spire.character.CharacterInfo;
import pvp_in_the_spire.character.PlayerMonster;
import pvp_in_the_spire.patches.AnimationRecorder;
import pvp_in_the_spire.powers.PowerManager;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.ArrayList;

//玩家的信息
//以前的这乱七八槽的都是在socketserver里面放着，后面这些东西要改地方了
public class PlayerInfo {

    //这主要是config页面里面玩家的UI
    public CharacterConfigPage configPage = null;

    //玩家的角色信息
    public CharacterInfo characterInfo;

    //当前的玩家保有的monster信息
    public PlayerMonster playerMonster;

    //卡牌信息管理器
    public PlayerCardManager cardManager;

    //玩家的power信息管理器 这是用来管理当前玩家的buff的
    //这些buff不一定是在这个玩家身上，但这个buff是由当前玩家发起的
    public PowerManager powerManager;

    //玩家掌管的遗物列表
    public ArrayList<AbstractRelic> relicList;

    //当前玩家的药水
    public ArrayList<AbstractPotion> potionList;

    //是否为房主
    public boolean isLobbyOwner = false;

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

    //能量上限
    public int maxEnergy;

    //玩家的tag,这是玩家的唯一标志
    public int playerTag;

    //目前所属的team id
    public int idTeam = -1;

    //玩家所在的座次
    public int idSeat = -1;

    public void initConfigPage()
    {
        //初始化config页面，虽然最开始可能会是空的
        this.configPage = new CharacterConfigPage();
    }

    public PlayerInfo(int playerTag)
    {
        this.playerTag = playerTag;
        initConfigPage();
        this.cardManager = new PlayerCardManager();
        //初始化power的管理器
        this.powerManager = new PowerManager(playerTag);
        //初始化玩家的药水列表
        this.potionList = new ArrayList<>();
        //准备遗物列表
        this.relicList = new ArrayList<>();
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
        System.out.printf("Set player name %s\n",name);
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

    public boolean getReadyFlag()
    {
        return configPage.getReadyFlag();
    }

    //获得player的class
    public AbstractPlayer.PlayerClass getPlayerClass()
    {
        return this.characterInfo.getPlayerClass();
    }


    //更新角色信息
    //正常情况下，都需要检查新的角色类别是否和之前相同，防止频繁更新
    public void updateCharacter(AbstractPlayer.PlayerClass playerClass,boolean checkEqual)
    {
        //判断是否需要更新
        if(characterInfo!= null && ((!checkEqual) || characterInfo.getPlayerClass() != playerClass))
        {
            characterInfo = new CharacterInfo(playerClass);
            //更新config页面里面的角色数据
            this.configPage.updateCharacter(this.characterInfo);
        }
    }

    public void updateCharacter(AbstractPlayer.PlayerClass playerClass)
    {
        this.updateCharacter(playerClass,true);
    }


    //将当前player的信息打包成monster
    public AbstractMonster generateMonster(int idMonster)
    {
        //每次调用时都会生成一个新的monster
        this.playerMonster = new PlayerMonster(this.getName(),true,
                (260.f)*idMonster,100*idMonster,playerTag,false,cardManager);
        return playerMonster;
    }

    //获取friend monster
    //目前的过程基本和敌方的monster是一致的，等有需要改变的地方再说
    public PlayerMonster getFriendMonster()
    {
        this.playerMonster = new PlayerMonster(this.getName(),false,-1170,100,playerTag,
                true,cardManager);
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
                this.beginOrbNum,
                this.maxEnergy,
                this.relicList,
                this.hasCaliper > 0
        );
        //更新monster的药水
        this.playerMonster.updatePotionList(this.potionList);
    }

    //重置角色大小
    public void resetPlayerTexture()
    {
        AnimationRecorder.resetCreatureScale(
            this.characterInfo.player,1.f
        );
        CharacterBox.initPlayerAnimation(this.characterInfo.player,this.characterInfo);
    }

    //判断是不是死亡状态
    public boolean isDead()
    {
        return playerMonster.isDead;
    }

    //判断是不是回合结束了
    public boolean isEndTurn()
    {
        if(this.playerMonster == null)
            return false;
        return this.playerMonster.isEndTurn();
    }

    //设置是否为房主
    public void setLobbyOwner(boolean ownerFlag)
    {
        this.isLobbyOwner = ownerFlag;
        //设置config页面里面的房主信息
        this.configPage.setOwnerUI(ownerFlag);
    }

    //重置player贴图的位置
    //这包括了重置准备按钮
    public void resetPlayerLocation()
    {
        //重置角色贴图的位置
        this.updateCharacter(this.getPlayerClass(),false);
        if(this.configPage != null)
        {
            this.configPage.resetReadyStage();
            //同时还要设置一下房主的状态
            this.configPage.setOwnerUI(this.isLobbyOwner);
        }
    }

    //更新药水列表
    public void updatePotionList()
    {
        //判断是否有monster
        if(playerMonster != null)
        {
            playerMonster.updatePotionList(this.potionList);
        }
    }

    //获取当前玩家所在的座次
    public int getIdSeat() {
        return this.idSeat;
    }

    //设置当前玩家的座位
    public void setIdSeat(int idSeat) {
        this.idSeat = idSeat;
    }

    //获取这个玩家当前的轮次
    public int getIdTurn()
    {
        if(this.playerMonster == null)
            return -1;
        return this.playerMonster.getIdTurn();
    }

    //获得info里面对应的玩家
    public AbstractCreature getCreature()
    {
        return this.playerMonster;
    }

    public void setAsDead()
    {
        if(this.playerMonster != null)
            this.playerMonster.isDead = true;
    }
}
