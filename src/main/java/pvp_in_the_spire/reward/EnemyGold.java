package pvp_in_the_spire.reward;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;

//敌人掉落的金钱，所有的逻辑都和gold一样，只是改个描述
public class EnemyGold extends RewardItem {

    public EnemyGold(int goldAmount)
    {
        super(goldAmount);
        //但需要特别地把种类改成遗物
        this.type = RewardType.STOLEN_GOLD;
        this.text = this.goldAmt + " 敌人掉落的金钱";
    }

    public void incrementGold(int gold) {
        this.goldAmt+=gold;
    }

    public boolean claimReward() {
        CardCrawlGame.sound.play("GOLD_GAIN");
        if (this.bonusGold == 0) {
            AbstractDungeon.player.gainGold(this.goldAmt);
        } else {
            AbstractDungeon.player.gainGold(this.goldAmt + this.bonusGold);
        }
        return true;
    }

    public void move(float y) {
        this.y = y;
        this.hb.move((float)Settings.WIDTH / 2.0F, y);
    }

}
