package pvp_in_the_spire.reward;

import pvp_in_the_spire.patches.NeowRewardPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;

import java.util.ArrayList;

public class FirstRelicItem extends RewardItem {

    public FirstRelicItem(AbstractRelic relic)
    {
        super(relic);
    }

    @Override
    public boolean claimReward() {
        if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.GRID) {
            return false;
        }

        if (this.relicLink != null) {
            this.relicLink.isDone = true;
            this.relicLink.ignoreReward = true;
        }

        if (!this.ignoreReward) {
            //初始遗物的情况下，就再给它加回初始遗物
            ArrayList<AbstractRelic> relicList = new ArrayList<>();
            relicList.add(this.relic);
            relicList.addAll(AbstractDungeon.player.relics);
            //替换角色的relic
            AbstractDungeon.player.relics = relicList;
            //重新组织遗物位置
            AbstractDungeon.player.reorganizeRelics();
            CardCrawlGame.metricData.addRelicObtainData(this.relic);
            //如果把它加回来了，那下次还能换4
            NeowRewardPatch.ChangeCasePatch.bossRelicChanged = false;
        }

        return true;
    }
}
