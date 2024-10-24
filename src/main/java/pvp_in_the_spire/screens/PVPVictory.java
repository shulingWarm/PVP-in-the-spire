package pvp_in_the_spire.screens;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.TrueVictoryRoom;

//胜利的结算画面
public class PVPVictory {

    //参考的是goToTrueVictoryRoom这个函数
    public static void enterVictory()
    {
        CardCrawlGame.music.fadeOutBGM();
        MapRoomNode node = new MapRoomNode(7, 5);
        node.room = new TrueVictoryRoom();
        AbstractDungeon.nextRoom = node;
        AbstractDungeon.closeCurrentScreen();
        //AbstractDungeon.nextRoomTransitionStart();
    }

}
