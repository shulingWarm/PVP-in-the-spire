package pvp_in_the_spire.events;

import pvp_in_the_spire.ui.RelicPanel;
import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.BaseEvent;
import pvp_in_the_spire.network.PlayerInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//发送玩家的遗物事件
public class PlayerRelicEvent extends BaseEvent {


    public PlayerRelicEvent()
    {
        this.eventId = "PlayerRelicEvent";
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        //发送自己的tag
        GlobalManager.playerManager.encodePlayer(streamHandle);
        try
        {
            //发送遗物的数量
            streamHandle.writeInt(
                AbstractDungeon.player.relics.size()
            );
            //遍历当前玩家的遗物
            for(AbstractRelic eachRelic : AbstractDungeon.player.relics)
            {
                //写入的遗物名称
                streamHandle.writeUTF(eachRelic.relicId);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void decode(DataInputStream streamHandle) {
        //解码对应的PlayerMonster
        PlayerInfo info = GlobalManager.playerManager.decodePlayerInfo(streamHandle);
        if(info == null)
            return;
        //依次读取每个relic
        try
        {
            //清空玩家的遗物列表
            info.relicList.clear();
            int relicNum = streamHandle.readInt();
            //遍历每个relic
            for(int idRelic=0;idRelic<relicNum;++idRelic)
            {
                String relicName = streamHandle.readUTF();
                AbstractRelic tempRelic;
                //判断是不是用过的尾巴
                if(relicName.equals(RelicPanel.usedTailName))
                {
                    tempRelic = RelicPanel.getUsedTailInstance();
                }
                else {
                    tempRelic = RelicLibrary.getRelic(relicName).makeCopy();
                }
                info.relicList.add(tempRelic);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
