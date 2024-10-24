package pvp_in_the_spire.powers;

import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.actions.TransformCardAction;
import pvp_in_the_spire.character.PlayerMonster;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

//弃牌时把状态牌转移给对面
public class ComputeTransformPower extends AbstractPower {

    public static final String POWER_ID = "computeTransformPower";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESC;

    public ComputeTransformPower(AbstractCreature owner)
    {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = 0;
        this.updateDescription();
        //借用一下火焰吐息的贴图
        this.loadRegion("cExplosion");
        this.type = PowerType.BUFF;
    }

    //多次使用时什么都不需要做
    public void stackPower(int stackAmount) {
    }

    //弃牌时触发的卡牌转移
    @SpirePatch(clz = AbstractCard.class,method = "triggerOnManualDiscard")
    public static class transformOnDiscard
    {

        //判断这张牌是否需要被丢弃
        public static boolean judgeTransform(AbstractCard card)
        {
            //强化过的牌不触发转移
            if(card.upgraded)
                return false;
            //判断是不是没强化过的白卡 基础卡 状态卡 诅咒卡
            return card.rarity == AbstractCard.CardRarity.COMMON ||
                    card.rarity == AbstractCard.CardRarity.BASIC ||
                    card.type == AbstractCard.CardType.CURSE ||
                    card.type == AbstractCard.CardType.STATUS;
        }

        //弃置之后的处理 如果有这个power就把状态牌转移给对面
        @SpirePostfixPatch
        public static void fix(AbstractCard __instance)
        {
            //判断被丢弃的牌是不是状态牌
            if(judgeTransform(__instance) &&
                AbstractDungeon.player.hasPower(ComputeTransformPower.POWER_ID))
            {
                PlayerMonster randMonster = GlobalManager.getBattleInfo().getRandEnemy();
                if(randMonster != null)
                {
                    //从5个地方找这张牌
                    AbstractDungeon.actionManager.addToTop(
                            new TransformCardAction(__instance,
                                    AbstractDungeon.player.discardPile,1,randMonster)
                    );
                    AbstractDungeon.actionManager.addToTop(
                            new TransformCardAction(__instance,
                                    AbstractDungeon.player.hand,1,randMonster)
                    );
                    AbstractDungeon.actionManager.addToTop(
                            new TransformCardAction(__instance,
                                    AbstractDungeon.player.drawPile,1,randMonster)
                    );
                }
            }
        }
    }

    public void updateDescription() {
        this.description = DESC[0];
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings("computeTransformPower");
        NAME = powerStrings.NAME;
        DESC = powerStrings.DESCRIPTIONS;
    }

}
