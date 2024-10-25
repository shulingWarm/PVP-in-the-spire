package pvp_in_the_spire.powers;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.actions.TransformCardAction;
import pvp_in_the_spire.character.PlayerMonster;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

//燃烧转移的buff,当出现烧牌时，把烧掉的状态牌给对面
public class BurnTransformPower extends AbstractPower {

    public static final String POWER_ID = "burnTransformPower";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESC;

    public BurnTransformPower(AbstractCreature owner)
    {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = 1;
        this.updateDescription();
        //借用一下火焰吐息的贴图
        this.loadRegion("firebreathing");
        this.type = PowerType.BUFF;
    }

    public void onExhaust(AbstractCard card) {
        //如果这张牌是状态牌，将这张牌转移给对面
        if(card.type == AbstractCard.CardType.STATUS)
        {
            PlayerMonster randMonster = GlobalManager.getBattleInfo().getRandEnemy();
            if(randMonster != null)
                TransformCardAction.sendAddCard(card,1,randMonster);
        }
    }

    public void atStartOfTurn() {
        //判断是不是需要删除
        if(this.amount<=1)
        {
            AbstractDungeon.actionManager.addToBottom(
                    new RemoveSpecificPowerAction(this.owner,this.owner,POWER_ID)
            );
        }
        else {
            //将power数量减1
            AbstractDungeon.actionManager.addToBottom(
                    new ReducePowerAction(this.owner,this.owner,POWER_ID,1)
            );
        }
    }

    public void updateDescription() {
        this.description = DESC[0] + this.amount + DESC[1];
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings("burnTransformPower");
        NAME = powerStrings.NAME;
        DESC = powerStrings.DESCRIPTIONS;
    }

}
