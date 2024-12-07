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

    public BanCardStream(ArrayList<String> cardNames)
    {
        this.eventId = "BanCardStream";
        this.cardNames = cardNames;
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        try
        {
            streamHandle.writeInt(this.cardNames.size());
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
