package UI.BattleUI;

import WarlordEmblem.patches.CardShowPatch.CardBox;
import WarlordEmblem.patches.CardShowPatch.CardRecorder;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

//这虽然叫card panel,但时而会同时包括卡牌信息和能量信息
public class BattleCardPanel {

    //用于显示手牌的box
    public CardBox cardBox;

    public BattleCardPanel(float xCenter,
        float yCenter, CardRecorder shownCards, AbstractMonster monster
    )
    {
        //生成card box
        this.cardBox = new CardBox(xCenter,yCenter,shownCards,monster);
    }

    public void render(SpriteBatch sb)
    {
        //直接渲染card box
        this.cardBox.render(sb);
    }


}
