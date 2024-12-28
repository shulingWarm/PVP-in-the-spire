package pvp_in_the_spire.ui.ConfigSave;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import pvp_in_the_spire.helpers.FontLibrary;
import pvp_in_the_spire.ui.AbstractPage;
import pvp_in_the_spire.ui.Button.WithUpdate.BaseUpdateButton;
import pvp_in_the_spire.ui.Events.ClickCallback;
import pvp_in_the_spire.ui.Events.ClosePageEvent;
import pvp_in_the_spire.ui.Events.ConfigSaveCallback;
import pvp_in_the_spire.ui.InputBox;
import pvp_in_the_spire.ui.TextLabel;

//保存配置时，用于指定配置方案的名字
public class ConfigNameBox extends AbstractPage implements ClickCallback {

    public InputBox nameBox;
    public TextLabel title;
    public BaseUpdateButton confirmButton;
    public BaseUpdateButton cancelButton;
    //确定保存配置时的回调函数
    public ConfigSaveCallback configSaveCallback;
    //退出页面的回调函数
    public ClosePageEvent closePageEvent;

    //按钮的半径
    public static final float BUTTON_RAID = Settings.WIDTH * 0.05f;
    //按钮的中心点相对于中间位置的偏移量
    public static final float BUTTON_OFFSET = Settings.WIDTH * 0.1f;
    //按钮的高度
    public static final float BUTTON_Y = Settings.WIDTH * 0.2f;
    //按钮的高度
    public static final float BUTTON_HEIGHT = Settings.HEIGHT * 0.08f;

    public ConfigNameBox(ConfigSaveCallback configSaveCallback,
         ClosePageEvent closePageEvent)
    {
        this.nameBox = new InputBox(Settings.WIDTH*0.3f,
            Settings.HEIGHT*0.5f,Settings.WIDTH*0.4f,Settings.HEIGHT*0.04f,
            FontLibrary.getBaseFont());
        //这个open也只是临时的，正常使用的时候不必在这个地方open
        this.nameBox.open();
        //新建文本标题
        this.title = new TextLabel(Settings.WIDTH*0.4f,Settings.HEIGHT*0.6f,
                Settings.WIDTH*0.2f,Settings.HEIGHT*0.04f,"配置命名",
                FontLibrary.getFontWithSize(40));
        //初始化确定按钮
        this.confirmButton = new BaseUpdateButton(
                Settings.WIDTH * 0.5f - BUTTON_OFFSET - BUTTON_RAID,
                BUTTON_Y,
                BUTTON_RAID * 2,
                BUTTON_HEIGHT,
                "确定",
                FontLibrary.getBaseFont(),
                ImageMaster.PROFILE_SLOT,
                this
        );
        //初始化取消按钮
        this.cancelButton = new BaseUpdateButton(
                Settings.WIDTH * 0.5f + BUTTON_OFFSET - BUTTON_RAID,
                BUTTON_Y,
                BUTTON_RAID * 2,
                BUTTON_HEIGHT,
                "取消",
                FontLibrary.getBaseFont(),
                ImageMaster.PROFILE_SLOT,
                this
        );
        this.closePageEvent = closePageEvent;
        this.configSaveCallback = configSaveCallback;
    }

    @Override
    public void render(SpriteBatch sb) {
        this.title.render(sb);
        this.nameBox.render(sb);
        this.confirmButton.render(sb);
        this.cancelButton.render(sb);
    }

    @Override
    public void update() {
        this.title.update();
        this.nameBox.update();
        this.confirmButton.update();
        this.cancelButton.update();
    }

    @Override
    public void clickEvent(BaseUpdateButton button) {
        //对于确定按钮，调用保存配置信息的回调
        if(button == this.confirmButton)
        {
            if(this.configSaveCallback != null)
            {
                this.configSaveCallback.saveConfig(this.nameBox.textField);
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
