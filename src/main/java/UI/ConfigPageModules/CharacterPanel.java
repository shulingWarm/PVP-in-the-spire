package UI.ConfigPageModules;

import UI.AbstractPage;
import UI.GridPanel;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;

//角色的panel,关于两个角色框在两边渲染的页面都是由这里来处理的
public class CharacterPanel extends AbstractPage {

    //左边的角色网格
    public GridPanel leftCharacters;
    //右边的网格
    public GridPanel rightCharacters;

    public CharacterPanel()
    {
        //初始化左边的角色网格
        this.leftCharacters = new GridPanel(
            2,CharacterConfigPage.WIDTH,
                CharacterConfigPage.HEIGHT,
                Settings.WIDTH * 0.06f,
                Settings.HEIGHT * 0.93f
        );
        //先随便往里面添加几个角色
        for(int i=0;i<4;++i)
            this.leftCharacters.addPage(
                new CharacterConfigPage()
            );
        //右边的角色网格
        this.rightCharacters = new GridPanel(
                leftCharacters.gridWidth,CharacterConfigPage.WIDTH,
                CharacterConfigPage.HEIGHT,
                Settings.WIDTH - leftCharacters.x - leftCharacters.gridWidth* leftCharacters.cellWidth,
                leftCharacters.y
        );
        //先随便往里面添加几个角色
        for(int i=0;i<4;++i)
        {
            CharacterConfigPage tempPage = new CharacterConfigPage();
            tempPage.setHorizontalFlip(true);
            this.rightCharacters.addPage(
                tempPage
            );
        }

    }

    @Override
    public void render(SpriteBatch sb) {
        leftCharacters.render(sb);
        rightCharacters.render(sb);
    }

    @Override
    public void update() {
        leftCharacters.update();
        rightCharacters.update();
    }
}
