package pvp_in_the_spire.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.helpers.EventHelper;
import com.megacrit.cardcrawl.random.Random;

//和事件相关的补丁
public class EventPatch {

    //生成随机事件的函数重写
    //1.尼利事件只生成两次
    //2.遇到金神像后改成血神像
    @SpirePatch(clz = AbstractDungeon.class,method = "generateEvent")
    public static class ChangeGetEvent
    {
        //已经获得过金神像的次数
        public static int goldIdleTime=0;
        //已经进入宝典事件的次数
        public static int codexTime=0;

        //全局信息初始化
        public static void globalInit()
        {
            goldIdleTime=0;
            codexTime=0;
        }

        //判断是不是神像事件
        public static boolean isIdleEvent(int idEvent)
        {
            String eventName = AbstractDungeon.eventList.get(idEvent);
            return eventName.equals("Golden Idol") || eventName.equals("Forgotten Altar");
        }

        public static boolean isEventUsable(int idEvent)
        {
            //判断是不是神像事件
            if(isIdleEvent(idEvent))
            {
                //判断次数有没有达到
                return goldIdleTime<2;
            }
            if(AbstractDungeon.eventList.get(idEvent).equals("Cursed Tome"))
            {
                return codexTime<2;
            }
            return true;
        }

        public static void updateEventUseTime(int idEvent)
        {
            String name = AbstractDungeon.eventList.get(idEvent);
            //判断是不是宝典事件
            if(name.equals("Cursed Tome"))
            {
                codexTime++;
            }
            //判断是不是金神像
            else if(name.equals("Golden Idol"))
            {
                goldIdleTime++;
                AbstractDungeon.eventList.set(idEvent,"Forgotten Altar");
            }
            //判断是不是血神像
            else if(name.equals("Forgotten Altar"))
            {
                goldIdleTime++;
            }
        }

        @SpirePrefixPatch
        //直接截流生成event的函数
        public static SpireReturn<AbstractEvent> fix(Random rng)
        {
            //生成随机数
            int idEvent;
            while(true)
            {
                idEvent = rng.random(AbstractDungeon.eventList.size()-1);
                //判断这个事件能不能用
                if(isEventUsable(idEvent))
                    break;
            }
            String name = AbstractDungeon.eventList.get(idEvent);
            //更新事件的使用次数
            updateEventUseTime(idEvent);
            return SpireReturn.Return(EventHelper.getEvent(name));
        }
    }

}
