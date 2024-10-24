package pvp_in_the_spire.actions;

import pvp_in_the_spire.SocketServer;
import com.megacrit.cardcrawl.actions.AbstractGameAction;

import java.util.ArrayList;

//抽象的动作协议
abstract public class AbstractActionProtocol {

    public AbstractActionProtocol()
    {

    }

    //当前是否还需要读取数据
    public boolean isNeedData()
    {
        return false;
    }



    //从网络连接里面读取数据，并返回是否已经读取结束
    //如果已经读取完了就返回true
    public boolean readData(SocketServer server)
    {
        return true;
    }

    //生成相应的action
    public ArrayList<AbstractGameAction> getAction()
    {
        return new ArrayList<AbstractGameAction>();
    }

}
