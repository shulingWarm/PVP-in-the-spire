package pvp_in_the_spire.player_management;

import pvp_in_the_spire.network.PlayerInfo;

//人与人的轮流管理器
public class PersonTurnManager extends TurnManager {

    public PersonTurnManager(int seatNum)
    {
        super(seatNum);
    }

    @Override
    public int assignPlayerSeat(PlayerInfo info) {
        int idTeam = super.assignPlayerSeat(info);
        //判断目标是否为空
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
