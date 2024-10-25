package pvp_in_the_spire.events;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.BaseEvent;
import pvp_in_the_spire.network.PlayerInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

//更新手牌事件
public class UpdateHandCardEvent extends BaseEvent {

    //当前的手牌状态
    public ArrayList<Integer> cardIdList;

    public UpdateHandCardEvent(ArrayList<Integer> cardIdList)
    {
        this.eventId = "UpdateHandCardEvent";
        this.cardIdList = cardIdList;
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        //编码自身的tag
        GlobalManager.playerManager.encodePlayer(streamHandle);
        try
        {
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
        if(info == null)
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
            info.cardManager.updateHandCard(tempCardList);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
