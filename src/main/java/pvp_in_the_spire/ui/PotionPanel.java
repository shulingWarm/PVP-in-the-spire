package pvp_in_the_spire.ui;

import pvp_in_the_spire.AutomaticSocketServer;
import pvp_in_the_spire.events.PlayerPotionEvent;
import pvp_in_the_spire.pvp_api.Communication;
import pvp_in_the_spire.SocketServer;
import pvp_in_the_spire.actions.FightProtocol;
import pvp_in_the_spire.character.ControlMoster;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.potions.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

//敌人的药水列表
public class PotionPanel {

    //需要显示的药水列表
    public ArrayList<AbstractPotion> potionList = new ArrayList<>();

    //起始的x位置
    public static final float X_BEGIN = Settings.WIDTH*0.88f;
    public static final float Y_BEGIN = Settings.HEIGHT*0.6f;
    //x的间隔位置
    public static final float Y_PAD = AbstractRelic.PAD_X;

    //上次检查时我方的药水列表
    public static ArrayList<AbstractPotion> lastPotion = new ArrayList<>();

    public PotionPanel()
    {

    }

    public void updatePotion(ArrayList<AbstractPotion> potions)
    {
        this.potionList = potions;
        updatePotionPos();
    }

    //更新每个药水的位置
    public void updatePotionPos()
    {
        //遍历每个药水，更新位置
        for(int id=0;id<potionList.size();++id)
        {
            AbstractPotion currPotion = potionList.get(id);
            currPotion.posX = X_BEGIN;
            currPotion.posY = Y_BEGIN - Y_PAD*id;
            currPotion.hb.move(currPotion.posX,currPotion.posY);
            currPotion.isObtained = true;
        }
    }

    public void update()
    {
        for(AbstractPotion eachPotion : potionList)
        {
            eachPotion.update();
        }
        //检查是否需要更新药水列表
        checkNeedSend();
    }

    //对每个药水的渲染
    public void render(SpriteBatch sb)
    {
        for(AbstractPotion eachPotion : potionList)
        {
            eachPotion.shopRender(sb);
        }
    }

    //对收到的potion信息的解码
    public static void potionDecode(DataInputStream streamHandle)
    {
        //解码药水
        try
        {
            int potionNum = streamHandle.readInt();
            ArrayList<AbstractPotion> potions = new ArrayList<>();
            for(int id=0;id<potionNum;++id)
            {
                String potionId = streamHandle.readUTF();
                AbstractPotion tempPotion = PotionHelper.getPotion(potionId);
                if(tempPotion!=null)
                    potions.add(tempPotion);
            }
            //更新药水显示
            ControlMoster.instance.potionPanel.updatePotion(potions);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //对potion信息的编码
    public static boolean potionEncode(DataOutputStream streamHandle)
    {
        try
        {
            ArrayList<AbstractPotion> potionList = new ArrayList<>();
            for(AbstractPotion eachPotion : AbstractDungeon.player.potions)
            {
                //判断是不是有效药水
                if(!(eachPotion instanceof PotionSlot))
                {
                    potionList.add(eachPotion);
                }
            }
            //如果没有有效药水就直接退出
            if(potionList.isEmpty())
                return false;
            streamHandle.writeInt(FightProtocol.POTION_LIST);
            //写有效药水的个数
            streamHandle.writeInt(potionList.size());
            //遍历写入每个药水的名字
            for(AbstractPotion eachPotion : potionList)
            {
                streamHandle.writeUTF(eachPotion.ID);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return true;
    }

    //发送我方的药水情况
    public static void sendMyPotion()
    {
        SocketServer server = AutomaticSocketServer.getServer();
        if(potionEncode(server.streamHandle))
            server.send();
    }

    //检查药水内容是否相同
    public static boolean checkListEqual(ArrayList<AbstractPotion> potion1,
             ArrayList<AbstractPotion> potion2)
    {
        if(potion1.size() != potion2.size())
            return false;
        for(int i=0;i<potion1.size();++i)
        {
            if(potion1.get(i) != potion2.get(i))
                return false;
        }
        return true;
    }

    //检查是否需要发送药水列表
    public static void checkNeedSend()
    {
        //如果是等待状态不传输这个信息
        if(AbstractDungeon.isScreenUp)
            return;
        //判断是否与之前的相同
        ArrayList<AbstractPotion> realPotions = AbstractDungeon.player.potions;
        if(!checkListEqual(lastPotion,realPotions))
        {
            lastPotion = (ArrayList<AbstractPotion>) realPotions.clone();
            Communication.sendEvent(new PlayerPotionEvent());
        }
    }

}
