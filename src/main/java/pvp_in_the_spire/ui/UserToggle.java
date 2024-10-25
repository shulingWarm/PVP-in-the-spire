package pvp_in_the_spire.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.options.ToggleButton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//由用户控制的toggle
public class UserToggle extends AbstractPage {

    private static final Logger logger = LogManager.getLogger(ToggleButton.class.getName());
    private static final int W = 32;
    public Hitbox hb;
    public boolean enabled = true;
    ToggleInterface toggleInterface;
    //有时候回调函数可能会需要一个id
    int idToggle = 0;

    public UserToggle(float x, float y,
              ToggleInterface toggleInterface,int idToggle) {
        this.x = x;
        this.y = y;
        this.hb = new Hitbox(200.0F * Settings.scale, 32.0F * Settings.scale);
        this.hb.move(x - 74.0F * Settings.scale, this.y);
        this.toggleInterface = toggleInterface;
        this.idToggle = idToggle;
    }

    public UserToggle(float x, float y, ToggleInterface toggleInterface,
          boolean isLarge) {
        this.x = x;
        this.y = y;
        if (isLarge) {
            this.hb = new Hitbox(480.0F * Settings.scale, 32.0F * Settings.scale);
            this.hb.move(x + 214.0F * Settings.scale, this.y);
        } else {
            this.hb = new Hitbox(240.0F * Settings.scale, 32.0F * Settings.scale);
            this.hb.move(x + 74.0F * Settings.scale, this.y);
        }
        this.toggleInterface = toggleInterface;
    }

    public void update() {
        this.hb.update();
        if (this.hb.hovered && (InputHelper.justClickedLeft || CInputActionSet.select.isJustPressed())) {
            InputHelper.justClickedLeft = false;
            this.toggle();
        }

    }

    @Override
    public void move(float xChange, float yChange) {
        super.move(xChange, yChange);
        this.hb.move(x - 74.0F * Settings.scale, this.y);
    }

    public void toggle() {
        this.enabled = !this.enabled;
        //调用按钮触发时的回调
        this.toggleInterface.triggerToggleButton(this,this.idToggle,this.enabled);
    }

    public void render(SpriteBatch sb) {
        if (this.enabled) {
            sb.setColor(Color.LIGHT_GRAY);
        } else {
            sb.setColor(Color.WHITE);
        }

        float scale = Settings.scale;
        if (this.hb.hovered) {
            sb.setColor(Color.SKY);
            scale = Settings.scale * 1.25F;
        }

        sb.draw(ImageMaster.OPTION_TOGGLE, this.x - 16.0F, this.y - 16.0F, 16.0F, 16.0F, 32.0F, 32.0F, scale, scale, 0.0F, 0, 0, 32, 32, false, false);
        if (this.enabled) {
            sb.setColor(Color.WHITE);
            sb.draw(ImageMaster.OPTION_TOGGLE_ON, this.x - 16.0F, this.y - 16.0F, 16.0F, 16.0F, 32.0F, 32.0F, scale, scale, 0.0F, 0, 0, 32, 32, false, false);
        }

        this.hb.render(sb);
    }

}
