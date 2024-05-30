package UI;

import WarlordEmblem.GlobalManager;
import WarlordEmblem.helpers.FontLibrary;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.codedisaster.steamworks.SteamUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.helpers.input.ScrollInputProcessor;
import com.megacrit.cardcrawl.helpers.steamInput.SteamInputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.mainMenu.SaveSlotScreen;
import com.megacrit.cardcrawl.ui.panels.RenamePopup;
import com.megacrit.cardcrawl.ui.panels.SeedPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//通用的输入框
//最开始的时候是用重命名存档的方式来弄的
public class InputBox extends AbstractPage implements InputProcessor {

    private static final Logger logger = LogManager.getLogger(RenamePopup.class.getName());
    private boolean shown = false;
    public String textField = "";
    private Color faderColor;
    private Color uiColor;
    private float waitTimer;
    //文本相对于page起始位置的偏移量
    public static final float X_OFFSET = Settings.WIDTH * 0.01f;
    //光标的x偏移位置
    public static final float X_CURSOR = Settings.WIDTH * -0.0025f;
    public static final float Y_OFFSET = Settings.HEIGHT * 0.02f;

    //输入框使用的字体
    public BitmapFont font;

    //正常的始终输入框
    //这个构造函数需要指定明确的字体
    public InputBox(float x,float y,float width,float height,
                    BitmapFont font)
    {
        //记录page的位置
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.faderColor = new Color(0.0F, 0.0F, 0.0F, 0.0F);
        this.uiColor = new Color(1.0F, 0.965F, 0.886F, 0.0F);
        this.waitTimer = 0.0F;
        //记录字体
        this.font = font;
    }

    public InputBox(float x,float y,float width,float height)
    {
        this(x,y,width,height, FontLibrary.getBaseFont());
    }

    public void update() {
        if (GlobalManager.activateBox == this && Gdx.input.isKeyPressed(67) && !textField.isEmpty() && this.waitTimer <= 0.0F) {
            textField = textField.substring(0, textField.length() - 1);
            this.waitTimer = 0.09F;
        }

        if (this.waitTimer > 0.0F) {
            this.waitTimer -= Gdx.graphics.getDeltaTime();
        }

        if (this.shown) {
            this.faderColor.a = MathHelper.fadeLerpSnap(this.faderColor.a, 0.75F);
            this.uiColor.a = MathHelper.fadeLerpSnap(this.uiColor.a, 1.0F);
        } else {
            this.faderColor.a = MathHelper.fadeLerpSnap(this.faderColor.a, 0.0F);
            this.uiColor.a = MathHelper.fadeLerpSnap(this.uiColor.a, 0.0F);
        }

    }

    public void render(SpriteBatch sb) {
        sb.setColor(this.faderColor);
        this.renderTextbox(sb);
    }

    private void renderTextbox(SpriteBatch sb) {
        //sb.draw(ImageMaster.RENAME_BOX, (float)Settings.WIDTH / 2.0F - 160.0F, Settings.OPTION_Y + 20.0F * Settings.scale - 160.0F, 160.0F, 160.0F, 320.0F, 320.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 320, 320, false, false);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, this.x, this.y-this.height, this.width,this.height*2);
        FontHelper.renderSmartText(sb, this.font, textField,
            this.x + X_OFFSET,
            this.y + Y_OFFSET, 100000.0F, 0.0F, this.uiColor, 1);
        float tmpAlpha = (MathUtils.cosDeg((float)(System.currentTimeMillis() / 3L % 360L)) + 1.25F) / 3.0F * this.uiColor.a;
        //需要先判断它是不是被激活了的输入框
        if(GlobalManager.activateBox == this)
            FontHelper.renderSmartText(sb, this.font, "_", this.x + FontHelper.getSmartWidth(this.font, textField, 1000000.0F, 0.0F, 1) + X_CURSOR,
                this.y + Y_OFFSET, 100000.0F, 0.0F, new Color(1.0F, 1.0F, 1.0F, tmpAlpha));
    }

    public void open() {
        Gdx.input.setInputProcessor(this);
        if (SteamInputHelper.numControllers == 1 && CardCrawlGame.clientUtils != null && CardCrawlGame.clientUtils.isSteamRunningOnSteamDeck()) {
            CardCrawlGame.clientUtils.showFloatingGamepadTextInput(SteamUtils.FloatingGamepadTextInputMode.ModeSingleLine, 0, 0, Settings.WIDTH, (int)((float)Settings.HEIGHT * 0.25F));
        }
        this.shown = true;
        //处理当前的激活输入框
        GlobalManager.activateBox = this;
    }

    @Override
    public boolean keyDown(int i) {
        return false;
    }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

    //用户输入的基本逻辑，用户每按下一个按键，在这里就会有对应
    @Override
    public boolean keyTyped(char character) {
        String charStr = String.valueOf(character);
        logger.info(charStr);
        if (charStr.length() != 1) {
            return false;
        } else {

            //判断是不是退格或者换行之类的操作
            if(character == '\n' || character == '\b' ||
                    (!Character.isDigit(character) &&
                    !Character.isLetter(character))
            ){
                return false;
            }
            textField = textField + charStr;

            return true;
        }
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(int i) {
        return false;
    }
}
