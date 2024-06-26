package WarlordEmblem;

import UI.ConfigPage;
import UI.InputBox;
import UI.TextureManager;
import WarlordEmblem.Dungeon.FakeEnding;
import WarlordEmblem.EffectTransport.EffectManager;
import WarlordEmblem.PVPApi.BaseEvent;
import WarlordEmblem.Screens.midExit.MidExitScreen;
import WarlordEmblem.network.SteamConnector;
import WarlordEmblem.patches.*;
import WarlordEmblem.patches.CardShowPatch.UseCardSend;
import WarlordEmblem.patches.connection.MeunScreenFadeout;
import WarlordEmblem.patches.steamConnect.SteamManager;
import WarlordEmblem.relics.BlockGainer;
import basemod.DevConsole;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

//全局的静态变量管理器，每次调用的时候需要把所有的静态变量初始化一次
public class GlobalManager {

    //用来记录当前是第几场游戏
    public static int idGame = 0;
    //初始化的尾巴数量
    public static int beginTailNum = 2;
    //初始的坚不可摧比例
    public static int invincibleRate = 2;
    //版本号
    public static final String VERSION = "v0.4.12";
    //是否启用customMOD,例如现开套牌
    public static boolean useModFlag = false;
    //最后决定使用的mod
    public static HashSet<String> enabledMods = new HashSet<>();
    //初始金币的数量
    public static int startGold = 500;
    //用户自定义的事件
    public static HashMap<String, BaseEvent> eventMap = new HashMap<>();
    public static ArrayList<BaseEvent> eventList = new ArrayList<>();
    //是否在战斗中开启友军小怪
    public static boolean friendMonsterFlag = false;
    //败者奖励等级
    public static int loserRewardFlag = 0;
    //特效管理器
    public static EffectManager effectManager = new EffectManager();
    //是否准备胜利
    public static boolean prepareWin = false;
    //当前被激活的输入框，这属于UI控制
    public static InputBox activateBox = null;
    //在当前在主界面直接进入lobby
    public static boolean enterLobbyFlag = false;

    public static void characterPatchInit()
    {
        CharacterSelectScreenPatches.NeowGetRelic.hasGiveGift=false;
    }

    //仅初始化与游戏相关但与网络无关的参数
    public static void initGameGlobal()
    {
        //禁用basemod里面的控制台
        DevConsole.enabled = false;
        //取消胜利准备
        prepareWin = false;
        ++idGame;
        //一些局部区域全局变量的初始化
        characterPatchInit();
        //初始化网络相关的静态变量
        SocketServer.initGlobal();
        MidExitScreen.screenInstance=null;
        //初始化，记录还没有换过boss遗物
        NeowRewardPatch.ChangeCasePatch.bossRelicChanged = false;
        //初始化玩家等待页面相关的变量
        CharacterSelectScreenPatches.TestUpdateFading.initJumpFlag();
        //连接状态谈出时相关信息的初始化
        MeunScreenFadeout.initFadeout();
        //事件管理器的全局初始化
        EventPatch.ChangeGetEvent.globalInit();
        //上次发送的游戏时间
        SteamConnector.lastHelloTime = 0;
        ConfigPage.oppositeCharacter = null;
        RenderPatch.ForceCloseRewardScreen.forceCloseOnce = false;
        //初始化准备时需要载入的纹理
        TextureManager.initTexture();
        //把进阶设置成0
        AbstractDungeon.ascensionLevel = 0;
        //初始化格挡增益遗物的数量
        BlockGainer.gainedNum=0;
    }

    public static void initGlobal()
    {
        //初始化steam管理器相关的全局变量
        SteamManager.initManager();
        AutomaticSocketServer.initAutomatic();
        //初始的尾巴数量
        beginTailNum = 2;
        invincibleRate = 2;
        //初始化每张牌可以被使用的次数
        UseCardSend.CardUseManager.MAX_USE_TIME = 2;
        RenderPatch.delayBox = null;
        //格挡增益的初始倍率
        BlockGainer.blockGainRate = 0.5f;
        //金钱掉落的比例
        SocketServer.loseGoldRate = 0;
        useModFlag = false;
        enabledMods.clear();
        //初始金币
        startGold = 500;
        //初始化获取资源的层数为5
        FakeEnding.ROW_NUM = 5;
        //友军默认是不开的
        friendMonsterFlag = false;
        //败者奖励，默认是小屋子
        loserRewardFlag = 0;

        //以下是与网络无关的设置
        initGameGlobal();
    }

    //选择人物时点击启程的操作，点击的时候会确定游戏即将开始
    //具体是在生成种子之前做触发
    @SpirePatch(clz = AbstractDungeon.class, method = "generateSeeds")
    public static class CharSelectConfirm
    {
        //在每次调用之前做处理，观察点击确认的按钮是不是被点击了
        @SpirePrefixPatch
        public static void fix()
        {
            System.out.println("confirm clicked!!!");
            //如果当前不是房间模式才会使用到这个地方的初始化
            if(!PanelScreenPatch.lobbyFlag)
                GlobalManager.initGlobal();
        }
    }

}
