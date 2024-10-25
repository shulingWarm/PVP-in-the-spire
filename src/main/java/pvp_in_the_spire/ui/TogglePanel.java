package pvp_in_the_spire.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.options.ToggleButton;

//在这里面会放很多多选按钮，这样可以方便添加一些地主增益选项
public class TogglePanel extends AbstractPage {

    public ToggleButton toggleButton;

    public TogglePanel()
    {
        //添加一个toggle的按钮
        toggleButton = new ToggleButton(Settings.WIDTH *0.5f,
                Settings.HEIGHT*0.5f,Settings.WIDTH*0.2f,
                ToggleButton.ToggleBtnType.AMBIENCE_ON);
    }

    @Override
    public void render(SpriteBatch sb) {
        toggleButton.render(sb);
    }

    @Override
    public void update() {
        toggleButton.update();
    }
}
