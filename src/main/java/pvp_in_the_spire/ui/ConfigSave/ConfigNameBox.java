package pvp_in_the_spire.ui.ConfigSave;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;
import org.lwjgl.Sys;
import pvp_in_the_spire.helpers.FontLibrary;
import pvp_in_the_spire.screens.WarningText;
import pvp_in_the_spire.ui.*;
import pvp_in_the_spire.ui.Button.LoadConfigButton;
import pvp_in_the_spire.ui.Button.WithUpdate.BaseUpdateButton;
import pvp_in_the_spire.ui.Events.ClickCallback;
import pvp_in_the_spire.ui.Events.ClosePageEvent;
import pvp_in_the_spire.ui.Events.ConfigSaveCallback;
import pvp_in_the_spire.ui.Events.LoadConfigCallback;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

//保存配置时，用于指定配置方案的名字
public class ConfigNameBox extends AbstractPage implements ClickCallback{

    public static final UIStrings uiStrings =
            CardCrawlGame.languagePack.getUIString("ConfigNameBox");

    public InputBox nameBox;
    public TextLabel title;
    public BaseUpdateButton confirmButton;
    public BaseUpdateButton cancelButton;
    //确定保存配置时的回调函数
    public ConfigSaveCallback configSaveCallback;
    //退出页面的回调函数
    public ClosePageEvent closePageEvent;
    //用于显示界面的背景框
    public PlainBox background;
    //显示名字列表的背景
    public PlainBox panelBackground;
    //用于显示已经保存的配置列表
    public BasePanel configPanel;

    //按钮的半径
    public static final float BUTTON_RAID = Settings.WIDTH * 0.05f;
    //按钮的中心点相对于中间位置的偏移量
    public static final float BUTTON_OFFSET = Settings.WIDTH * 0.1f;
    //按钮的高度
    public static final float BUTTON_Y = Settings.HEIGHT * 0.56f;
    //按钮的高度
    public static final float BUTTON_HEIGHT = Settings.HEIGHT * 0.08f;

    public ConfigNameBox(ConfigSaveCallback configSaveCallback,
         ClosePageEvent closePageEvent
    )
    {
        this.nameBox = new InputBox(Settings.WIDTH*0.3f,
            Settings.HEIGHT*0.7f,Settings.WIDTH*0.4f,Settings.HEIGHT*0.04f,
            FontLibrary.getBaseFont());
        //新建文本标题
        this.title = new TextLabel(Settings.WIDTH*0.4f,Settings.HEIGHT*0.8f,
                Settings.WIDTH*0.2f,Settings.HEIGHT*0.04f,uiStrings.TEXT[0],
                FontLibrary.getFontWithSize(40));
        //初始化背景框
        this.background = new PlainBox(Settings.WIDTH*0.6f,
                Settings.HEIGHT*0.98f, Color.valueOf("111111EE"));
        this.background.x = Settings.WIDTH * 0.2f;
        this.background.y = 0;
        this.background.texture = ImageMaster.WHITE_SQUARE_IMG;
        //用于放列表后面的背景
        this.panelBackground = new PlainBox(Settings.WIDTH*0.4f,Settings.HEIGHT*0.46f,Color.GRAY);
        this.panelBackground.x = Settings.WIDTH*0.3f;
        this.panelBackground.y = Settings.HEIGHT*0.04f;
        //初始化确定按钮
        this.confirmButton = new BaseUpdateButton(
                Settings.WIDTH * 0.5f - BUTTON_OFFSET - BUTTON_RAID,
                BUTTON_Y,
                BUTTON_RAID * 2,
                BUTTON_HEIGHT,
                uiStrings.TEXT[1],
                FontLibrary.getBaseFont(),
                ImageMaster.PROFILE_SLOT,
                this
        );
        //初始化panel列表
        this.configPanel = new BasePanel(Settings.WIDTH*0.3f,
                Settings.HEIGHT*0.04f,
                Settings.WIDTH*0.4f,Settings.HEIGHT*0.46f);
        //初始化取消按钮
        this.cancelButton = new BaseUpdateButton(
                Settings.WIDTH * 0.5f + BUTTON_OFFSET - BUTTON_RAID,
                BUTTON_Y,
                BUTTON_RAID * 2,
                BUTTON_HEIGHT,
                uiStrings.TEXT[2],
                FontLibrary.getBaseFont(),
                ImageMaster.PROFILE_SLOT,
                this
        );
        this.closePageEvent = closePageEvent;
        this.configSaveCallback = configSaveCallback;
    }

    @Override
    public void render(SpriteBatch sb) {
        this.background.render(sb);
        this.panelBackground.render(sb);
        this.title.render(sb);
        this.nameBox.render(sb);
        this.confirmButton.render(sb);
        this.cancelButton.render(sb);
        this.configPanel.render(sb);
    }

    @Override
    public void update() {
        this.title.update();
        this.nameBox.update();
        this.confirmButton.update();
        this.cancelButton.update();
        this.configPanel.update();
    }

    //检查目录里面所有的配置文件
    public static ArrayList<String> scanConfigFiles()
    {
        //初始化用于返回的config文件
        ArrayList<String> configFiles = new ArrayList<>();
        //用当前目录初始化path
        Path currDir = Paths.get(".");
        //遍历所有可能的文件
        // 使用try-with-resources确保资源正确关闭
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(currDir,"*.pvpcfg"))
        {
            //打印stream里面的每个path
            for(Path eachPath : stream)
            {
                //去掉当前path里面的后缀名
                String fileName = eachPath.getFileName().toString();
                //去掉后缀名
                configFiles.add(fileName.substring(0,fileName.lastIndexOf('.')));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return configFiles;
    }

    @Override
    public void open() {
        this.nameBox.open();
        //加载所有的配置信息
        ArrayList<String> paths = scanConfigFiles();
        //把各种配置添加到panel里面
        for(String eachPath : paths)
        {
            //如果名字是空的就不管
            if(eachPath.isEmpty())
                continue;
            //新建加载配置的按钮
            LoadConfigButton tempButton = new LoadConfigButton(
                0,0,this.configPanel.width*0.9f,
                    Settings.HEIGHT*0.06f,eachPath,FontLibrary.getBaseFont(),this.configSaveCallback
            );
            //把按钮添加到panel里面
            this.configPanel.addNewPage(tempButton);
        }
    }

    @Override
    public void close() {
        this.nameBox.close();
    }

    @Override
    public void clickEvent(BaseUpdateButton button) {
        //对于确定按钮，调用保存配置信息的回调
        if(button == this.confirmButton)
        {
            if(this.configSaveCallback != null)
            {
                //判断配置名称是不是空的
                if(this.nameBox.textField.isEmpty())
                {
                    return;
                }
                this.configSaveCallback.saveConfig(this.nameBox.textField);
                //同时也调用退出操作
                if(this.closePageEvent != null)
                {
                    this.closePageEvent.closePageEvent(this);
                }
            }
        }
        else if(button == this.cancelButton)
        {
            if(this.closePageEvent != null)
            {
                this.closePageEvent.closePageEvent(this);
            }
        }
    }
}
