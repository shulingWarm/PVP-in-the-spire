package WarlordEmblem.patches;

import WarlordEmblem.AutomaticSocketServer;
import WarlordEmblem.SocketServer;
import WarlordEmblem.actions.FightProtocol;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

import java.io.DataOutputStream;
import java.io.IOException;

//和球相关的patch
public class OrbPatch {

    public static void removeOrbEncode(DataOutputStream streamHandle)
    {
        //发送移除球操作的数据头
        try
        {
            streamHandle.writeInt(FightProtocol.REMOVE_ORB_INFO);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    //当自己移除球的时候，告诉对面也移除一个球
    @SpirePatch(clz = AbstractPlayer.class,method = "removeNextOrb")
    public static class RemoveOrbInfoSend
    {
        //即将处理的时候，告诉对面也做相同的处理
        @SpirePrefixPatch
        public static void fix(AbstractPlayer __instance)
        {
            //如果不需要使用网络就算了
            if(!SocketServer.USE_NETWORK)
            {
                return;
            }
            SocketServer server = AutomaticSocketServer.getServer();
            //发送移除球相关的信息
            removeOrbEncode(server.streamHandle);
            server.send();
        }
    }

}
