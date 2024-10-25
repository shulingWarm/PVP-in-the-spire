package pvp_in_the_spire.ui.configOptions;

import pvp_in_the_spire.AutomaticSocketServer;
import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.SocketServer;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.daily.mods.*;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.options.DropdownMenu;

import java.util.ArrayList;

//初始卡组的设置
public class StartDeckConfig extends TailNumSelect {

    public static final UIStrings uiStrings =
            CardCrawlGame.languagePack.getUIString("StartDeckConfig");

    //之前选择的row
    public int previousRow = 0;

    public StartDeckConfig(float width)
    {
        super(width);
        //设置文本的内容
        this.textLabel.text = uiStrings.TEXT[0];
    }

    //获得选项标号对应的modid
    public static String getModIdByRow(int idRow)
    {
        if(idRow==1)
            return SealedDeck.ID;
        else if(idRow==2)
            return Draft.ID;
        else if(idRow==3)
            return Shiny.ID;//金卡各一张
        else if(idRow==4)
            return Insanity.ID;
        else if(idRow==5)
            return Chimera.ID;//所有角色初始卡组混合
        return null;
    }

    public void initOptions()
    {
        ArrayList<String> options = new ArrayList<>();
        //3种选项
        for(int idRow=1;idRow<=6;++idRow)
        {
            options.add(uiStrings.TEXT[idRow]);
        }
        dropdownMenu = new DropdownMenu(this,options,getOptionFont(), Color.WHITE);
        this.sendConfigChangeFlag = false;
        dropdownMenu.setSelectedIndex(0);
        this.sendConfigChangeFlag = true;
    }

    //根据之前的idrow移除mod选项
    public void removePreviousMod()
    {
        String previousModStr = getModIdByRow(previousRow);
        if(previousModStr!=null)
            GlobalManager.enabledMods.remove(previousModStr);
    }

    public void addNewMod(int newIdRow)
    {
        previousRow = newIdRow;
        String modStr = getModIdByRow(newIdRow);
        if(modStr!=null)
            GlobalManager.enabledMods.add(modStr);
    }


    //更改下拉菜单的选项
    @Override
    public void changedSelectionTo(DropdownMenu dropdownMenu, int i, String s) {
        //移除之前的mod
        removePreviousMod();
        addNewMod(i);
        //判断是否需要发送消息
        if (sendConfigChangeFlag) {
            SocketServer server = AutomaticSocketServer.getServer();
            this.sendConfigChange(server.streamHandle, i);
            server.send();
        }
    }

}
