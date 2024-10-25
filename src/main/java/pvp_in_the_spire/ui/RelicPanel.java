package pvp_in_the_spire.ui;

import pvp_in_the_spire.AutomaticSocketServer;
import pvp_in_the_spire.SocketServer;
import pvp_in_the_spire.actions.FightProtocol;
import pvp_in_the_spire.character.ControlMoster;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

//在屏幕的最右边纵向显示遗物
public class RelicPanel extends BasePanel {

    //纵向的滚动条
    public ScrollBar scroll;

    //遗物的列表
    public ArrayList<AbstractRelic> relicList = new ArrayList<>();

    //用过的尾巴的名字
    public static final String usedTailName = "Lizard Tail_used";

    public RelicPanel()
    {
        super(Settings.WIDTH*0.935f, Settings.HEIGHT*0.1f,Settings.WIDTH*0.2f,Settings.HEIGHT*0.8f);
    }

    //寻找relic
    public AbstractRelic getRelic(String relicId)
    {
        for (AbstractRelic eachRelic : relicList)
        {
            if(eachRelic.relicId.equals(relicId))
                return eachRelic;
        }
        return null;
    }

    //这个渲染不再渲染滚动条
    public void render(SpriteBatch sb)
    {
        //渲染每个page
        //为了确保下拉菜单的情况下可以正常显示，显示的时候改成倒序
        for(int idPage=pageList.size()-1;idPage>=0;--idPage)
        {
            AbstractPage eachPage = pageList.get(idPage);
            //如果y超过下边界就不再显示了
            if(eachPage.y < this.y + this.height &&
                    eachPage.y > this.y)
            {
                eachPage.render(sb);
            }
        }
    }

    public static void relicInfoEncode(DataOutputStream streamHandle)
    {
        try
        {
            //发送数据头
            streamHandle.writeInt(FightProtocol.RELIC_LIST);
            //发送遗物的数量
            streamHandle.writeInt(AbstractDungeon.player.relics.size());
            //遍历当前角色的每个遗物
            for(AbstractRelic eachRelic : AbstractDungeon.player.relics)
            {
                //发送遗物名称
                streamHandle.writeUTF(eachRelic.relicId);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    //获取一个用过的尾巴
    public static AbstractRelic getUsedTailInstance()
    {
        LizardTail tempTail = new LizardTail();
        tempTail.setCounter(-2);
        return tempTail;
    }

    //接收敌方角色的遗物
    public static void relicInfoDecode(DataInputStream streamHandle)
    {
        try
        {
            //读取遗物的数量
            int relicNum = streamHandle.readInt();
            //需要添加的目标panel
            RelicPanel targetPanel = ControlMoster.instance.relicPanel;
            //依次读取每个遗物
            for(int idRelic=0;idRelic<relicNum;++idRelic)
            {
                //读取遗物的名称
                String relicName = streamHandle.readUTF();
                AbstractRelic tempRelic;
                //判断是不是用过的尾巴
                if(relicName.equals(usedTailName))
                {
                    tempRelic = getUsedTailInstance();
                }
                else {
                    tempRelic = RelicLibrary.getRelic(relicName).makeCopy();
                }
                targetPanel.addNewPage(new RelicPage(tempRelic));
                targetPanel.relicList.add(tempRelic);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //发送我方的遗物
    public static void sendMyRelic()
    {
        SocketServer server = AutomaticSocketServer.getServer();
        relicInfoEncode(server.streamHandle);
        server.send();
    }

}
