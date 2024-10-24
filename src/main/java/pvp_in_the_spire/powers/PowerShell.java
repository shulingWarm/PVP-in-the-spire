package pvp_in_the_spire.powers;

import com.megacrit.cardcrawl.powers.*;

import java.util.HashSet;

//buff的壳子，有些buff会受到伤害的触发，这是不行的，所以需要把这些buff改成壳子
public class PowerShell extends AbstractPower {

    public AbstractPower linkingPower;

    //构造的时候需要传入一个已经构造好的power
    public PowerShell(AbstractPower content)
    {
        linkingPower = content;
        //同步两个power的显示
        this.name = content.name;
        //对于腐化需要特殊处理，腐化有时候会用字符串来测试匹配
        if(content.ID.equals(CorruptionPower.POWER_ID))
        {
            this.ID = "Corruption_fake";
        }
        else {
            this.ID = content.ID;
        }
        this.owner = content.owner;
        this.amount = content.amount;
        this.type = content.type;
        //更新它区域的图标
        this.region48 = content.region48;
        this.region128=content.region128;
        //记录power是否能变成负数
        this.canGoNegative = content.canGoNegative;
        //更新它的显示
        this.updateDescription();
    }

    //更新显示
    public void updateDescription() {
        this.amount = linkingPower.amount;
        linkingPower.updateDescription();
        this.description = linkingPower.description;
    }

    //更新power的数量
    public void stackPower(int stackAmount) {
        linkingPower.stackPower(stackAmount);
        this.type = linkingPower.type;
    }

    public void reducePower(int reduceAmount) {
        linkingPower.reducePower(reduceAmount);
    }

    //用于判断哪些power需要被转换成shell
    public static HashSet<String> needShellPower=null;

    //初始化哪些power需要被转换到容器里面
    public static void initPowerInShell()
    {
        if(needShellPower!=null)
        {
            return;
        }
        needShellPower = new HashSet<String>();
    }

    //把power转换成容器里面的power
    //目前是默认所有的buff都使用壳子版本
    public static AbstractPower createShell(AbstractPower power)
    {
        return new PowerShell(power);
    }
}
