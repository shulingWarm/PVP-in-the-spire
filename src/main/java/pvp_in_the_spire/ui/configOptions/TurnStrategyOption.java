package pvp_in_the_spire.ui.configOptions;

import pvp_in_the_spire.AutomaticSocketServer;
import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.SocketServer;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.options.DropdownMenu;

import java.util.ArrayList;

public class TurnStrategyOption extends TailNumSelect{

    public static final UIStrings uiStrings =
            CardCrawlGame.languagePack.getUIString("TurnStrategyOption");

    public TurnStrategyOption(float width)
    {
        super(width);
        //设置文本的内容
        this.textLabel.text = uiStrings.TEXT[0];
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


    //更改下拉菜单的选项
    @Override
    public void changedSelectionTo(DropdownMenu dropdownMenu, int i, String s) {
        //更改是否添加小怪的全局设置
        GlobalManager.turnStrategy
                = i;
        //判断是否需要发送消息
        if (sendConfigChangeFlag) {
            SocketServer server = AutomaticSocketServer.getServer();
            this.sendConfigChange(server.streamHandle, i);
            server.send();
        }
    }

}
