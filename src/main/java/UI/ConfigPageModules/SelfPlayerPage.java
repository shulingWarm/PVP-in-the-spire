package UI.ConfigPageModules;

import UI.Button.ReadyButton;
import UI.Button.ReadyButtonCallback;
import WarlordEmblem.Events.ConfigReadyEvent;
import WarlordEmblem.GlobalManager;
import WarlordEmblem.PVPApi.Communication;
import WarlordEmblem.helpers.FontLibrary;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;

//用来渲染本地玩家的界面
public class SelfPlayerPage extends CharacterConfigPage
implements ReadyButtonCallback{

    //用于更新准备状态的按钮
    public ReadyButton readyButton;

    public SelfPlayerPage()
    {
        super(Settings.WIDTH * 0.15f,Settings.HEIGHT*0.35f);
        //自身的人物不需要重置大小
        this.resetScaleFlag = false;
        float tempWidth = this.width*0.7f;
        //初始化准备按钮
        this.readyButton = new ReadyButton(this.x + this.width/2 - tempWidth/2,
                this.y + this.height * 0.1f,tempWidth,this.height*0.2f,
                FontLibrary.getBaseFont());
        this.readyButton.readyButtonCallback = this;
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
    }

    @Override
    public void move(float xChange, float yChange) {
        super.move(xChange, yChange);
        readyButton.move(xChange,yChange);
    }

    @Override
    public void pressReady(boolean readyFlag) {
        //发送准备的消息
        Communication.sendEvent(new ConfigReadyEvent(readyFlag));
        //更新准备信息
        GlobalManager.playerManager.updateReadyFlag(
            GlobalManager.playerManager.selfPlayerInfo,readyFlag
        );
    }
}