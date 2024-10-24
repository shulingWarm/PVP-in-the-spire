package pvp_in_the_spire.actions;

import pvp_in_the_spire.card.TimeEat;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.unique.AddCardToDeckAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.red.BloodForBlood;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.relics.BustedCrown;
import com.megacrit.cardcrawl.relics.PrismaticShard;
import com.megacrit.cardcrawl.relics.QuestionCard;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDrawPileEffect;

import java.util.ArrayList;
import java.util.Iterator;

//自定义的尼利的宝典
//有特殊的抽牌堆
public class UserCodeAction extends AbstractGameAction {

    public static int numPlaced;
    private boolean retrieveCard = false;

    public UserCodeAction() {
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FAST;
    }

    public void update() {
        if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            this.isDone = true;
        } else if (this.duration == Settings.ACTION_DUR_FAST) {
            AbstractDungeon.cardRewardScreen.customCombatOpen(this.generateCardChoices(), CardRewardScreen.TEXT[1], true);
            this.tickDuration();
        } else {
            if (!this.retrieveCard) {
                if (AbstractDungeon.cardRewardScreen.discoveryCard != null) {
                    AbstractCard codexCard = AbstractDungeon.cardRewardScreen.discoveryCard.makeStatEquivalentCopy();
                    codexCard.current_x = -1000.0F * Settings.xScale;
                    AbstractDungeon.effectList.add(new ShowCardAndAddToDrawPileEffect(codexCard, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, true));
                    AbstractDungeon.cardRewardScreen.discoveryCard = null;
                    //当用户选择了一张牌时，同时把这个牌添加到牌库里面
                    //如果这个牌是以血换血，就需要做一些特殊的处理
                    if(codexCard instanceof BloodForBlood)
                    {
                        AbstractDungeon.actionManager.addToBottom(new AddCardToDeckAction(new BloodForBlood()));
                    }
                    else {
                        AbstractDungeon.actionManager.addToBottom(new AddCardToDeckAction(codexCard.makeCopy()));
                        //AbstractDungeon.actionManager.addToBottom(new AddCardToDeckAction(new GodPunishment()));
                    }
                }

                this.retrieveCard = true;
            }

            this.tickDuration();
        }
    }

    //默认的出牌概率
    //白卡 蓝卡 金卡 无色卡
    public static final int[] rarityList={70,100,110,120};

    public static CardGroup getRandomCardGroup()
    {
        //生成一个随机数
        int tempRare = AbstractDungeon.cardRng.random(rarityList[3]);
        int idRare = 0;
        for(;idRare<4;++idRare)
        {
            if(tempRare<rarityList[idRare])
            {
                break;
            }
        }
        switch (idRare)
        {
            case 0:
                return AbstractDungeon.srcCommonCardPool;
            case 1:
                return AbstractDungeon.srcUncommonCardPool;
            case 2:
                return AbstractDungeon.srcRareCardPool;
        }
        return AbstractDungeon.srcColorlessCardPool;
    }

    //有棱镜的情况下，获取任何颜色的卡牌
    public static AbstractCard getAnyColorCard()
    {
        //生成一个随机数
        int tempRare = AbstractDungeon.cardRng.random(rarityList[3]);
        int idRare = 0;
        for(;idRare<4;++idRare)
        {
            if(tempRare<rarityList[idRare])
            {
                break;
            }
        }
        switch (idRare)
        {
            case 0:
                return CardLibrary.getAnyColorCard(AbstractCard.CardRarity.COMMON);
            case 1:
                return CardLibrary.getAnyColorCard(AbstractCard.CardRarity.UNCOMMON);
            case 2:
                return CardLibrary.getAnyColorCard(AbstractCard.CardRarity.RARE);
        }
        return CardLibrary.getAnyColorCard(AbstractCard.CardRarity.COMMON);
    }

    //按照稀有度拿牌
    public static AbstractCard getCardByRarity()
    {
        //判断是否有棱镜，有棱镜的话按照棱镜的逻辑来生成牌
        if(AbstractDungeon.player.hasRelic(PrismaticShard.ID))
        {
            return getAnyColorCard();
        }
        //获得随机的卡池
        ArrayList<AbstractCard> cardList = getRandomCardGroup().group;
        //返回随机的一张牌
        return (AbstractCard) cardList.get(AbstractDungeon.cardRng.random(cardList.size()-1));
    }

    //自定义的随机牌生成器，关键是在里面加入了白卡
    public static AbstractCard getRandomCardWithColorless() {
        ArrayList<AbstractCard> list = new ArrayList();
        Iterator var1 = AbstractDungeon.srcCommonCardPool.group.iterator();

        AbstractCard c;
        while(var1.hasNext()) {
            c = (AbstractCard)var1.next();
            if (!c.hasTag(AbstractCard.CardTags.HEALING)) {
                list.add(c);
                UnlockTracker.markCardAsSeen(c.cardID);
            }
        }

        var1 = AbstractDungeon.srcUncommonCardPool.group.iterator();

        while(var1.hasNext()) {
            c = (AbstractCard)var1.next();
            if (!c.hasTag(AbstractCard.CardTags.HEALING)) {
                list.add(c);
                UnlockTracker.markCardAsSeen(c.cardID);
            }
        }

        var1 = AbstractDungeon.srcRareCardPool.group.iterator();

        while(var1.hasNext()) {
            c = (AbstractCard)var1.next();
            if (!c.hasTag(AbstractCard.CardTags.HEALING)) {
                list.add(c);
                UnlockTracker.markCardAsSeen(c.cardID);
            }
        }

        var1 = AbstractDungeon.srcColorlessCardPool.group.iterator();

        //在这里特别添加了白卡卡池
        while(var1.hasNext()) {
            c = (AbstractCard)var1.next();
            if(c instanceof TimeEat)
            {
                System.out.println("find time eat!!!\n\n");
            }
            if (!c.hasTag(AbstractCard.CardTags.HEALING)) {
                list.add(c);
                UnlockTracker.markCardAsSeen(c.cardID);
            }
        }

        return (AbstractCard)list.get(AbstractDungeon.cardRandomRng.random(list.size() - 1));
    }

    private ArrayList<AbstractCard> generateCardChoices() {
        ArrayList<AbstractCard> derp = new ArrayList();

        //目标的拿牌数
        int targetNum = 3;
        if(AbstractDungeon.player.hasRelic(QuestionCard.ID))
        {
            targetNum++;
        }
        //如果有破碎金冠，再把抽牌数-2
        if(AbstractDungeon.player.hasRelic(BustedCrown.ID))
        {
            targetNum-=2;
        }

        while(derp.size() != targetNum) {
            boolean dupe = false;
            //从卡池里面抽卡，按照稀有度来抽牌
            AbstractCard tmp = getCardByRarity();
            Iterator var4 = derp.iterator();

            while(var4.hasNext()) {
                AbstractCard c = (AbstractCard)var4.next();
                if (c.cardID.equals(tmp.cardID)) {
                    dupe = true;
                    break;
                }
            }

            if (!dupe) {
                derp.add(tmp.makeCopy());
            }
        }

        return derp;
    }

}
