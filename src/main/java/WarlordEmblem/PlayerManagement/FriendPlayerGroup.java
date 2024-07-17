package WarlordEmblem.PlayerManagement;

import WarlordEmblem.character.PlayerMonster;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

//这东西是用来对标monster group的
public class FriendPlayerGroup {

    //所有的友军玩家的列表
    public ArrayList<PlayerMonster> playerList = new ArrayList<>();

    public void addPlayer(PlayerMonster playerMonster)
    {
        playerList.add(playerMonster);
        playerMonster.init();
    }

    //渲染
    public void render(SpriteBatch sb)
    {
        for(PlayerMonster eachPlayer : playerList)
        {
            eachPlayer.render(sb);
        }
    }

    public void update()
    {
        for(PlayerMonster eachPlayer : playerList)
        {
            eachPlayer.update();
        }
    }


}
