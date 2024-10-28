package pvp_in_the_spire.relics;

import pvp_in_the_spire.events.LoseGoldEvent;
import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.Communication;
import pvp_in_the_spire.character.PlayerMonster;
import pvp_in_the_spire.patches.EffectPatch;
import basemod.abstracts.CustomRelic;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.GainPennyEffect;

//pvp里面的绿帽
public class PVPEctoplasm extends CustomRelic {

    public static final String ID = "PVPEctoplasm";

    public static final int LOSE_GOLD = 5;

    public DamageInfo tempInfo = null;

    public PVPEctoplasm() {
        super(ID, "ectoplasm.png", RelicTier.BOSS, LandingSound.FLAT);
    }

    public String getUpdatedDescription() {
        return AbstractDungeon.player != null ? this.setDescription(AbstractDungeon.player.chosenClass) : this.setDescription((AbstractPlayer.PlayerClass)null);
    }

    private String setDescription(AbstractPlayer.PlayerClass c) {
        return this.DESCRIPTIONS[1] + this.DESCRIPTIONS[0];
    }

    public void updateDescription(AbstractPlayer.PlayerClass c) {
        this.description = this.setDescription(c);
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
    }

    @Override
    public void onLoseHp(int damageAmount) {
        //判断自己目前的钱是否足够
        if(this.tempInfo != null && this.tempInfo.type == DamageInfo.DamageType.NORMAL &&
            damageAmount > AbstractDungeon.player.currentBlock &&
            AbstractDungeon.player.gold > 0 &&
            this.tempInfo.owner instanceof PlayerMonster)
        {
            int loseAmount = Math.min(LOSE_GOLD, AbstractDungeon.player.gold);
            PlayerMonster playerMonster = (PlayerMonster)this.tempInfo.owner;
            AbstractDungeon.player.loseGold(loseAmount);
            //发送令对方获得金币的事件
            Communication.sendEvent(new LoseGoldEvent(
                    GlobalManager.myPlayerTag,
                    playerMonster.playerTag,
                    loseAmount
            ));
            EffectPatch.enable = false;
            //执行丢钱的效果
            for(int i=0;i<loseAmount;++i)
            {
                AbstractDungeon.effectList.add(new GainPennyEffect(
                        playerMonster,AbstractDungeon.player.hb.cX,
                        AbstractDungeon.player.hb.cY,
                        playerMonster.hb.cX,
                        playerMonster.hb.cY,false
                ));
            }
            EffectPatch.enable = true;
            this.tempInfo = null;
        }
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        this.tempInfo = info;
        return super.onAttacked(info,damageAmount);
    }

    public void onEquip() {
        ++AbstractDungeon.player.energy.energyMaster;
    }

    public void onUnequip() {
        --AbstractDungeon.player.energy.energyMaster;
    }

    public AbstractRelic makeCopy() {
        return new PVPEctoplasm();
    }

}
