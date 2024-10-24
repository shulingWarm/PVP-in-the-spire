 package pvp_in_the_spire.actions;
 import com.megacrit.cardcrawl.actions.AbstractGameAction;
 import com.megacrit.cardcrawl.actions.utility.DiscardToHandAction;
 import com.megacrit.cardcrawl.cards.AbstractCard;
 import com.megacrit.cardcrawl.characters.AbstractPlayer;
 import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

 public class BasicDefendToHandAction extends AbstractGameAction {
     private AbstractPlayer p;


   public BasicDefendToHandAction() {
        this.p = AbstractDungeon.player;
       setValues(this.p, AbstractDungeon.player, this.amount);
       this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
   }

   
   public void update() {
       if (this.p.discardPile.size() > 0) {
           for (AbstractCard card : this.p.discardPile.group) {
                if(card.hasTag(AbstractCard.CardTags.STARTER_DEFEND)){
                    addToBot(new DiscardToHandAction(card));
                }
           }
       }
       tickDuration();
       this.isDone = true;
   }
 }


