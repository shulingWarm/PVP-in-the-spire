package pvp_in_the_spire.ui.ConfigPageModules;

import pvp_in_the_spire.ui.AbstractPage;
import pvp_in_the_spire.ui.GridPanel;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;

//角色的panel,关于两个角色框在两边渲染的页面都是由这里来处理的
public class CharacterPanel extends AbstractPage {

    //左边的角色网格
    public GridPanel leftCharacters;
    //右边的网格
    public GridPanel rightCharacters;
    //主显示位
    public AbstractPage mainCharacter;

    //测试用的初始化过程
    public void testInit()
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

    public CharacterPanel()
    {
        //初始化左边的角色网格
        this.leftCharacters = new GridPanel(
                2,CharacterConfigPage.WIDTH,
                CharacterConfigPage.HEIGHT,
                Settings.WIDTH * 0.06f,
                Settings.HEIGHT * 0.59f
        );
        //右边的角色网格
        this.rightCharacters = new GridPanel(
                leftCharacters.gridWidth,CharacterConfigPage.WIDTH,
                CharacterConfigPage.HEIGHT,
                Settings.WIDTH - leftCharacters.x - leftCharacters.gridWidth* leftCharacters.cellWidth,
                Settings.HEIGHT * 0.93f
        );
    }

    //设置主位页面
    public void setMainCharacter(AbstractPage mainCharacter) {
        this.mainCharacter = mainCharacter;
        mainCharacter.moveTo(
                leftCharacters.x + leftCharacters.cellWidth -
                        mainCharacter.width/2,
                leftCharacters.y
        );
    }

    @Override
    public void render(SpriteBatch sb) {
        leftCharacters.render(sb);
        rightCharacters.render(sb);
        if(mainCharacter != null)
            mainCharacter.render(sb);
    }

    @Override
    public void update() {
        leftCharacters.update();
        rightCharacters.update();
        if(mainCharacter != null)
            mainCharacter.update();
    }
}
