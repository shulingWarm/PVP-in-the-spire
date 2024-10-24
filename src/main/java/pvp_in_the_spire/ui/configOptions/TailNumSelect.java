package pvp_in_the_spire.ui.configOptions;

import pvp_in_the_spire.ui.TextLabel;
import pvp_in_the_spire.AutomaticSocketServer;
import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.SocketServer;
import pvp_in_the_spire.helpers.FontLibrary;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.options.DropdownMenu;
import com.megacrit.cardcrawl.screens.options.DropdownMenuListener;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

//选择开局给的尾巴个数
public class TailNumSelect extends AbstractConfigOption implements DropdownMenuListener {

    public static final UIStrings uiStrings =
            CardCrawlGame.languagePack.getUIString("TailNumSelect");

    //显示文本的接口
    TextLabel textLabel;

    public TailNumSelect(float width)
    {
        //初始化目前的位置
        this.x = 0;
        this.y = 0;
        this.width = width;
        this.height = Settings.HEIGHT * 0.06f;
        //初始化用于显示的文本
        textLabel=new TextLabel(x,y,this.width * 0.8f,this.height,
            uiStrings.TEXT[0], FontLibrary.getFontWithSize(24));
        //把文本设置成左对齐
        textLabel.isLeftAlign = true;
        initOptions();
    }

    public void initOptions()
    {
        //初始化尾巴的下拉菜单
        ArrayList<String> options = new ArrayList<>();
        for(int idRow=0;idRow<=10;++idRow)
            options.add(String.valueOf(idRow));
        dropdownMenu = new DropdownMenu(this,options,FontLibrary.getFontWithSize(24), Color.WHITE);
        this.sendConfigChangeFlag = false;
        dropdownMenu.setSelectedIndex(GlobalManager.beginTailNum);
        this.sendConfigChangeFlag = true;
    }

    //更改下拉菜单的选项
    @Override
    public void changedSelectionTo(DropdownMenu dropdownMenu, int i, String s) {
        //更新全局变量里面的尾巴数量
        GlobalManager.beginTailNum = i;
        //判断是否需要发送消息
        if(sendConfigChangeFlag)
        {
            SocketServer server = AutomaticSocketServer.getServer();
            this.sendConfigChange(server.streamHandle,i);
            server.send();
        }
    }

    @Override
    public void receiveConfigChange(DataInputStream streamHandle) {
        try
        {
            //读取新选择的尾巴数量
            int newTailNum = streamHandle.readInt();
            //更新尾巴的数量
            sendConfigChangeFlag = false;
            dropdownMenu.setSelectedIndex(newTailNum);
            sendConfigChangeFlag = true;
            //高亮这个东西
            this.highlight();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void move(float xChange, float yChange) {
        super.move(xChange,yChange);
        textLabel.move(xChange,yChange);
    }

    //渲染下拉菜单
    public void renderMenu(SpriteBatch sb)
    {
        dropdownMenu.render(sb,this.x + this.width * 0.62f,this.y + this.height*0.5f);
    }

    @Override
    public void update() {
        //如果目前不可交互的话，就禁止在这里更新
        if(!this.enableFlag)
            return;
        //更新下拉菜单
        dropdownMenu.update();
    }

    //渲染文本和选择框
    @Override
    public void render(SpriteBatch sb) {
        renderMenu(sb);
        textLabel.copyLocation(this);
        textLabel.render(sb);
        //最后执行父类的效果
        super.render(sb);
    }
}
