package pvp_in_the_spire.card;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.powers.TimeWarpDebuff;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

//多人玩家情况下的老头表的卡牌
public class MultiplayerTimeWarp extends CustomCard {

    public static final String ID = "MultiplayerTimeWarp";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    //临时用斩破命运的图片来代替，所有的图片最后统一处理
    public static final String IMG = "pvp_in_the_spire/images/cards/power/time.png";
    private static final int COST = 1;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final CardType TYPE = CardType.SKILL;
    private static final CardColor COLOR = CardColor.COLORLESS;
    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.ENEMY;

    public MultiplayerTimeWarp()
    {
        super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.baseMagicNumber = 16;
        this.magicNumber = this.baseMagicNumber;
    }

    //升级时设置成出牌限制减4
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(-4);
        }
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        //执行应用power的操作
        GlobalManager.playerManager.selfPlayerInfo.powerManager.applyPower(
            new TimeWarpDebuff(m,this.magicNumber),m,p,this.magicNumber,true
        );
    }

    public AbstractCard makeCopy() {
        return new MultiplayerTimeWarp();
    }

}
