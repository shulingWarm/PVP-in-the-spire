package WarlordEmblem.patches.CardShowPatch;

import WarlordEmblem.Events.UpdateEnergyEvent;
import WarlordEmblem.PVPApi.Communication;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

//用于更新显示当前玩家的能量信息
public class EnergyUpdateSender {

    public static int lastEnergy = 0;

    //其实这东西只要随便找一个时隙来调用一下就可以了
    public static void updatePlayerEnergy()
    {
        //玩家当前的能量
        int currEnergy = EnergyPanel.getCurrentEnergy();
        if(currEnergy != lastEnergy)
        {
            lastEnergy = currEnergy;
            //发送更新能量的信息
            Communication.sendEvent(new UpdateEnergyEvent(currEnergy));
        }
    }

}