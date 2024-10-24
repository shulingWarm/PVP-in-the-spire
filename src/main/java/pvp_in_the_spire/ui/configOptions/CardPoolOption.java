package pvp_in_the_spire.ui.configOptions;

import pvp_in_the_spire.AutomaticSocketServer;
import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.SocketServer;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.daily.mods.Diverse;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.options.DropdownMenu;

import java.util.ArrayList;

//卡池设置 可以是默认或者全角色混合
public class CardPoolOption extends TailNumSelect {

    public static final UIStrings uiStrings =
            CardCrawlGame.languagePack.getUIString("CardPoolOption");

    //之前选择的row
    public int previousRow = 0;

    public CardPoolOption(float width)
    {
        super(width);
        //设置文本的内容
        this.textLabel.text = uiStrings.TEXT[0];
    }

    //获得选项标号对应的modid
    public static String getModIdByRow(int idRow)
    {
        if(idRow==1)
            return Diverse.ID;
        return null;
    }

    public void initOptions()
    {
        ArrayList<String> options = new ArrayList<>();
        //3种选项
        for(int idRow=1;idRow<=2;++idRow)
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
