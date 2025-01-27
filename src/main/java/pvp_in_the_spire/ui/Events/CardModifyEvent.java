package pvp_in_the_spire.ui.Events;

import com.megacrit.cardcrawl.cards.AbstractCard;

//卡牌信息更新的接口
//当输入框里面的信息更新时，同步更新卡牌信息
public interface CardModifyEvent {

    //初始化卡牌信息
    public String initCardInfo(AbstractCard card);

    //卡牌信息更新时的回调
    public void cardModifyTrigger(AbstractCard card, String configText);

}
