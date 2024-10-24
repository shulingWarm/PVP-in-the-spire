package pvp_in_the_spire.screens.midExit;

import pvp_in_the_spire.AutomaticSocketServer;
import pvp_in_the_spire.screens.UserButton;
import pvp_in_the_spire.SocketServer;
import pvp_in_the_spire.actions.FightProtocol;
import pvp_in_the_spire.patches.RenderPatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;

import java.io.DataOutputStream;
import java.io.IOException;

public class ExitButton extends UserButton {

    //是否强制退出
    public boolean forceExitFlag=false;

    //退出回到主界面的操作
    public static void returnMainMenu()
    {
        //停止渲染延迟信息
        RenderPatch.delayBox = null;
        //返回游戏的主界面
        CardCrawlGame.music.fadeAll();
        AbstractDungeon.getCurrRoom().clearEvent();
        AbstractDungeon.closeCurrentScreen();
        SaveAndContinue.deleteSave(AbstractDungeon.player);
        CardCrawlGame.startOver();
        if (RestRoom.lastFireSoundId != 0L) {
            CardCrawlGame.sound.fadeOut("REST_FIRE_WET", RestRoom.lastFireSoundId);
        }

        if (!AbstractDungeon.player.stance.ID.equals("Neutral") && AbstractDungeon.player.stance != null) {
            AbstractDungeon.player.stance.stopIdleSfx();
        }
    }

    //发送退出游戏的信号
    public static void sendExitGame(DataOutputStream streamHandle)
    {
        try
        {
            //发送结束游戏的数据头
            streamHandle.writeInt(FightProtocol.EXIT_GAME_INFO);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public ExitButton(float x, float y, float width, float height,
          BitmapFont font)
    {
        super(x,y,width,height,"exit",font);
    }

    //按钮的点击事件
    @Override
    public void clickEvent()
    {
        //发送退出游戏的信号
        //但如果目前处于强制退出的状态就不用发送了
        if(!forceExitFlag)
        {
            SocketServer server = AutomaticSocketServer.getServer();
            sendExitGame(server.streamHandle);
            server.send();
        }
        //结束游戏
        returnMainMenu();
    }

}
