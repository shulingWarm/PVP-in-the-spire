package UI.BattleUI;

import WarlordEmblem.patches.CardShowPatch.CardBox;
import WarlordEmblem.patches.CardShowPatch.CardRecorder;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

//这虽然叫card panel,但时而会同时包括卡牌信息和能量信息
public class BattleCardPanel {

    //用于显示手牌的box
    public CardBox cardBox;
    //敌人的能量框
    public MonsterEnergyPanel energyPanel;

    public BattleCardPanel(float xCenter,
        float yCenter, CardRecorder shownCards, AbstractMonster monster
    )
    {
        //生成card box
        this.cardBox = new CardBox(xCenter,yCenter,shownCards,monster);
        this.energyPanel = new MonsterEnergyPanel(xCenter- Settings.WIDTH*0.1f,
            yCenter - Settings.HEIGHT*0.15f);
    }

    public void render(SpriteBatch sb)
    {
        //直接渲染card box
        this.cardBox.render(sb);
        this.energyPanel.render(sb);
    }

    public void update()
    {
        this.energyPanel.update();
    }

    //设置能量
    public void setEnergy(int currEnergy)
    {
        this.energyPanel.setCurrentEnergy(currEnergy);
    }

}
