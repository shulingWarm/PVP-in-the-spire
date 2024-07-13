package WarlordEmblem.patches;

import WarlordEmblem.Other.Pair;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

//用来记录各种角色的骨骼文件
public class AnimationRecorder {

    //骨骼文件的信息
    public static HashMap<AbstractPlayer.PlayerClass, Pair<String,String>> fileMap = new HashMap<>();

    //设置卡牌透明度的反射工具
    public static Method loadMethod = null;

    public static void initReflectMethod()
    {
        if(loadMethod!=null)
            return;
        try
        {
            loadMethod = AbstractCreature.class.getDeclaredMethod("loadAnimation", String.class, String.class, float.class);
            loadMethod.setAccessible(true);
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
    }

    //获取creature的PlayerClass
    public static AbstractPlayer.PlayerClass getPlayerClass(AbstractCreature creature)
    {
        return ((AbstractPlayer)creature).chosenClass;
    }

    @SpirePatch(clz = AbstractCreature.class, method = "loadAnimation")
    public static class AnimationPatch
    {
        @SpirePrefixPatch
        public static void fix(AbstractCreature __instance,
           String atlasUrl, String skeletonUrl, float scale
        )
        {
            if(!(__instance instanceof AbstractPlayer))
                return;
            AbstractPlayer.PlayerClass playerClass = getPlayerClass(__instance);
            //如果之前没有记录过，就记录下这个角色的信息
            if(!fileMap.containsKey(playerClass))
            {
                fileMap.put(playerClass,new Pair<>(atlasUrl,skeletonUrl));
            }
        }
    }

    //重新载入一个player的大小
    public static void resetCreatureScale(AbstractPlayer player,float scale)
    {
        initReflectMethod();
        //如果没有记录过这种creature就算了
        if(fileMap.containsKey(player.chosenClass))
        {
            try
            {
                Pair<String,String> file = fileMap.get(player.chosenClass);
                loadMethod.invoke(player,file.first,file.second,scale);
            }
            catch (IllegalAccessException | InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }
    }

}
