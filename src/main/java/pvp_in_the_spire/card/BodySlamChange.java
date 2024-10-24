package pvp_in_the_spire.card;

import pvp_in_the_spire.relics.BlockGainer;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.red.BodySlam;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

//修改过的全身撞击 计算数值的时候需要除以已经获得的格挡增益遗物的数量
public class BodySlamChange extends AbstractCard {

    private static final CardStrings cardStrings;

    //计算需要把格挡除以的系数
    public static float getDivideIndex()
    {
        //返回已经获得的增益遗物的数量
        return BlockGainer.gainedNum * BlockGainer.blockGainRate + 1.f;
    }

    public BodySlamChange() {
        super("Body Slam", cardStrings.NAME, "red/attack/body_slam", 1, cardStrings.DESCRIPTION, CardType.ATTACK, CardColor.RED, CardRarity.COMMON, CardTarget.ENEMY);
        this.baseDamage = 0;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        this.baseDamage = (int)(p.currentBlock/getDivideIndex());
        this.calculateCardDamage(m);
        this.addToBot(new DamageAction(m, new DamageInfo(p, this.damage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        this.rawDescription = cardStrings.DESCRIPTION;
        this.initializeDescription();
    }

    public void applyPowers() {
        this.baseDamage = (int) (AbstractDungeon.player.currentBlock/getDivideIndex());
        super.applyPowers();
        this.rawDescription = cardStrings.DESCRIPTION;
        this.rawDescription = this.rawDescription + cardStrings.UPGRADE_DESCRIPTION;
        this.initializeDescription();
    }

    public void onMoveToDiscard() {
        this.rawDescription = cardStrings.DESCRIPTION;
        this.initializeDescription();
    }

    public void calculateCardDamage(AbstractMonster mo) {
        super.calculateCardDamage(mo);
        this.rawDescription = cardStrings.DESCRIPTION;
        this.rawDescription = this.rawDescription + cardStrings.UPGRADE_DESCRIPTION;
        this.initializeDescription();
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBaseCost(0);
        }

    }

    //更改全身撞击的获得渠道，把它换成改过的这张牌
    @SpirePatch(clz = BodySlam.class,method = "makeCopy")
    public static class ChangeCardPatch
    {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> fix(BodySlam __instance)
        {
            return SpireReturn.Return(new BodySlamChange());
        }
    }

    public AbstractCard makeCopy() {
        return new BodySlamChange();
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings("Body Slam");
    }

}
