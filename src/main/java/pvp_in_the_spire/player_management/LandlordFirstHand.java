package pvp_in_the_spire.player_management;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.network.PlayerInfo;

//地主总是先手的方案
public class LandlordFirstHand extends TurnManager {

    public LandlordFirstHand(int seatNum)
    {
        super(seatNum);
    }

    @Override
    public int assignPlayerSeat(PlayerInfo info) {
        //判断所在队伍是不是地主队伍
        PlayerTeam team = GlobalManager.playerManager.teams[info.idTeam];
        //初始座次
        int idTeam = 0;
        //判断这个队伍是不是地主队伍
        if(!team.isLandlord())
        {
            //设置为先手
            idTeam = 1;
        }
        if(this.seatList.get(idTeam).isEmpty())
            return idTeam;
        if(idTeam == 0)
        {
            for(int i=this.seatList.size() - 1;i>idTeam;--i)
            {
                if(this.seatList.get(i).isEmpty())
                    return i;
            }
        }
        else if(idTeam == 1)
        {
            for(int i=idTeam + 1;i<seatList.size();++i)
            {
                if(this.seatList.get(i).isEmpty())
                    return i;
            }
        }
        System.out.println("Warning: Invalid assignment!!");
        return -1;
    }
}
