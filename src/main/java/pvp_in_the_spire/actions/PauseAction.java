package pvp_in_the_spire.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.actions.GameActionManager;
import pvp_in_the_spire.SocketServer;

//抽象的数据读取协议


//这是用来获取用户操作的类
//获取到用户输入后就会停止向里面添加信息
public class PauseAction extends AbstractGameAction {

    //游戏的action队列，它会反复向游戏里面添加等待队列
    public GameActionManager globalManager;

    //网络通信的句柄，这个句柄需要不断传递来查看它是不是已经被更新过了
    public SocketServer serverHandle;

    //用于判断数据是否被读取的协议
    public AbstractActionProtocol protocol;

    //是否已经向队列里面添加过自己
    public boolean haveAddSonFlag = false;

    public PauseAction(GameActionManager manager,
           SocketServer inputServer,
           AbstractActionProtocol inputProtocol
   ) {
        this.setValues((AbstractCreature)null, (AbstractCreature)null, 0);
        this.duration = 0.1F;

        this.actionType = ActionType.WAIT;

        //记录活动队列管理器
        globalManager = manager;
        //记录输入的服务器
        serverHandle = inputServer;
        //记录数据读取协议
        protocol = inputProtocol;
    }

    @Override
    public void update() {
        this.tickDuration();
        //如果已经不需要数据了，那就什么都不需要处理了
        if(!protocol.isNeedData())
        {
            return;
        }
        if((!haveAddSonFlag) && this.isDone)
        {
            //向队列里面再添加一个自己
            globalManager.addToBottom(new PauseAction(globalManager,serverHandle,protocol));
            haveAddSonFlag = true;
        }
    }


}
