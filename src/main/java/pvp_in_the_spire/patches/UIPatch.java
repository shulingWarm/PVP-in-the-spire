package pvp_in_the_spire.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.credits.CreditsScreen;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.TinyHouse;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;

//为了制作输入ip的界面设置的patch
public class UIPatch {

//    //补充的点击事件，添加一些枚举类型
//    public static class Enums
//    {
//        @SpireEnum
//        public static MainMenuPanelButton.PanelClickResult BE_HOST;
//        //加入他人游戏的界面
//        @SpireEnum
//        public static MainMenuPanelButton.PanelClickResult JOIN_OTHER;
//    }
//
//    //测试开始游戏的按钮是什么时候触发的
//    @SpirePatch(clz = MenuPanelScreen.class, method = "initializePanels")
//    public static class MenuPlayStop
//    {
//        @SpirePrefixPatch
//        public static SpireReturn<Void> fix(MenuPanelScreen __instance,
//                                      MenuPanelScreen.PanelScreen ___screen,
//                                    float ___PANEL_Y)
//        {
//            if(___screen == MenuPanelScreen.PanelScreen.PLAY)
//            {
//                //添加host形式的按钮
//                __instance.panels.add(new
//                    MainMenuPanelButton(
//                        Enums.BE_HOST,
//                        MainMenuPanelButton.PanelColor.BEIGE,
//                        Settings.WIDTH / 2.0F - 225.0F,
//                        ___PANEL_Y));
//                //另一个选项卡，对应的是join的选项
//                __instance.panels.add(new
//                        MainMenuPanelButton(
//                        MainMenuPanelButton.PanelClickResult.PLAY_DAILY,
//                        MainMenuPanelButton.PanelColor.BEIGE,
//                        Settings.WIDTH / 2.0F + 225.0F,
//                        ___PANEL_Y));
//                return SpireReturn.Return();
//            }
//            return SpireReturn.Continue();
//        }
//    }
//
//    //显示按钮的时候，改成显示自己定义的按钮标头
//    @SpirePatch(clz = MainMenuPanelButton.class, method = "setLabel")
//    public static class ChangeButtonLabel
//    {
//        //对自己定义的类型进行截流，如果是自己定义的类型，就直接更新它显示的label
//        @SpirePrefixPatch
//        public static SpireReturn<Void> fix(MainMenuPanelButton __instance,
//           MainMenuPanelButton.PanelClickResult ___result)
//        {
//            //判断result是不是自己定义的那种
//            if(___result == Enums.BE_HOST)
//            {
//                //修改贴图，贴图必须单独设置一下
//                ReflectionHacks.setPrivate(__instance, MainMenuPanelButton.class, "panelImg", ImageMaster.MENU_PANEL_BG_BLUE);
//                ReflectionHacks.setPrivate(__instance, MainMenuPanelButton.class, "portraitImg", ImageMaster.P_STANDARD);
//                //修改按钮显示的标头和描述
//                ReflectionHacks.setPrivate(__instance, MainMenuPanelButton.class, "header", "build a room");
//                ReflectionHacks.setPrivate(__instance, MainMenuPanelButton.class, "description", "wait connection from others");
//                return SpireReturn.Return();
//            }
//            return SpireReturn.Continue();
//        }
//
//    }

    //禁止打开制作组名单，战斗胜利的时候直接回到主界面
    @SpirePatch(clz = CreditsScreen.class, method = "open")
    public static class RemoveCreditScreen
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> fix(CreditsScreen __instance,
                                            boolean playCreditsBgm
        )
        {
            //把screen改成主界面
            CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.MAIN_MENU;
            return SpireReturn.Return();
        }
    }

    //检查小屋子是否调用了reopen
    @SpirePatch(clz = CombatRewardScreen.class, method = "reopen")
    public static class TinyHouseReopen
    {

        //小屋子的名称
        public static final RelicStrings HOUSE_STRINGS = CardCrawlGame.languagePack.getRelicStrings(TinyHouse.ID);
        public static final UIStrings CLOSE_STRINGS = CardCrawlGame.languagePack.getUIString("CombatRewardScreen");

        @SpirePostfixPatch
        public static void fix(CombatRewardScreen __instance,
            String ___labelOverride)
        {
            if(___labelOverride!=null && ___labelOverride.equals(
                HOUSE_STRINGS.DESCRIPTIONS[3]
            ))
            {
                //判断取消按钮是否正在显示
                if(AbstractDungeon.overlayMenu.cancelButton.isHidden)
                {
                    //强制它重新显示
                    AbstractDungeon.overlayMenu.cancelButton.show(CLOSE_STRINGS.TEXT[6]);
                }
            }
        }
    }
}
