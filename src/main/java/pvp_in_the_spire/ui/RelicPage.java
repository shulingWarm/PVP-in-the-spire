package pvp_in_the_spire.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.relics.AbstractRelic;

//单独显示一个遗物的区块
public class RelicPage extends AbstractPage {

    public AbstractRelic relic;

    public RelicPage(AbstractRelic relic)
    {
        this.width = AbstractRelic.PAD_X;
        this.height = AbstractRelic.PAD_X;
        this.relic = relic;
        this.relic.isObtained = true;
        this.relic.isDone = true;
        this.relic.isSeen = false;
    }

    @Override
    public void move(float xChange, float yChange) {
        super.move(xChange,yChange);
        //移动遗物
        relic.currentX = this.x;
        relic.currentY = this.y;
        relic.hb.move(this.x,this.y);
    }

    @Override
    public void update() {
        relic.update();
    }

    @Override
    public void render(SpriteBatch sb) {
        relic.renderInTopPanel(sb);
        if(relic.hb.hovered)
        {
            relic.renderTip(sb);
        }
    }
}
