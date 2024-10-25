package pvp_in_the_spire.patches.CardShowPatch;

import com.megacrit.cardcrawl.cards.AbstractCard;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//一些用于更改卡牌透明度的工具
public class CardShowChange {

    //设置卡牌透明度的反射工具
    public static Method transparencyMethod = null;

    public static void initReflectMethod()
    {
        if(transparencyMethod!=null)
            return;
        try
        {
            transparencyMethod = AbstractCard.class.getDeclaredMethod("updateTransparency");
            transparencyMethod.setAccessible(true);
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
    }

    public static void changeCardAlpha(AbstractCard card,float alpha)
    {
        initReflectMethod();
        try
        {
            card.fadingOut = false;
            card.transparency = alpha;
            card.targetTransparency = alpha;
            transparencyMethod.invoke(card);
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            e.printStackTrace();
        }
    }

}
