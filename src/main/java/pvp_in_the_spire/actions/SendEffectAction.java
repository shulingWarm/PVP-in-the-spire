package pvp_in_the_spire.actions;

import pvp_in_the_spire.AutomaticSocketServer;
import pvp_in_the_spire.patches.ActionNetworkPatches;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

//特殊的发送特效的action
public class SendEffectAction extends AbstractGameAction {

    public AbstractGameEffect effect;
    public float duration;


    public SendEffectAction(AbstractGameEffect effect,float duration)
    {
        //记录类内属性
        this.effect = effect;
        this.duration = duration;
        //设置一个等待时间
        this.duration = 0.1F;
        this.actionType = ActionType.WAIT;
    }

    public void update()
    {
        this.tickDuration();
        if(this.isDone)
        {
            AutomaticSocketServer server = AutomaticSocketServer.getServer();
            //发送相关的信息
            ActionNetworkPatches.encodeEffect(
                    server.streamHandle,
                    this.effect,this.duration
            );
            server.send();
        }
    }

}
