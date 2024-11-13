package pvp_in_the_spire.patches.CardShowPatch;

import pvp_in_the_spire.SocketServer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

//这是一个用来显示牌的界面，会显示在敌人的头上。
public class CardBox {

    public float xCenter;
    public float yCenter;
    public CardRecorder shownCards;

    //显示牌时的缩放倍率
    public static float SHOW_SCALE = 0.4F;

    //上一次迭代的卡牌数量
    int lastCardNum = 0;

    //最多同时显示的牌数
    public static final int MAX_SHOW_NUM = 10;

    //目前的攻击意图的值
    public int damageAmount = 0;

    //当前的card box所属的monster
    AbstractMonster belongMonster;

    public CardBox(float xCenter, float yCenter,
       CardRecorder shownCards,AbstractMonster monster)
    {
        //记录传入的属性
        this.xCenter = xCenter;
        this.yCenter = yCenter;
        this.shownCards = shownCards;
        //记录当前的box所属的monster
        this.belongMonster = monster;
    }

    //默认的无monster的构造函数
    public CardBox(float xCenter, float yCenter,
                   CardRecorder shownCards)
    {
        this(xCenter,yCenter,shownCards,null);
    }

    //根据当前是第几个牌来计算当前的偏移量
    int getXOffsetById(int idCard)
    {
        //计算向左最多能放置的id
        int maxSet = (int)(xCenter / (AbstractCard.IMG_WIDTH * SHOW_SCALE)) - 1;
        if(maxSet > idCard)
            return -idCard;
        return -maxSet;
    }

    //获得接下来5张牌的攻击总数
    public int sumDamageAmount(int cardNum)
    {
        //判断查找的牌数是否超过
        if(cardNum > shownCards.drawingCards.size())
        {
            cardNum = shownCards.drawingCards.size();
        }
        //总的伤害数
        int sumDamage = 0;
        //遍历即将读取到的每个牌
        for(int idCard=0;idCard<cardNum;++idCard)
        {
            //获取对应的牌
            AbstractCard card = shownCards.drawingCards.get(idCard);
            //判断是不是攻击牌
            if(card.type == AbstractCard.CardType.ATTACK)
            {
                sumDamage += card.baseDamage;
            }
        }
        return  sumDamage;
    }

    //根据即将抽到的第一张牌更新意图
    public AbstractMonster.Intent getIntent()
    {
        //如果没有牌的话就是unknown
        if(shownCards.drawingCards.isEmpty())
            return AbstractMonster.Intent.UNKNOWN;
        //求和接下来5张牌里面的伤害总和
        this.damageAmount = sumDamageAmount(5);
        if(damageAmount > 0)
            return AbstractMonster.Intent.ATTACK;
        //获得第一张牌
        AbstractCard firstCard = shownCards.drawingCards.get(0);
        //判断是不是能力牌
        if(firstCard.type == AbstractCard.CardType.POWER)
            return AbstractMonster.Intent.BUFF;
        //技能牌，判断是不是给自己使用的
        if(firstCard.target == AbstractCard.CardTarget.SELF)
        {
            if(firstCard.baseBlock>0)
                return AbstractMonster.Intent.DEFEND;
            return AbstractMonster.Intent.MAGIC;
        }
        //判断是不是金卡或者大于1费
        if(firstCard.rarity == AbstractCard.CardRarity.RARE ||
            firstCard.cost>1 )
            return AbstractMonster.Intent.STRONG_DEBUFF;
        return AbstractMonster.Intent.DEBUFF;
    }

    //对牌内容的渲染
    public void render(SpriteBatch sb)
    {
        //判断是否需要更新显示位置
        boolean updateLocation=false;
        if(this.belongMonster != null && (shownCards.justUpdateFlag ||
            this.belongMonster.intent == AbstractMonster.Intent.DEBUG))
        {
            updateLocation = true;
            shownCards.justUpdateFlag = false;
            //更新意图
            AbstractMonster.Intent tempIntent = getIntent();
            //如果是攻击意图，需要设置对应的伤害值
            if(tempIntent== AbstractMonster.Intent.ATTACK)
            {
                this.belongMonster.setMove((byte)1,tempIntent,
                        this.damageAmount);
                if(this.belongMonster.intent == AbstractMonster.Intent.DEBUG)
                    this.belongMonster.createIntent();
            }
            else {
                this.belongMonster.setMove((byte)1,tempIntent,-1);
            }
        }
        //遍历所有需要显示的牌

        //下回合抽牌显示的数量
        int showDrawNum = Math.min(MAX_SHOW_NUM-shownCards.cardList.size(),shownCards.drawingCards.size());
        //有圆顶的情况下不显示即将抽到的牌
        if(SocketServer.hasDome)
            showDrawNum=0;
        int xOffset = getXOffsetById(shownCards.cardList.size() + showDrawNum -1);
        //先显示要抽的牌
        for(int idCard=showDrawNum-1;idCard>=0;--idCard)
        {
            //当前的牌
            AbstractCard card = shownCards.drawingCards.get(idCard);
            //判断是否需要更新位置
            if(updateLocation)
            {
                card.current_y = yCenter;
                card.target_y = yCenter;
                card.target_x = xCenter + xOffset * AbstractCard.IMG_WIDTH * SHOW_SCALE;
                card.current_x = card.target_x;
                //更新卡牌的缩放大小
                card.targetDrawScale = SHOW_SCALE;
                card.drawScale = SHOW_SCALE;
                ++xOffset;
                //强行设置这个牌的透明度
                CardShowChange.changeCardAlpha(card,0.5f);
            }
            card.render(sb);
        }
        for (AbstractCard card : shownCards.cardList) {
            //获取当前位置的牌
            //判断是否需要更新位置
            if (updateLocation) {
                //更新卡牌的位置
                card.current_y = yCenter;
                card.target_y = yCenter;
                card.target_x = xCenter + xOffset * AbstractCard.IMG_WIDTH * SHOW_SCALE;
                card.current_x = card.target_x;
                //更新卡牌的缩放大小
                card.targetDrawScale = SHOW_SCALE;
                card.drawScale = SHOW_SCALE;
                ++xOffset;
                card.unfadeOut();
            }
            //渲染这个牌
            card.render(sb);
        }
    }

}
