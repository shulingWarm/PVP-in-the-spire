package pvp_in_the_spire.ui.ConfigPageModules;

import pvp_in_the_spire.ui.Button.ChangeSideButton;
import pvp_in_the_spire.ui.Button.ChangeSideCallback;
import pvp_in_the_spire.ui.Button.ReadyButton;
import pvp_in_the_spire.ui.Button.ReadyButtonCallback;
import pvp_in_the_spire.ui.Button.WithUpdate.BaseUpdateButton;
import pvp_in_the_spire.ui.Events.ClickCallback;
import pvp_in_the_spire.events.ConfigReadyEvent;
import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.Communication;
import pvp_in_the_spire.helpers.FontLibrary;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;

//用来渲染本地玩家的界面
public class SelfPlayerPage extends CharacterConfigPage
implements ReadyButtonCallback, ChangeSideCallback, ClickCallback {

    //用于更新准备状态的按钮
    public ReadyButton readyButton;
    //用于换边的按钮
    public ChangeSideButton changeSideButton;
    //切换角色的按钮
    public BaseUpdateButton leftButton;
    public BaseUpdateButton rightButton;

    public SelfPlayerPage()
    {
        super(Settings.WIDTH * 0.15f,Settings.HEIGHT*0.35f);
        //自身的人物不需要重置大小
        this.resetScaleFlag = false;
        float tempWidth = this.width*0.7f;
        //初始化准备按钮
        this.readyButton = new ReadyButton(this.x + this.width/4 - tempWidth/2,
                this.y + this.height * 0.1f,tempWidth,this.height*0.2f,
                FontLibrary.getBaseFont());
        //初始化换边按钮
        this.changeSideButton = new ChangeSideButton(
            this.x + this.width*0.75f - tempWidth/2,
            this.y + this.height * 0.1f,tempWidth,this.height*0.2f,
            FontLibrary.getBaseFont(),this
        );
        this.readyButton.readyButtonCallback = this;
        //初始化切换角色的按钮
        this.leftButton = new BaseUpdateButton(
            this.x + this.width * 0.05f,this.y + this.height * 0.83f,
                this.width*0.25f,this.height*0.2f,
                "",FontLibrary.getBaseFont(), ImageMaster.CF_LEFT_ARROW,this
        );
        this.rightButton = new BaseUpdateButton(
            this.width - (leftButton.x - this.x) - leftButton.width + this.x,
                leftButton.y,
                leftButton.width, leftButton.height, leftButton.text,
                FontLibrary.getBaseFont(),ImageMaster.CF_RIGHT_ARROW,this
        );
    }

    @Override
    public float getCharacterBoxY() {
        return this.height * 0.32f + this.y;
    }

    @Override
    public void render(SpriteBatch sb) {
        this.plainBox.render(sb);
        if(characterBox!=null)
        {
            characterBox.render(sb);
            this.nameLabel.render(sb);
            this.versionText.render(sb);
        }
        //渲染准备按钮
        this.readyButton.render(sb);
        //渲染换边用的按钮
        this.changeSideButton.render(sb);
        this.leftButton.render(sb);
        this.rightButton.render(sb);
    }

    @Override
    public void update() {
        super.update();
        this.leftButton.update();
        this.rightButton.update();
    }

    @Override
    public void move(float xChange, float yChange) {
        super.move(xChange, yChange);
        readyButton.move(xChange,yChange);
        changeSideButton.move(xChange,yChange);
        this.leftButton.move(xChange,yChange);
        this.rightButton.move(xChange,yChange);
    }

    @Override
    public void pressReady(boolean readyFlag) {
        //准备状态下禁止再换边
        changeSideButton.disabled = readyFlag;
        //发送准备的消息
        Communication.sendEvent(new ConfigReadyEvent(readyFlag));
        //更新准备信息
        GlobalManager.playerManager.updateReadyFlag(
            GlobalManager.playerManager.selfPlayerInfo,readyFlag
        );
    }

    @Override
    public void setReady(boolean newStage) {
        super.setReady(newStage);
        System.out.println("Self player page setReady!!");
        this.readyButton.setReadyFlag(newStage);
        changeSideButton.disabled = newStage;
    }

    @Override
    public void changeSideTrigger() {
        //调用global manager执行换边操作
        GlobalManager.playerManager.changeTeam();
    }

    @Override
    public void clickEvent(BaseUpdateButton button) {
        //判断是左边还是右边
        if(button == leftButton)
        {
            GlobalManager.playerManager.selfPlayerInfo.changeCharacter(-1);
        }
        else if(button == rightButton)
        {
            GlobalManager.playerManager.selfPlayerInfo.changeCharacter(1);
        }
    }

    @Override
    public void setOwnerUI(boolean isOwner) {
        super.setOwnerUI(isOwner);
        this.allowReady(!isOwner);
    }

    //设置为允许准备
    public void allowReady(boolean allowFlag)
    {
        if(allowFlag)
            this.readyButton.disabled = false;
        else {
            this.readyButton.setReadyFlag(false);
            this.readyButton.disabled = true;
        }
    }
}
