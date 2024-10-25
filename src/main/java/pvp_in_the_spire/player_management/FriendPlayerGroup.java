package pvp_in_the_spire.player_management;

import pvp_in_the_spire.character.PlayerMonster;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

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
            eachPlayer.hb.update();
            eachPlayer.intentHb.update();
            eachPlayer.healthHb.update();
            eachPlayer.update();
        }
    }

    //更新这里面玩家的power显示
    public void updateAnimation()
    {
        for(PlayerMonster eachPlayer : playerList)
        {
            eachPlayer.updatePowers();
        }
    }

    //获取当前正在选中的monster
    public PlayerMonster getHoveredMonster()
    {
        if(AbstractDungeon.player.isDraggingCard)
            return null;
        for(PlayerMonster eachMonster : playerList)
        {
            if(eachMonster.intentHb.hovered ||
                eachMonster.healthHb.hovered || eachMonster.hb.hovered)
            {
                return eachMonster;
            }
        }
        return null;
    }


}
