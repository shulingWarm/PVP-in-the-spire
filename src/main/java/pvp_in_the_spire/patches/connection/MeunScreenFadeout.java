package pvp_in_the_spire.patches.connection;


import pvp_in_the_spire.ui.LocalConnectPage;
import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.SocketServer;
import pvp_in_the_spire.network.Lobby.LobbyManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;

public class MeunScreenFadeout {

    //输入ip的界面
    public static InputIpBox ipBox = new InputIpBox();
    //是否开始输入ip
    public static boolean beginInputIp = false;

    //是否使用房间式的连接界面
    public static final boolean useGameRoomMode=true;
    //游戏连接的房间的界面
    public static LocalConnectPage localConnectPage = null;

    //是否已经连接成功的标志 当连接成功的时候需要修改这个位置的标志
    public static boolean connectOk = false;

    //因为要退出所以要渲染
    public static boolean allowForExit = false;

    //初始化消息淡出相关的操作
    public static void initFadeout()
    {
        ipBox = null;
        ipBox = new InputIpBox();
        beginInputIp=false;
        connectOk=false;
        localConnectPage = null;
        allowForExit = false;
    }

    @SpirePatch(clz = MainMenuScreen.class,method = "fadeOut")
    public static class StopFadeOut
    {
        //判断是否满足提前退出的条件，如果正在淡出，则禁止它淡出
        @SpirePrefixPatch
        public static SpireReturn<Void> fix(MainMenuScreen __instance)
        {
            //如果不使用网络的话就什么都不需要做
            if(!SocketServer.USE_NETWORK)
            {
                return SpireReturn.Continue();
            }
            //判断它是否正在谈出
            if(__instance.isFadingOut && (!connectOk) && (!allowForExit))
            {
                //如果是第一次打开，调用一下ip输入框的初始化逻辑
                if(!beginInputIp)
                {
                    //通过下面这个东西可以知道自己当前选的是什么人物
//                    System.out.println(
//                        CardCrawlGame.chosenCharacter.name()
//                    );
                    beginInputIp = true;
                    //判断是不是使用新式的游戏连接规则
                    if(useGameRoomMode)
                    {
                        localConnectPage = new LocalConnectPage();
                    }
                    else {
                        ipBox.open();
                    }
                }
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }

    }

    //主函数界面更新接口的调用，相关的显示界面需要用到这个接口
    @SpirePatch(clz = MainMenuScreen.class, method = "update")
    public static class MainMenuUpdateCalling
    {

        public static boolean justClickedLeft = false;
        public static boolean justReleasedClickLeft = false;
        public static boolean justClickedRight = false;
        public static boolean justReleasedClickRight = false;

        //在开始阶段记录进入时的输入接口，防止它因为fadeout被屏蔽掉
        @SpirePrefixPatch
        public static void prefix(MainMenuScreen __instance)
        {
            //记录开始阶段的输入操作
            justClickedRight = InputHelper.justClickedRight;
            justClickedLeft = InputHelper.justClickedLeft;
            justReleasedClickLeft = InputHelper.justReleasedClickLeft;
            justReleasedClickRight = InputHelper.justReleasedClickRight;
        }

        @SpirePostfixPatch
        public static void fix(MainMenuScreen __instance)
        {
            //如果不使用网络的话就什么都不需要做
            if(!SocketServer.USE_NETWORK)
            {
                return;
            }
            if(beginInputIp && (!connectOk) && (!allowForExit))
            {
                //把改过的input信息恢复回来
                InputHelper.justClickedRight = justClickedRight;
                InputHelper.justClickedLeft = justClickedLeft;
                InputHelper.justReleasedClickLeft = justReleasedClickLeft;
                InputHelper.justReleasedClickRight = justReleasedClickRight;
                //判断是不是使用的新式的连接规则
                if(useGameRoomMode)
                {
                    if(localConnectPage !=null)
                    {
                        localConnectPage.update();
                    }
                }
            }
        }
    }

    //对输入ip的界面的渲染
    @SpirePatch(clz = MainMenuScreen.class,method = "render")
    public static class IpInputRender
    {
        @SpirePostfixPatch
        public static void fix(MainMenuScreen __instance, SpriteBatch sb)
        {
            //判断是否需要强制回到之前的房间
            if(GlobalManager.enterLobbyFlag && __instance.screen == MainMenuScreen.CurScreen.MAIN_MENU)
            {
                LobbyManager.backLobby();
                GlobalManager.enterLobbyFlag = false;
            }
            //如果不使用网络的话就什么都不需要做
            if(!SocketServer.USE_NETWORK)
            {
                return;
            }
            if(beginInputIp && (!connectOk) && (!allowForExit))
            {
                //判断是不是使用的新式的连接规则
                if(useGameRoomMode)
                {
                    if(localConnectPage !=null)
                    {
                        localConnectPage.render(sb);
                    }
                }
                else
                {
                    ipBox.render(sb);
                }
            }
        }
    }

}
