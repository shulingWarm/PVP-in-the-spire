package pvp_in_the_spire.actions;

import pvp_in_the_spire.SocketServer;
import pvp_in_the_spire.actions.actionList.ActListGeneratorBase;
import pvp_in_the_spire.actions.actionList.AttackActList;
import pvp_in_the_spire.actions.actionList.EssenceOfSteelActList;
import pvp_in_the_spire.actions.actionList.RitualActList;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;

import java.io.IOException;
import java.util.ArrayList;

import pvp_in_the_spire.actions.actionList.DoubleStrength;
import pvp_in_the_spire.actions.actionList.BlockActList;
import pvp_in_the_spire.actions.actionList.AddWoundAction;
import pvp_in_the_spire.actions.actionList.CuriosityActList;
import pvp_in_the_spire.actions.actionList.RegeneratePowerAction;
import pvp_in_the_spire.actions.actionList.VulnerabilityAddAction;
import pvp_in_the_spire.actions.actionList.WeakenAddAction;

//用来演示kaka效果的通信模式，可以根据指令做不同的操作
public class KaKaProtocol extends AbstractActionProtocol {


    //生成各种act列表的接口
    public static ArrayList<ActListGeneratorBase> generatorList;

    //初始化各种action的函数的映射表
    static void initActList()
    {
        //如果列表不是空的，那就初始化一下
        if(generatorList != null) return;
        generatorList = new ArrayList<ActListGeneratorBase>();
        //添加每个操作
        generatorList.add(new AttackActList());
        generatorList.add(new RitualActList());
        generatorList.add(new EssenceOfSteelActList());
        generatorList.add(new DoubleStrength());
        generatorList.add(new BlockActList());
        generatorList.add(new AddWoundAction());
        generatorList.add(new CuriosityActList());
        generatorList.add(new RegeneratePowerAction());
        generatorList.add(new VulnerabilityAddAction());
        generatorList.add(new WeakenAddAction());
    }


    //各种操作行为的映射表
    public static ArrayList<AbstractGameAction> actListMapping(AbstractCreature actSource,
           int baseValue,int actType)
    {
        //初始化action的列表
        initActList();
        //根据种类调用里面的数据
        return generatorList.get(actType).getActList(actSource,baseValue);
    }


    //怪物的源头
    AbstractCreature actionSource_;
    //执行效果的数值
    private int baseValue_ = -200;
    //通过读取数据得到的操作各类
    private  int actType_ = -1;

    public KaKaProtocol(AbstractCreature actionSource)
    {
        this.actionSource_ = actionSource;
    }

    //判断是否还需要接收数据
    @Override
    public boolean isNeedData() {
        return baseValue_ == -200;
    }

    //从网络连接里面读取数据，并返回是否已经读取结束
    //如果已经读取完了就返回true
    @Override
    public boolean readData(SocketServer server)
    {
        //从数据里面读取最基本的伤害值
        try{
            //如果流是空的也可以直接返回false
            if(server.inputHandle.available()==0)
            {
                return false;
            }
            //是否需要读取种类
            if(actType_ == -1)
            {
                actType_ = server.inputHandle.readInt();
            }
            else {
                baseValue_ = server.inputHandle.readInt();
            }
            //处理完之后检查一下是否还需要别的操作
            if(!isNeedData())
                return true;
        }
        catch (IOException e){

        }
        return false;
    }

    //生成相应的action
    @Override
    public ArrayList<AbstractGameAction> getAction()
    {
        //通过映射表来获取活动信息
        return actListMapping(actionSource_,baseValue_,actType_);
    }
}
