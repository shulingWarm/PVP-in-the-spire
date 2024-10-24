package pvp_in_the_spire.events;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.BaseEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//这是用来回传给用户，告诉他应该加入哪个房间
public class AssignTeamEvent extends BaseEvent {

    public int playerTag;
    int idTeam;

    public AssignTeamEvent(int playerTag,int idTeam)
    {
        this.playerTag = playerTag;
        this.idTeam = idTeam;
        this.eventId = "AssignTeamEvent";
    }

    public AssignTeamEvent(){
        this(0,0);
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        try
        {
            streamHandle.writeInt(playerTag);
            streamHandle.writeInt(idTeam);
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
            int tempTag = streamHandle.readInt();
            //如果这个tag不是自己那就不用再看了
            if(tempTag != GlobalManager.myPlayerTag)
                return;
            int tempTeamId = streamHandle.readInt();
            GlobalManager.playerManager.assignTeam(tempTag,tempTeamId);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
