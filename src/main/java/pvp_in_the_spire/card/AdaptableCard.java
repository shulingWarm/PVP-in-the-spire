package pvp_in_the_spire.card;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pvp_in_the_spire.card.CardAction.AbstractCardAction;

import java.util.ArrayList;
import java.util.HashMap;

//允许被修改的卡牌
public class AdaptableCard extends AbstractCard {

    //被修改的卡牌的基础卡牌
    public AbstractCard baseCard;

    //卡牌抽象动作的列表
    public HashMap<String, AbstractCardAction> cardActionMap;

    public AdaptableCard(AbstractCard card) {
        super(card.cardID, card.name, card.assetUrl, card.cost,
            card.rawDescription, card.type, card.color, card.rarity, card.target);
        //初始化卡牌抽象动作的列表
        this.cardActionMap = new HashMap<>();
        //记录基础卡牌
        this.baseCard = card;
        //初始化卡牌数值
        this.baseMagicNumber = card.baseMagicNumber;
        this.baseDamage = card.baseDamage;
        this.baseBlock = card.baseBlock;
    }

    //向卡牌操作里面添加Action
    public void addActionToCard(AbstractCardAction cardAction)
    {
        //先尝试能不能直接通过修改卡牌数值而不添加action
        if(cardAction.tryDirectApply(this))
        {
            return;
        }
        //判断这个action是否记录过
        if(this.cardActionMap.containsKey(cardAction.getActionId()))
        {
            this.cardActionMap.get(cardAction.getActionId()).adjustRepeatAction(cardAction);
        }
        else
        {
            this.cardActionMap.put(cardAction.getActionId(),cardAction);
        }
    }

    //判断是否需要添加伤害Action
    //只有当基础卡牌不涉及伤害数值的时候才需要添加伤害类型的Action
    public boolean isNeedDamageAction()
    {
        return this.baseCard.damage == 0;
    }

    @Override
    public void upgrade() {

    }

    @Override
    public void use(AbstractPlayer abstractPlayer, AbstractMonster abstractMonster) {

    }

    @Override
    public AbstractCard makeCopy() {
        return null;
    }
}
