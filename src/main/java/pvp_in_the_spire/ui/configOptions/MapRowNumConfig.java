package pvp_in_the_spire.ui.configOptions;

import pvp_in_the_spire.AutomaticSocketServer;
import pvp_in_the_spire.dungeon.FakeEnding;
import pvp_in_the_spire.SocketServer;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.options.DropdownMenu;

import java.util.ArrayList;

//战斗前获取资源的地图层数
public class MapRowNumConfig extends TailNumSelect {

    public static final UIStrings uiStrings =
            CardCrawlGame.languagePack.getUIString("MapRowNumConfig");

    public MapRowNumConfig(float width)
    {
        super(width);
        //设置文本的内容
        this.textLabel.text = uiStrings.TEXT[0];
    }

    public void initOptions()
    {
        ArrayList<String> options = new ArrayList<>();
        //从4到14
        for(int idRow=4;idRow<=14;idRow++)
        {
            options.add(String.valueOf(idRow));
        }
        dropdownMenu = new DropdownMenu(this,options,getOptionFont(), Color.WHITE);
        this.sendConfigChangeFlag = false;
        dropdownMenu.setSelectedIndex(1);
        this.sendConfigChangeFlag = true;
    }

    //更改下拉菜单的选项
    @Override
    public void changedSelectionTo(DropdownMenu dropdownMenu, int i, String s) {
        FakeEnding.ROW_NUM = i+4;
        //判断是否需要发送消息
        if (sendConfigChangeFlag) {
            SocketServer server = AutomaticSocketServer.getServer();
            this.sendConfigChange(server.streamHandle, i);
            server.send();
        }
    }

}
