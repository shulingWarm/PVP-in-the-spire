package pvp_in_the_spire.actions;

import pvp_in_the_spire.patches.CardShowPatch.DrawPileSender;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

//消耗特定的卡牌
public class ExhaustDrawPileCard extends AbstractGameAction {

    public AbstractCard card;

    public ExhaustDrawPileCard(AbstractCard card)
    {
        this.card = card;
    }

    @Override
    public void update() {
        CardGroup targetGroup = AbstractDungeon.player.drawPile;
        //判断是否包含这张牌
        if(targetGroup.contains(card))
        {
            targetGroup.moveToExhaustPile(card);
            //更新卡牌位置的显示
            DrawPileSender.updateDrawingCards();
        } else if (AbstractDungeon.player.hand.contains(card)) {
            //有时候这张牌可能已经抽到手上了，比如拿积木的时候
            AbstractDungeon.player.hand.moveToExhaustPile(card);
        }
        this.isDone = true;
    }
}
