package pvp_in_the_spire.events;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.BaseEvent;
import pvp_in_the_spire.network.PlayerInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

//即将抽到的牌的更新事件
public class DrawCardUpdateEvent extends BaseEvent {

    //当前的手牌状态
    public ArrayList<Integer> cardIdList;

    public DrawCardUpdateEvent(ArrayList<Integer> cardIdList)
    {
        this.eventId = "DrawCardUpdateEvent";
        this.cardIdList = cardIdList;
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        //编码自身的tag
        GlobalManager.playerManager.encodePlayer(streamHandle);
        try
        {
            System.out.printf("Sending draw pile num %d\n",cardIdList.size());
            //依次写入每个手牌
            streamHandle.writeInt(cardIdList.size());
            for(int eachCard : cardIdList)
            {
                streamHandle.writeInt(eachCard);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void decode(DataInputStream streamHandle) {
        //解码对应的player info
        PlayerInfo info = GlobalManager.playerManager.decodePlayerInfo(streamHandle);
        //如果是和自己同阵营的就不必处理了
        if(info == null || info.idTeam == GlobalManager.playerManager.selfPlayerInfo.idTeam)
            return;
        //依次读取手牌
        try
        {
            ArrayList<Integer> tempCardList = new ArrayList<>();
            int cardNum = streamHandle.readInt();
            for(int idCard=0;idCard<cardNum;++idCard)
            {
                int tempId = streamHandle.readInt();
                tempCardList.add(tempId);
            }
            info.cardManager.updateDrawPile(tempCardList);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
