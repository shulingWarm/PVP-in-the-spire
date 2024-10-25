package pvp_in_the_spire.events;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.BaseEvent;
import pvp_in_the_spire.network.PlayerInfo;
import pvp_in_the_spire.network.SelfPlayerInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.FairyPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Boot;
import com.megacrit.cardcrawl.relics.Calipers;
import com.megacrit.cardcrawl.relics.LizardTail;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//发送战斗信息的事件
//这里直接就是发送我方的基本信息就可以了
public class BattleInfoEvent extends BaseEvent {

    public BattleInfoEvent()
    {
        this.eventId = "BattleInfoEvent";
    }

    @Override
    public void encode(DataOutputStream streamHandle) {
        try
        {
            SelfPlayerInfo playerInfo = GlobalManager.playerManager.selfPlayerInfo;
            //和之前最大的不同，这里需要写上我方的tag
            streamHandle.writeInt(GlobalManager.myPlayerTag);
            //发送目前的最大生命上限
            streamHandle.writeInt(AbstractDungeon.player.maxHealth);
            streamHandle.writeInt(AbstractDungeon.player.currentHealth);
            //发送自己的尾巴数量
            int tailCount = 0;
            //判断是不是有发条靴
            int hasBoot = 0;
            //是否有外卡钳的信息
            int hasCaliper=0;
            for(AbstractRelic eachRelic : AbstractDungeon.player.relics)
            {
                //判断是不是尾巴
                if(eachRelic.relicId.equals(LizardTail.ID) && !eachRelic.usedUp)
                {
                    tailCount++;
                }
                else if(hasBoot==0 && eachRelic.relicId.equals(Boot.ID))
                {
                    hasBoot++;
                }
                else if(hasCaliper==0 && eachRelic.relicId.equals(Calipers.ID))
                {
                    hasCaliper++;
                }
            }
            //发送尾巴的数量
            streamHandle.writeInt(tailCount);
            //初始化自己的尾巴数量
            playerInfo.tailNum = tailCount;
            //发送是否有靴子
            streamHandle.writeInt(hasBoot);
            //发送外卡钳的信息
            streamHandle.writeInt(hasCaliper);
            int fairyPotionNum = 0;
            //获取瓶中精灵的数量
            for(AbstractPotion eachPotion : AbstractDungeon.player.potions)
            {
                //判断是不是瓶中精灵
                if(eachPotion instanceof FairyPotion)
                {
                    fairyPotionNum++;
                }
            }
            //发送瓶中精灵的数量
            streamHandle.writeInt(fairyPotionNum);
            //发送金钱的信息
            streamHandle.writeInt(AbstractDungeon.player.gold);
            //发送自己的初始球数
            streamHandle.writeInt(AbstractDungeon.player.masterMaxOrbs);
            //发送自己的每回合能量
            streamHandle.writeInt(AbstractDungeon.player.energy.energyMaster);
            //发送我方的日期信息 这里需要修改
            streamHandle.writeLong(GlobalManager.playerManager.selfPlayerInfo.enterTime);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void decode(DataInputStream streamHandle) {
        try{
            //读取玩家的tag
            int tempTag = streamHandle.readInt();
            PlayerInfo playerInfo = GlobalManager.playerManager.getPlayerInfo(tempTag);
            if(playerInfo == null)
                return;
            //读取生命上限和当前的生命
            playerInfo.maxHealth = streamHandle.readInt();
            System.out.printf("Receive max health %d\n",playerInfo.maxHealth);
            playerInfo.currentHealth = streamHandle.readInt();
            //处理尾巴的数量
            playerInfo.tailNum = streamHandle.readInt();
            playerInfo.bootNum = streamHandle.readInt();
            //外卡钳信息
            playerInfo.hasCaliper = streamHandle.readInt();
            //瓶中精灵的数量
            playerInfo.fairyPotionNum = streamHandle.readInt();
            //金钱数量
            playerInfo.goldNum = streamHandle.readInt();
            //初始球位
            playerInfo.beginOrbNum = streamHandle.readInt();
            //每回合能量数
            playerInfo.maxEnergy = streamHandle.readInt();
            long enterTime = streamHandle.readLong();
            //给新进入战斗房间的玩家分配座次
            GlobalManager.playerManager.assignSeatOfPlayer(playerInfo);
            //进入游戏的时间
            GlobalManager.playerManager.updateEnterTime(
                playerInfo,enterTime
            );
            GlobalManager.playerManager.checkCanEnterBattle();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
