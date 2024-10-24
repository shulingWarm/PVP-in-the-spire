package pvp_in_the_spire.character;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//这是和角色有关的，但它并不是角色本身，它是用来存储角色的信息的
public class CharacterInfo {

    //存储到的角色信息
    public AbstractPlayer player = null;
    //玩家的真正贴图，有时候会把人物的真实贴图弄丢
    public Texture playerSourceImg = null;
    //访问角色信息的field
    public static Field atlasField = null;
    public static Field skeletonField = null;
    public static Field stateDataField = null;
    //复制实体的方法
    public static Method instanceMethod = null;
    //用于判断是否渲染死亡时的反射读取工具
    public static Field renderCorpseField = null;

    public static void initField()
    {
        if(atlasField!=null)
            return;
        try
        {
            atlasField = AbstractCreature.class.getDeclaredField("atlas");
            atlasField.setAccessible(true);
            skeletonField = AbstractCreature.class.getDeclaredField("skeleton");
            skeletonField.setAccessible(true);
            stateDataField = AbstractCreature.class.getDeclaredField("stateData");
            stateDataField.setAccessible(true);
            //用于判断是否渲染死亡时的图片
            renderCorpseField = AbstractPlayer.class.getDeclaredField("renderCorpse");
            renderCorpseField.setAccessible(true);
            //获得新实例的方法
            instanceMethod = AbstractPlayer.class.getDeclaredMethod("newInstance");
            instanceMethod.setAccessible(true);
        }
        catch (NoSuchFieldException | NoSuchMethodException e)
        {
            e.printStackTrace();
        }
    }

    //用于判断是否可以路过角色更新
    public AbstractPlayer.PlayerClass getPlayerClass()
    {
        return this.player.chosenClass;
    }

    //播放角色选择时的音效
    public void playPlayerSound()
    {
        if(this.player != null)
        {
            this.player.doCharSelectScreenSelectEffect();
        }
    }

    //更新角色的实例
    public void updatePlayer(AbstractPlayer srcPlayer)
    {
        if(srcPlayer==null)
        {
            System.out.println("warning: srcPlayer is null!!!!!");
        }
        try
        {
            this.player = (AbstractPlayer)instanceMethod.invoke(srcPlayer);
            //记录人物的真实贴图
            this.playerSourceImg = this.player.img;
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            e.printStackTrace();
        }
        if(this.player==null)
        {
            System.out.println("warning: this.player is null!!!!!");
        }
    }

    //把人物切换回活着时的图片
    public void resetAliveImg()
    {
        this.player.img = this.playerSourceImg;
        try
        {
            renderCorpseField.set(this.player,false);
            this.player.isDead = false;
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    public CharacterInfo(AbstractPlayer.PlayerClass playerClass)
    {
        System.out.println(playerClass.name());
        initField();
        //查找对应的角色信息
        //this.player = CardCrawlGame.characterManager.getCharacter(playerClass);
        updatePlayer(CardCrawlGame.characterManager.getCharacter(playerClass));
    }

    //获得角色用的信息,atlas
    public TextureAtlas getAtlas()
    {
        try
        {
            return (TextureAtlas) atlasField.get(player);
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    //获得skeleton
    public Skeleton getSkeleton()
    {
        try
        {
            return (Skeleton) skeletonField.get(player);
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    //获取状态数据
    public AnimationStateData getStateData()
    {
        try
        {
            return (AnimationStateData) stateDataField.get(player);
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    //获得动画状态
    public AnimationState getState()
    {
        return player.state;
    }


}
