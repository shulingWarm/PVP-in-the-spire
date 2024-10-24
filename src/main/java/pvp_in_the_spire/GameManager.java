package pvp_in_the_spire;

import pvp_in_the_spire.ui.Chat.ChatFoldPage;
import pvp_in_the_spire.patches.InputActionPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.SeedHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.random.Random;

//游戏管理器
//这里主要指的是即将进入游戏时的一些操作
//这是给游戏大厅用的，以前的普通p2p通信是不会用到这个的
public class GameManager {

    //给游戏设置随机种子，这是直接从CharacterSelectScreen那里抄过来的
    public static void setSeed()
    {
        long sourceTime = System.nanoTime();
        Random rng = new Random(sourceTime);
        Settings.seedSourceTimestamp = sourceTime;
        Settings.seed = SeedHelper.generateUnoffensiveSeed(rng);
        Settings.seedSet = false;
    }

    //执行进入游戏的准备 这个是从lobby进入游戏时专用的
    public static void prepareEnterGame()
    {
        //给游戏设置随机种子
        setSeed();
        //设置全局变量，这样打完这一局的时候就可以直接进入到房间里了
        GlobalManager.enterLobbyFlag = true;
        //把当前的游戏模式改成CHAR_SELECT
        CardCrawlGame.mode = CardCrawlGame.GameMode.CHAR_SELECT;
        //把选择角色的主界面设置成fadedOut,这代表直接进入游戏
        CardCrawlGame.mainMenuScreen.fadedOut = true;
        //取消音乐
        CardCrawlGame.mainMenuScreen.fadeOutMusic();
        Settings.isDailyRun = false;
        AbstractDungeon.generateSeeds();
        //如果没有打开聊天窗口，就把快捷键开一下
        if(!ChatFoldPage.getInstance().chatBox.isOpen)
        {
            ChatFoldPage.instance.chatBox.close();
        }
        else
        {
            InputActionPatch.allowShortcut = true;
        }
        InputHelper.initialize();
    }

}
