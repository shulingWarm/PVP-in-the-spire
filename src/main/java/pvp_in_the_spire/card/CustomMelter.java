package pvp_in_the_spire.card;

import pvp_in_the_spire.actions.CustomRemoveBlockAction;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.blue.Melter;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

//手动实现的熔化
public class CustomMelter extends Melter {

    //复制熔化的时候，强制复制这张牌
    @SpirePatch(clz = Melter.class, method = "makeCopy")
    public static class CopyPatch
    {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> fix()
        {
            return SpireReturn.Return(new CustomMelter());
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new CustomRemoveBlockAction(m, p));
        this.addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
    }
}
