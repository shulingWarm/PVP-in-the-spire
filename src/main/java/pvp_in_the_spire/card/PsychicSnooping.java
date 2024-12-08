package pvp_in_the_spire.card;

import pvp_in_the_spire.actions.PsychicSnoopingAction;
import pvp_in_the_spire.character.PlayerMonster;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

//心灵窥探
public class PsychicSnooping extends CustomCard {

    public static final String ID = "PsychicSnooping";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    //升级后的相关描述
    public static final CardStrings upgradedStrings = CardCrawlGame.languagePack.getCardStrings("StealCard");
    public static final String NAME = cardStrings.NAME;
    //临时用邪咒的图片
    public static final String IMG = "pvp_in_the_spire/images/cards/skill/PsychicSnooping.png";
    //升级后的图片
    public static final String UPGRADE_IMG = "pvp_in_the_spire/images/cards/skill/StealCard.png";
    private static final int COST = 1;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final AbstractCard.CardType TYPE = AbstractCard.CardType.SKILL;
    private static final AbstractCard.CardColor COLOR = AbstractCard.CardColor.COLORLESS;
    private static final AbstractCard.CardRarity RARITY = AbstractCard.CardRarity.RARE;
    private static final AbstractCard.CardTarget TARGET = CardTarget.ENEMY;

    public PsychicSnooping()
    {
        super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.baseMagicNumber = 1;
        this.magicNumber = this.baseMagicNumber;
        //设置为消耗
        this.exhaust = true;
    }

    //升级时改成施加两层
    public void upgrade() {
        if(timesUpgraded>=2)
            return;
        if(timesUpgraded==0)
        {
            //更改描述
            ++timesUpgraded;
            this.rawDescription = upgradedStrings.DESCRIPTION;
            //更改名字
            this.name = upgradedStrings.NAME;
            //重新载入图片
            this.textureImg = UPGRADE_IMG;
            this.loadCardImage(UPGRADE_IMG);
            this.upgraded=true;
            this.initializeTitle();
            //更新描述
            this.initializeDescription();
        }
        else {
            //更改描述
            this.upgradeName();
            this.rawDescription = upgradedStrings.UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }

    //可以升级两次
    @Override
    public boolean canUpgrade() {
        return timesUpgraded<2;
    }

    //给目标施加一层邪咒
    public void use(AbstractPlayer p, AbstractMonster m) {
        if(m instanceof PlayerMonster)
        {
            this.addToBot(new PsychicSnoopingAction((PlayerMonster) m,
                    this.timesUpgraded>0,this.timesUpgraded>1));
        }

    }

    public AbstractCard makeCopy() {
        return new PsychicSnooping();
    }

}
