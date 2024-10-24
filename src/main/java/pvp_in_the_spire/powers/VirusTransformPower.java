package pvp_in_the_spire.powers;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.actions.TransformCardAction;
import pvp_in_the_spire.character.PlayerMonster;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.ArrayList;

//病毒转移的power,打出时将状态牌转移给对面
public class VirusTransformPower extends AbstractPower {

    public static final String POWER_ID = "virusTransformPower";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESC;

    public VirusTransformPower(AbstractCreature owner,int amount)
    {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.updateDescription();
        this.loadRegion("nightmare");
        this.type = PowerType.BUFF;
    }

    //多次使用时会叠加转移的牌数
    public void stackPower(int stackAmount) {
        this.amount += stackAmount;
    }

    public void onUseCard(AbstractCard card, UseCardAction action) {
        //如果是状态牌的话，就去寻找抽牌堆里的状态牌
        if(card.type == AbstractCard.CardType.POWER)
        {
            //已经转移过的牌数
            int discardedCardNum=0;

            CardGroup drawGroup =AbstractDungeon.player.drawPile;
            //需要被移除的牌
            ArrayList<AbstractCard> cardToRemove = new ArrayList<>();

            for(int idCard=drawGroup.group.size()-1;idCard>=0;--idCard) {
                AbstractCard currCard = drawGroup.group.get(idCard);
                //判断是不是状态牌
                if(currCard.type == AbstractCard.CardType.STATUS)
                {
                    //转移出这张牌
                    cardToRemove.add(currCard);
                    discardedCardNum++;
                    if(discardedCardNum>=this.amount)
                        break;
                }
            }
            //依次移除每个卡牌
            for(AbstractCard eachCard : cardToRemove)
            {
                PlayerMonster randMonster = GlobalManager.getBattleInfo().getRandEnemy();
                if(randMonster != null)
                    TransformCardAction.sendAddCard(eachCard,1,randMonster);
                drawGroup.removeCard(eachCard);
            }
        }
    }

    public void updateDescription() {
        this.description = DESC[0] + this.amount + DESC[1];
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings("virusTransformPower");
        NAME = powerStrings.NAME;
        DESC = powerStrings.DESCRIPTIONS;
    }

}
