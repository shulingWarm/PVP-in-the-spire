package pvp_in_the_spire.actions;

import pvp_in_the_spire.SocketServer;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.io.IOException;
import java.util.ArrayList;

//抽象的通信协议


public class ActionProtocol extends AbstractActionProtocol {

    public int baseDamage = -200;
    //执行操作的源头
    AbstractCreature actionSource_;

    public ActionProtocol(AbstractCreature actionSource)
    {
        this.actionSource_ = actionSource;
    }

    //当前是否还需要读取数据
    public boolean isNeedData()
    {
        return baseDamage == -200;
    }


    //从网络连接里面读取数据，并返回是否已经读取结束
    //如果已经读取完了就返回true
    public boolean readData(SocketServer server)
    {
        //从数据里面读取最基本的伤害值
        try{
            //如果流是空的也可以直接返回false
            if(server.inputHandle.available()==0)
            {
                return false;
            }
            baseDamage = server.inputHandle.readInt();
            return true;
        }
        catch (IOException e){
            return false;
        }
    }

    //生成相应的action
    public ArrayList<AbstractGameAction> getAction()
    {
        //用于存储action信息的列表
        ArrayList<AbstractGameAction> actList = new ArrayList<AbstractGameAction>();
        //添加打人的动画
        actList.add(new AnimateSlowAttackAction(actionSource_));
        //生成临时的伤害数值信息
        DamageInfo tempInfo = new DamageInfo(actionSource_,baseDamage);
        //生成相应的对应于伤害值的action
        actList.add(new DamageAction(AbstractDungeon.player,tempInfo,AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
        //返回已经添加好的action列表
        return actList;
    }


}
