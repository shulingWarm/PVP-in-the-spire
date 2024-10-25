package pvp_in_the_spire.pvp_api;

import pvp_in_the_spire.character.ControlMoster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.stances.AbstractStance;

//获取有关敌人状态的信息
public class MonsterStage {

    //获取敌方的遗物
    public static AbstractRelic getRelic(String relicId)
    {
        if(ControlMoster.instance == null)
        {
            return null;
        }
        return ControlMoster.instance.relicPanel.getRelic(relicId);
    }

    //判断敌方有没有某遗物
    public static boolean hasRelic(String relicId)
    {
        if(ControlMoster.instance == null)
            return false;
        return getRelic(relicId)!=null;
    }

    //获取敌方的姿态
    public static AbstractStance getStance()
    {
        if(ControlMoster.instance == null)
            return null;
        return ControlMoster.instance.stance;
    }

    //更改敌方的姿态
    public static void changeStance(AbstractStance stance)
    {
        if(ControlMoster.instance != null)
        {
            ControlMoster.instance.changeStance(stance);
        }
    }

}
