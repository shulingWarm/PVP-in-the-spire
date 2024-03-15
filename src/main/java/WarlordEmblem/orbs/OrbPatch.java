package WarlordEmblem.orbs;

import WarlordEmblem.AutomaticSocketServer;
import WarlordEmblem.SocketServer;
import WarlordEmblem.actions.FightProtocol;
import basemod.devcommands.fight.Fight;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.Dark;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//球相关的补丁，但还有一些其它的球相关的补丁在Character里面
public class OrbPatch {
    //发送黑球的结束回合的操作
    public static void sendDarkEndTurn(AbstractOrb orb, DataOutputStream streamHandle)
    {
        //判断有没有这个球
        int idOrb = OrbMapping.getPlayerOrbNum(orb);
        if(idOrb<0)
            return;
        try
        {
            //发送数据头
            streamHandle.writeInt(FightProtocol.DARK_END_TURN);
            //发送球的编号
            streamHandle.writeInt(idOrb);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //解码黑球回合结束的操作
    public static void darkEndTurnDecode(DataInputStream streamHandle)
    {
        //读取球的标号
        try
        {
            int idOrb = streamHandle.readInt();
            //判断有没有这个球
            AbstractOrb orb = OrbMapping.getMonsterOrb(idOrb);
            if(orb!=null)
            {
                orb.onEndOfTurn();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //黑球的每回合触发的操作，出现这个东西时需要通知对面执行end turn操作
    @SpirePatch(clz = Dark.class,method = "onEndOfTurn")
    public static class DarkEndTurnPatch
    {
        //在黑球开始触发时通知对面进行相同的触发
        @SpirePrefixPatch
        public static void fix(Dark __instance)
        {
            //如果不使用网络的话是不用处理的
            if(!SocketServer.USE_NETWORK)
            {
                return;
            }
            AutomaticSocketServer server = AutomaticSocketServer.getServer();
            sendDarkEndTurn(__instance,server.streamHandle);
            server.send();
        }
    }

    //对球信息的解码
    public static void orbDescriptionDecode(DataInputStream streamHandle)
    {
        try
        {
            //获取球的标号
            int idOrb = streamHandle.readInt();
            //获取球的回合结束数据
            int passiveNum = streamHandle.readInt();
            //获取球的激发数值
            int evokeNum = streamHandle.readInt();
            //如果球的标号异常就不处理
            if(idOrb<0)
                return;
            //获得对应的球
            AbstractOrb orb = OrbMapping.getMonsterOrb(idOrb);
            //如果没有得到球就不处理了
            if(orb==null)
            {
                return;
            }
            //修改对应的球的数值
            orb.passiveAmount = passiveNum;
            orb.evokeAmount = evokeNum;
            //通知修改这个球的数值
            orb.updateDescription();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //发送新的关于球的描述
    public static void orbDescriptionEncode(AbstractOrb orb,DataOutputStream streamHandle)
    {
        //获取玩家里面的关于这个球的标号
        int idOrb = OrbMapping.getPlayerOrbNum(orb);
        //如果没有这个标号的话就算了
        if(idOrb==-1)
        {
            return;
        }
        try
        {
            //发送数据头
            streamHandle.writeInt(FightProtocol.ORB_UPDATE_DES);
            //发送球的标号
            streamHandle.writeInt(idOrb);
            //发送更新后的球的回合结束的数值
            streamHandle.writeInt(orb.passiveAmount);
            //发送球的激发数值
            streamHandle.writeInt(orb.evokeAmount);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //黑球更新描述的操作，其它的球会自己选择更新
    @SpirePatch(clz = Dark.class , method = "updateDescription")
    public static class UpdateDarkDescription
    {
        //更新黑球的处理操作
        @SpirePostfixPatch
        public static void fix(Dark __instance)
        {
            //不使用网络的情况下不需要处理
            if(!SocketServer.USE_NETWORK)
            {
                return;
            }
            AutomaticSocketServer server = AutomaticSocketServer.getServer();
            //发送新的球的描述
            orbDescriptionEncode(__instance,server.streamHandle);
            server.send();
        }
    }


}
