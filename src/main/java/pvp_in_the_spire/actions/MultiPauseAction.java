package pvp_in_the_spire.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

//新时代的多玩家pause action
public class MultiPauseAction extends AbstractGameAction {

    //用于判断是否已经添加过子类了
    public boolean addedSonFlag = false;

    //全局的阻塞状态
    public static boolean pauseStage = false;
    //跳过下一次阻塞
    public static int jumpNextPause = 0;

    public MultiPauseAction()
    {
        this.duration = 0.1f;
        //设置为需要延时等待
        this.actionType = ActionType.WAIT;
    }

    @Override
    public void update() {
        this.tickDuration();
        if(!addedSonFlag && pauseStage)
        {
            //向队列里面添加一个自身
            AbstractDungeon.actionManager.addToBottom(
                new MultiPauseAction()
            );
            this.addedSonFlag = true;
        }
    }
}
