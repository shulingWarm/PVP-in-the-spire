package pvp_in_the_spire.card;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.red.Offering;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;
import com.megacrit.cardcrawl.vfx.combat.OfferingEffect;

//修改过的祭品 改成失去最大生命的10%的生命值
public class ChangeOffering extends AbstractCard {

    public static final String ID = "Offering";
    private static final CardStrings cardStrings;

    //受伤的最大生命值的比例
    public static final float LOSE_RATE = 0.1f;

    public ChangeOffering() {
        super("Offering", cardStrings.NAME, "red/skill/offering", 0, cardStrings.DESCRIPTION, CardType.SKILL, CardColor.RED, CardRarity.RARE, CardTarget.SELF);
        this.exhaust = true;
        this.baseMagicNumber = 3;
        this.magicNumber = this.baseMagicNumber;
        //初始化伤害值，正常情况下就显示这个
        if(AbstractDungeon.player!=null)
        {
            this.baseDamage = (int)(AbstractDungeon.player.maxHealth * LOSE_RATE);
        }
        else {
            //默认情况下先写成8
            this.baseDamage = 8;
        }
    }

    //这里计算的是对自己的伤害值，什么都不需要叠加
    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        //如果有无实体的话，就只失去一滴生命
        if(AbstractDungeon.player.hasPower(IntangiblePlayerPower.POWER_ID))
        {
            this.damage = 1;
        }
        else
        {
            //计算即将受到的伤害值
            this.damage = (int)(AbstractDungeon.player.maxHealth * LOSE_RATE);
        }
    }

    //复制本体时改成复制这张牌
    @SpirePatch(clz = Offering.class,method = "makeCopy")
    public static class ChangeCardCopy
    {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> fix(Offering __instance)
        {
            return SpireReturn.Return(new ChangeOffering());
        }
    }

    @Override
    public void applyPowers() {
        //叠加buff的时候不需要特殊考虑
        this.calculateCardDamage(null);
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        if (Settings.FAST_MODE) {
            this.addToBot(new VFXAction(new OfferingEffect(), 0.1F));
        } else {
            this.addToBot(new VFXAction(new OfferingEffect(), 0.5F));
        }

        //失去10%的最大生命
        this.addToBot(new LoseHPAction(p, p, (int)(p.maxHealth * LOSE_RATE)));
        this.addToBot(new GainEnergyAction(2));
        this.addToBot(new DrawCardAction(p, this.magicNumber));
    }

    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeMagicNumber(2);
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }

    }

    public AbstractCard makeCopy() {
        return new ChangeOffering();
    }

    static {
        cardStrings = CardCrawlGame.languagePack.getCardStrings("ChangeOffering");
    }

}
