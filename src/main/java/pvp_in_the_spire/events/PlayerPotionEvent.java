package pvp_in_the_spire.events;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.BaseEvent;
import pvp_in_the_spire.network.PlayerInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

//发送玩家药水信息的事件
public class PlayerPotionEvent extends BaseEvent {

    public PlayerPotionEvent()
    {
        this.eventId = "PlayerPotionEvent";
    }

    //获取玩家的有效药水
    public static ArrayList<AbstractPotion> getValidPotionList()
    {
        ArrayList<AbstractPotion> potionList = new ArrayList<>();
        for(AbstractPotion eachPotion : AbstractDungeon.player.potions)
        {
            if(!(eachPotion instanceof PotionSlot))
            {
                potionList.add(eachPotion);
            }
        }
        return potionList;
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        //发送自己的info信息
        GlobalManager.playerManager.encodePlayer(streamHandle);
        try
        {
            //获取玩家的有效药水
            ArrayList<AbstractPotion> potionList = getValidPotionList();
            //发送自己的药水信息
            streamHandle.writeInt(potionList.size());
            for(AbstractPotion eachPotion : potionList)
            {
                streamHandle.writeUTF(eachPotion.ID);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void decode(DataInputStream streamHandle) {
        //解析player info
        PlayerInfo info = GlobalManager.playerManager.decodePlayerInfo(streamHandle);
        if(info == null)
            return;
        try
        {
            int potionNum = streamHandle.readInt();
            info.potionList.clear();
            for(int idPotion=0;idPotion<potionNum;++idPotion)
            {
                String potionName = streamHandle.readUTF();
                AbstractPotion tempPotion = PotionHelper.getPotion(potionName);
                if(tempPotion != null)
                {
                    info.potionList.add(tempPotion);
                }
            }
            //让info更新药水列表
            info.updatePotionList();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
