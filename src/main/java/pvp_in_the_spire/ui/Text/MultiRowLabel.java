package pvp_in_the_spire.ui.Text;

import pvp_in_the_spire.ui.AbstractPage;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.FontHelper;

//多行文本显示的标志符
public class MultiRowLabel extends AbstractPage {

    //文本管理器
    public AdvTextManager manager;
    //每一行的高度
    public float lineHeight;
    //渲染时的颜色
    public Color color;

    //构造的时候传入一个多行文本管理器
    public MultiRowLabel(AdvTextManager textManager,
                         float x,float y,
                         Color color
    )
    {
        textManager.freeze();
        manager = textManager;
        this.width = textManager.width;
        //获取数据高度
        this.lineHeight = manager.getTextHeight(manager.getLastLine()) * 1.2f;
        this.height = lineHeight * (manager.getLineNum() + 1);
        this.x = x;
        this.y = y;
        this.color = color;
    }

    @Override
    public void render(SpriteBatch sb) {
        //渲染每一行文本
        for(int idLine=0;idLine< manager.getLineNum();++idLine)
        {
            FontHelper.renderFontLeft(sb,manager.font,
                    manager.getStr(idLine),
                    x,
                    y+(0.5f + manager.getLineNum() - idLine)*
                    this.lineHeight, color);
        }
    }
}
