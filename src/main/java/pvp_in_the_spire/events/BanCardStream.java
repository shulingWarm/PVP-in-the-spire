package pvp_in_the_spire.events;

import pvp_in_the_spire.pvp_api.BaseEvent;
import pvp_in_the_spire.ui.CardFilter.CardFilterScreen;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

//用于传输一系列被禁用的卡牌
public class BanCardStream extends BaseEvent {

    public ArrayList<String> cardNames;
    public boolean resetFlag;

    public BanCardStream(ArrayList<String> cardNames,boolean resetFlag)
    {
        this.eventId = "BanCardStream";
        this.cardNames = cardNames;
        this.resetFlag = resetFlag;
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        try
        {
            streamHandle.writeInt(this.cardNames.size());
            streamHandle.writeBoolean(this.resetFlag);
            for(String eachCard : this.cardNames)
            {
                streamHandle.writeUTF(eachCard);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void decode(DataInputStream streamHandle) {
        try
        {
            int cardNum = streamHandle.readInt();
            boolean tempResetFlag = streamHandle.readBoolean();
            //把目前所有的卡牌信息记录清空
            if(tempResetFlag)
                CardFilterScreen.instance.resetBanCardStage();
            for(int i=0;i<cardNum;++i)
            {
                String tempCard = streamHandle.readUTF();
                CardFilterScreen.instance.changeCardBanStage(tempCard,true);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
