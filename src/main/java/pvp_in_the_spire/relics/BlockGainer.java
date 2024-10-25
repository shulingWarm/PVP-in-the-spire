package pvp_in_the_spire.relics;

import pvp_in_the_spire.ui.TextureManager;
import basemod.abstracts.CustomRelic;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

//格挡增益的遗物，每死一次就获得一个，算是一种效果补偿
public class BlockGainer extends CustomRelic {

    public static final String ID = "blockGainer";

    //正在触发状态的标志
    public static boolean inTriggerFlag = false;
    //玩家目前获得的这个遗物的数量
    //主要是用于平衡全身撞击这张牌
    public static int gainedNum = 0;
    //格挡增益的倍率
    public static float blockGainRate = 0.5f;

    public BlockGainer() {
        super(ID, TextureManager.BLOCK_GAIN,
        RelicTier.SPECIAL, CustomRelic.LandingSound.FLAT);
    }

    public void instantObtain() {
        super.instantObtain();
        //获得一次这个遗物就给计数增加一次
        gainedNum++;
    }

    //获得格挡时的触发，每次都获得等量的格挡
    public int onPlayerGainedBlock(float blockAmount) {
        //最终获得的格挡值
        int blockNum = super.onPlayerGainedBlock(blockAmount);
        //加上这个新格挡之后的格挡
        int blockAfterAdd = blockNum + AbstractDungeon.player.currentBlock;
        //如果目前正在触发就直接返回
        if(inTriggerFlag || blockAfterAdd >= AbstractDungeon.player.maxHealth)
            return blockNum;
        //记录表示正在触发
        inTriggerFlag = true;
        //令玩家再获得一次格挡
        AbstractDungeon.player.addBlock((int)(blockNum * blockGainRate));
        inTriggerFlag = false;
        return blockNum;
    }

    //把blockgainer的比率改成百分比
    public static String getPercentRate()
    {
        int perRate = (int)(blockGainRate * 100);
        return perRate + "%";
    }

    public String getUpdatedDescription() { return this.DESCRIPTIONS[0]
            + getPercentRate() + this.DESCRIPTIONS[1]; }

    public CustomRelic makeCopy() { return new BlockGainer(); }

}
