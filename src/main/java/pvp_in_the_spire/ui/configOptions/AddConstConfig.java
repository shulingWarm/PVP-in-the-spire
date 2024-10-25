package pvp_in_the_spire.ui.configOptions;

import pvp_in_the_spire.AutomaticSocketServer;
import pvp_in_the_spire.SocketServer;
import pvp_in_the_spire.patches.CardShowPatch.UseCardSend;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.options.DropdownMenu;

import java.util.ArrayList;

//设置同一张牌被打出几次后开始加费
public class AddConstConfig extends TailNumSelect {

    public static final UIStrings uiStrings =
            CardCrawlGame.languagePack.getUIString("AddConstConfig");

    //开始设置为无限的数值
    public static int NO_LIMIT_FLAG=0;

    public AddConstConfig(float width)
    {
        super(width);
        //设置文本的内容
        this.textLabel.text = uiStrings.TEXT[0];
        this.height = Settings.HEIGHT * 0.1f;
    }

    public void initOptions()
    {
        //初始化尾巴的下拉菜单
        ArrayList<String> options = new ArrayList<>();
        for(int idRow=2;idRow<=10;++idRow)
        {
            options.add(String.valueOf(idRow));
        }
        //最后一行再加入一个很大的数，表示不限制无限
        options.add("100");
        NO_LIMIT_FLAG = options.size()-1;
        dropdownMenu = new DropdownMenu(this,options,getOptionFont(), Color.WHITE);
        this.sendConfigChangeFlag = false;
        dropdownMenu.setSelectedIndex(UseCardSend.CardUseManager.MAX_USE_TIME-1);
        this.sendConfigChangeFlag = true;
    }

    //更改下拉菜单的选项
    @Override
    public void changedSelectionTo(DropdownMenu dropdownMenu, int i, String s) {
        //设置出牌限制的次数
        if(i==NO_LIMIT_FLAG)
        {
            UseCardSend.CardUseManager.MAX_USE_TIME = 99;
        }
        else {
            UseCardSend.CardUseManager.MAX_USE_TIME = i+1;
        }
        //判断是否需要发送消息
        if (sendConfigChangeFlag) {
            SocketServer server = AutomaticSocketServer.getServer();
            this.sendConfigChange(server.streamHandle, i);
            server.send();
        }
    }

}
