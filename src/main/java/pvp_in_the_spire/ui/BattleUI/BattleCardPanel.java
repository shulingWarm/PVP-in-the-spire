package pvp_in_the_spire.ui.BattleUI;

import pvp_in_the_spire.patches.CardShowPatch.CardBox;
import pvp_in_the_spire.patches.CardShowPatch.CardRecorder;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.ArrayList;

//这虽然叫card panel,但时而会同时包括卡牌信息和能量信息
public class BattleCardPanel {

    //用于显示手牌的box
    public CardBox cardBox;
    //敌人的能量框
    public MonsterEnergyPanel energyPanel;
    //遗物列表
    public MonsterRelicPanel monsterRelicPanel;
    //玩家的药水列表
    public MonsterPotionPanel potionPanel;

    public BattleCardPanel(float xCenter,
        float yCenter, CardRecorder shownCards, AbstractMonster monster
    )
    {
        //生成card box
        this.cardBox = new CardBox(xCenter,yCenter,shownCards,monster);
        this.energyPanel = new MonsterEnergyPanel(xCenter- Settings.WIDTH*0.1f,
            yCenter - Settings.HEIGHT*0.15f);
        //初始化遗物列表
        this.monsterRelicPanel = new MonsterRelicPanel();
        //更新玩家的药水列表
        this.potionPanel = new MonsterPotionPanel();
    }

    public void render(SpriteBatch sb)
    {
        //直接渲染card box
        this.cardBox.render(sb);
        this.energyPanel.render(sb);
        this.monsterRelicPanel.render(sb);
        this.potionPanel.render(sb);
    }

    public void update()
    {
        this.energyPanel.update();
        this.monsterRelicPanel.update();
        this.potionPanel.update();
    }

    //设置能量
    public void setEnergy(int currEnergy)
    {
        this.energyPanel.setCurrentEnergy(currEnergy);
    }

    //初始化玩家的遗物列表
    public void initRelicList(ArrayList<AbstractRelic> relicList)
    {
        monsterRelicPanel.clearPanel();
        for(AbstractRelic eachRelic : relicList)
        {
            monsterRelicPanel.addRelic(eachRelic);
        }
    }

}
