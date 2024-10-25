package pvp_in_the_spire.actions;

import pvp_in_the_spire.AutomaticSocketServer;
import pvp_in_the_spire.SocketServer;
import pvp_in_the_spire.character.ControlMoster;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//同步生命上限的操作
public class HealthSyncAction extends AbstractGameAction {

    public static void battleHealthEncode(DataOutputStream streamHandle)
    {
        //发送战斗健康信息的数据头
        try
        {
            streamHandle.writeInt(FightProtocol.BATTLE_HEALTH);
            //发送自己的生命值
            AbstractPlayer player = AbstractDungeon.player;
            streamHandle.writeInt(player.currentHealth);
            //发送自己的格挡值
            streamHandle.writeInt(player.currentBlock);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //接收自己的格挡值
    public static void battleHealthDecode(DataInputStream streamHandle)
    {
        try
        {
            int health = streamHandle.readInt();
            int currBlock = streamHandle.readInt();
            //判断是否controlmonster有实体
            ControlMoster monster = ControlMoster.instance;
            if(monster!=null)
            {
                monster.currentHealth = health;
                monster.currentBlock = currBlock;
                monster.healthBarUpdatedEvent();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        //发送自己的健康信息
        SocketServer server = AutomaticSocketServer.getServer();
        battleHealthEncode(server.streamHandle);
        server.send();
        this.isDone = true;
    }
}
