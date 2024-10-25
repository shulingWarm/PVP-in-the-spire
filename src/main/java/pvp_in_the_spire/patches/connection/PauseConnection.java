package pvp_in_the_spire.patches.connection;

//会阻塞显示动画的连接逻辑
//在选好角色进入开始的时候，执行网络连接，用以前的阻塞线程的方法就可以
public class PauseConnection {


    //使用它的时候需要把MeunScreenFadeout这个类注释掉

    //选好角色的时候进行双端的ip连接，使用以前的连接方法
//    @SpirePatch(clz = MainMenuScreen.class,method = "fadeOut")
//    public static class FadeoutConnection
//    {
//        //判断是不是正在fadeout,当首次进入淡出操作的时候，测试一次阻塞连接
//        @SpirePrefixPatch
//        public static void fix(MainMenuScreen __instance)
//        {
//            //判断一次是不是正在阻塞
//            if(__instance.isFadingOut)
//            {
//                //初始化连接
//                AutomaticSocketServer.getServer();
//            }
//        }
//    }

}
