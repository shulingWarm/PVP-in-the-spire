package pvp_in_the_spire.patches.CardShowPatch;

import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

//手里的牌的记录器，消费端很难判断到底什么时候应该更新位置，那就从这个地方增加一些提示
public class CardRecorder {

    public ArrayList<AbstractCard> cardList = new ArrayList<AbstractCard>();
    //即将抽到的牌
    public ArrayList<AbstractCard> drawingCards = new ArrayList<>();

    //是否刚刚更新过
    public boolean justUpdateFlag = false;

    public CardRecorder()
    {

    }
}
