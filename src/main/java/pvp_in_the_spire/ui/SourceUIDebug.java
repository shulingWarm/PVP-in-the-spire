package pvp_in_the_spire.ui;

//由于主界面的渲染处于fadeout阶段，有可能会影响很多输入效果，把这些地方禁用掉
public class SourceUIDebug {

//    @SpirePatch(clz = Hitbox.class,method = "update",
//        paramtypez = {})
//    public static class FadingoutCheck
//    {
//        @SpirePostfixPatch
//        public static void fix(Hitbox __instance)
//        {
//            //如果处于淡出阶段不能用，就报告一下
//            if(AbstractDungeon.isFadingOut)
//            {
//                System.out.println("fadeout warning");
//            }
//            if(__instance.hovered)
//            {
//                System.out.println("hovered");
//            }
//            if(__instance.clicked)
//            {
//                System.out.println("clicked");
//            }
//        }
//    }
//
//    //用于判断按钮是不是吸收了click信息
//    @SpirePatch(clz = Button.class, method = "update")
//    public static class AbsorbClickInfoCheck
//    {
//        public static boolean justOk = false;
//
//        @SpirePrefixPatch
//        public static void prefix(Button __instance)
//        {
//            justOk = InputHelper.justClickedLeft;
//        }
//
//        @SpirePostfixPatch
//        public static void postfix(Button __instance)
//        {
//            if(InputHelper.justClickedLeft != justOk)
//            {
//                System.out.println("Button click changing");
//            }
//        }
//    }
//
//    @SpirePatch(clz = TopPanel.class, method = "update")
//    public static class AbsorbClickInfoCheckTopPanel
//    {
//        public static boolean justOk = false;
//
//        @SpirePrefixPatch
//        public static void prefix(TopPanel __instance)
//        {
//            justOk = InputHelper.justClickedLeft;
//        }
//
//        @SpirePostfixPatch
//        public static void postfix(TopPanel __instance)
//        {
//            if(InputHelper.justClickedLeft != justOk)
//            {
//                System.out.println("TopPanel click changing");
//            }
//        }
//    }
//
//    //按键点击的信息判断
//    @SpirePatch(clz = InputHelper.class,method = "updateFirst")
//    public static class MouseDownCheck
//    {
//        @SpirePostfixPatch
//        public static void fix()
//        {
//            //记录是否按下左键
//            if(InputHelper.isMouseDown)
//            {
//                System.out.println("InputHelper.isMouseDown");
//            }
//            if(InputHelper.touchDown)
//            {
//                System.out.println("InputHelper.touchDown");
//            }
//            if(InputHelper.justClickedLeft)
//            {
//                System.out.println("InputHelper.justClickedLeft");
//            }
//            try
//            {
//                Field tempField = InputHelper.class.getDeclaredField("isPrevMouseDown");
//                tempField.setAccessible(true);
//                boolean preClickFlag = (Boolean) tempField.get(null);
//                if(preClickFlag)
//                {
//                    System.out.println("isPrevMouseDown");
//                }
//            }
//            catch (NoSuchFieldException | IllegalAccessException e)
//            {
//                e.printStackTrace();
//            }
//        }
//    }

}
