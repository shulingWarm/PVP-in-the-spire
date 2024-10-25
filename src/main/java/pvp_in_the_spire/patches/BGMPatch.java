package pvp_in_the_spire.patches;

import pvp_in_the_spire.SocketServer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

//游戏音乐相关的补丁
public class BGMPatch {

    //随机播放一个音乐
    //根据双方的生命来决定，当双方血量都正常的时候随机播放1,2,3层的音乐
    //有一方快死了的时候播放心脏的bgm
    public static void playRandomBGM()
    {
        //如果双方有一方要死了，就播放心脏的bgm
        if(SocketServer.myTailNum==0 || SocketServer.tailNum==0)
        {
            CardCrawlGame.music.playTempBgmInstantly("BOSS_ENDING");
            return;
        }
        //生成一个随机数
        int idBgm = AbstractDungeon.cardRandomRng.random(2);
        //根据情况播放bgm
        switch (idBgm)
        {
            case 0:
                CardCrawlGame.music.playTempBgmInstantly("BOSS_BOTTOM");
                break;
            case 1:
                CardCrawlGame.music.playTempBgmInstantly("BOSS_CITY");
                break;
            default:
                CardCrawlGame.music.playTempBgmInstantly("BOSS_BEYOND");
        }
    }

}
