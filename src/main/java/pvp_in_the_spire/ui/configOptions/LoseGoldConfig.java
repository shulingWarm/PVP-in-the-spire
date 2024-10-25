package pvp_in_the_spire.ui.configOptions;

import pvp_in_the_spire.AutomaticSocketServer;
import pvp_in_the_spire.SocketServer;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.options.DropdownMenu;

import java.util.ArrayList;

//丢金币的自定义
public class LoseGoldConfig extends TailNumSelect {

    public static final UIStrings uiStrings =
            CardCrawlGame.languagePack.getUIString("LoseGoldConfig");

    public LoseGoldConfig(float width)
    {
        super(width);
        //设置文本的内容
        this.textLabel.text = uiStrings.TEXT[0];
    }

    public void initOptions()
    {
        ArrayList<String> options = new ArrayList<>();
        //0% 到 100%
        for(int idRow=0;idRow<=10;++idRow)
        {
            options.add(String.valueOf(idRow*10)+"%");
        }
        dropdownMenu = new DropdownMenu(this,options,getOptionFont(), Color.WHITE);
        this.sendConfigChangeFlag = false;
        dropdownMenu.setSelectedIndex(0);
        this.sendConfigChangeFlag = true;
    }

    //更改下拉菜单的选项
    @Override
    public void changedSelectionTo(DropdownMenu dropdownMenu, int i, String s) {
        //更改金钱掉落比例
        SocketServer.loseGoldRate = 0.1f * i;
        //判断是否需要发送消息
        if (sendConfigChangeFlag) {
            SocketServer server = AutomaticSocketServer.getServer();
            this.sendConfigChange(server.streamHandle, i);
            server.send();
        }
    }

}
