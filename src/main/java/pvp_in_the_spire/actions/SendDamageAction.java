package pvp_in_the_spire.actions;

import pvp_in_the_spire.AutomaticSocketServer;
import pvp_in_the_spire.SocketServer;
import pvp_in_the_spire.patches.ActionNetworkPatches;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;

//发送伤害相关的action
//多段伤害的时候发送的消息太急了，所以弄成队列的处理方式
public class SendDamageAction extends AbstractGameAction {

    //链接的damgeInfo和action
    DamageAction action;
    DamageInfo info;

    public SendDamageAction(DamageAction action,
        DamageInfo info) {
        //设置一个等待时间
        this.duration = 0.F;
        //记录相关的操作数据
        this.info = info;
        this.action = action;
        this.actionType = ActionType.WAIT;
    }

    //执行的时候再发送数据
    public void update() {
        System.out.println("send damage action update");
        if(SocketServer.USE_NETWORK)
        {
            AutomaticSocketServer server = AutomaticSocketServer.getServer();
            //把打击信息发送出去
            ActionNetworkPatches.damageEncode(server.streamHandle,
                action,info);
            server.send();
        }
        this.isDone = true;
    }

}
