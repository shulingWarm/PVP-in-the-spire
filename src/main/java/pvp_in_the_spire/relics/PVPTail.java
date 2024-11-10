package pvp_in_the_spire.relics;

import basemod.devcommands.gold.Gold;
import com.megacrit.cardcrawl.rewards.RewardItem;
import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.patches.ActionNetworkPatches;
import pvp_in_the_spire.patches.CharacterSelectScreenPatches;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.LizardTail;
import com.megacrit.cardcrawl.screens.DeathScreen;

import java.lang.reflect.Field;

//pvp里面用的尾巴
public class PVPTail extends LizardTail {

    public static PVPTail listHead = null;
    public static PVPTail listEnd = null;

    //链表的下一个节点
    public PVPTail next = null;

    //初始化链表
    public static void initList()
    {
        listHead = null;
        listEnd = null;
    }

    public static void addTail(PVPTail tail)
    {
        if(listHead == null)
        {
            listHead = tail;
        }
        else {
            listEnd.next = tail;
        }
        listEnd = tail;
    }

    @Override
    public void onTrigger() {
        //标记为自身死亡
        GlobalManager.getBattleInfo().recordSelfDead();
    }

    //判断是否为可用的尾巴
    public boolean isUsable()
    {
        return !this.usedUp;
    }

    //触发第一个尾巴
    public static void triggerFirstTail()
    {
        //判断是否还有下一个尾巴
        if(listHead == null)
        {
            //游戏完全结束
            AbstractDungeon.player.isDead = true;
            AbstractDungeon.deathScreen = new DeathScreen(AbstractDungeon.getMonsters());
        }
        else {
            //调用第一个尾巴，执行回复操作
            listHead.heal();
            //判断是否还有下一个位置
            if(listHead.next != null)
            {
                listHead.changeId();
                listHead.setCounter(-2);
            }
            listHead = listHead.next;
        }
    }

    //令它的id失效
    public void changeId()
    {
        try
        {
            Field tempField = AbstractRelic.class.getDeclaredField("relicId");
            tempField.setAccessible(true);
            tempField.set(this,LizardTail.ID + "_used");
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    //给玩家回复生命
    public void heal()
    {
        ActionNetworkPatches.HealEventSend.disableSend = true;
        //把最大生命提高一倍
        AbstractDungeon.player.increaseMaxHp(
            AbstractDungeon.player.maxHealth,true
        );
        //获得格挡增益
        if(BlockGainer.blockGainRate > 0.01f)
        {
            (new BlockGainer()).instantObtain();
        }
        ActionNetworkPatches.HealEventSend.disableSend = false;
        //标记为用过的尾巴
        this.usedUp();
        //逃跑
        GlobalManager.getBattleInfo().resetPlayerToAlive();
        CharacterSelectScreenPatches.ChangeTailName.endCombatAsSmoke();
    }

    @Override
    public void instantObtain() {
        super.instantObtain();
        if(this.isUsable())
            addTail(this);
    }
}
