package pvp_in_the_spire.ui.CardDesign;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pvp_in_the_spire.helpers.FontLibrary;
import pvp_in_the_spire.ui.AbstractPage;
import pvp_in_the_spire.ui.Button.DigitButton;
import pvp_in_the_spire.ui.TextLabel;

//能量选项
public class PowerItem extends AbstractPage {

    //在左边放power的图标
    PowerIcon powerIcon;
    //能量的文本
    TextLabel powerNameLabel;
    //用于控件增减的控件
    DigitButton digitButton;

    public PowerItem(AbstractPower power, float width)
    {
        //初始化width
        this.width = width;
        this.height = Settings.HEIGHT*0.04f;
        //记录能量的图标
        this.powerIcon = new PowerIcon(power, this.x + this.width*0.1f,
            this.y);
        //记录能量的文本
        this.powerNameLabel = new TextLabel(this.x + this.width*0.3f,
                this.y, this.width*0.2f, this.height, power.name,
                FontLibrary.getBaseFont());
        //初始化用于控制增减的控件
        this.digitButton = new DigitButton(this.x + this.width*0.55f,
            this.y);
    }

    @Override
    public void update() {
        this.powerNameLabel.update();
        this.digitButton.update();
    }

    @Override
    public void move(float xChange, float yChange) {
        super.move(xChange, yChange);
        this.powerNameLabel.move(xChange,yChange);
        this.powerIcon.move(xChange, yChange);
        this.digitButton.move(xChange,yChange);
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE);
        powerIcon.render(sb);
        this.powerNameLabel.render(sb);
        this.digitButton.render(sb);
    }
}
