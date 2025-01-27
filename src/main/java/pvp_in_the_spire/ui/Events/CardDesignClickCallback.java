package pvp_in_the_spire.ui.Events;

import com.megacrit.cardcrawl.cards.AbstractCard;

//在卡牌设计界面点击卡牌时会调用这个回调
public interface CardDesignClickCallback {

    //在卡牌设计库里面点击卡牌的操作
    public void onCardClicked(AbstractCard card);

}
