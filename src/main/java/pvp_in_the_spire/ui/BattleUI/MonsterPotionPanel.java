package pvp_in_the_spire.ui.BattleUI;

import pvp_in_the_spire.patches.RenderPatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.potions.AbstractPotion;

import java.util.ArrayList;

//敌方玩家的药水列表
public class MonsterPotionPanel {

    //需要显示的药水列表
    public ArrayList<AbstractPotion> potionList = new ArrayList<>();
    //x的起始位置
    public static final float X_BEGIN = Settings.WIDTH * 0.67f;
    public static final float X_STEP = Settings.WIDTH * 0.03f;
    public static final float Y_BEGIN = Settings.HEIGHT * 0.97f;

    //更新药水内容
    public void updatePotion(ArrayList<AbstractPotion> potionList)
    {
        this.potionList = potionList;
        for(int idPotion=0;idPotion<potionList.size();++idPotion)
        {
            AbstractPotion tempPotion = potionList.get(idPotion);
            tempPotion.posX = X_BEGIN + X_STEP * idPotion;
            tempPotion.posY = Y_BEGIN;
            tempPotion.hb.move(tempPotion.posX,tempPotion.posY);
            tempPotion.isObtained = true;
        }
    }

    //药水交互效果的更新
    public void update()
    {
        for(AbstractPotion eachPotion : potionList)
        {
            eachPotion.update();
        }
    }

    public void tipRender(SpriteBatch sb)
    {
        for(AbstractPotion eachPotion : potionList)
        {
            eachPotion.shopRender(sb);
        }
    }


    //对每个药水的渲染
    public void render(SpriteBatch sb)
    {
        RenderPatch.potionPanel = this;
    }

}
