package pvp_in_the_spire.game_event;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.ArrayList;
import java.util.List;

//修改过的全知头骨,禁止死在里面
public class ModifiedSkull extends AbstractImageEvent {

    public static final String ID = "ModifiedSkull";
    private static final EventStrings eventStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    public static final String[] OPTIONS;
    private static final String INTRO_MSG;
    private static final String INTRO_2_MSG;
    private static final String ASK_AGAIN_MSG;
    private static final String POTION_MSG;
    private static final String CARD_MSG;
    private static final String GOLD_MSG;
    private static final String LEAVE_MSG;
    private int potionCost;
    private int cardCost;
    private int goldCost;
    private int leaveCost;
    private static final int GOLD_REWARD = 90;
    private CurScreen screen;
    private String optionsChosen;
    private int damageTaken;
    private int goldEarned;
    private List<String> potions;
    private List<String> cards;
    private ArrayList<Reward> options;

    public ModifiedSkull() {
        super(NAME, INTRO_MSG, "images/events/knowingSkull.jpg");
        this.screen = CurScreen.INTRO_1;
        this.optionsChosen = "";
        this.options = new ArrayList();
        this.imageEventText.setDialogOption(OPTIONS[0]);
        this.options.add(Reward.CARD);
        this.options.add(Reward.GOLD);
        this.options.add(Reward.POTION);
        this.options.add(Reward.LEAVE);
        this.leaveCost = 6;
        this.cardCost = this.leaveCost;
        this.potionCost = this.leaveCost;
        this.goldCost = this.leaveCost;
        this.damageTaken = 0;
        this.goldEarned = 0;
        this.cards = new ArrayList();
        this.potions = new ArrayList();
    }

    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.play("EVENT_SKULL");
        }

    }

    protected void buttonEffect(int buttonPressed) {
        //玩家当前的血量
        int playerHp = AbstractDungeon.player.currentHealth;
        switch (this.screen) {
            case INTRO_1:
                this.imageEventText.updateBodyText(INTRO_2_MSG);
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[4] + this.potionCost + OPTIONS[1],
                        (playerHp - leaveCost)<=potionCost);
                this.imageEventText.setDialogOption(OPTIONS[5] + 90 + OPTIONS[6] + this.goldCost + OPTIONS[1],
                        (playerHp - leaveCost)<=goldCost);
                this.imageEventText.setDialogOption(OPTIONS[3] + this.cardCost + OPTIONS[1],
                        (playerHp - leaveCost)<=cardCost);
                this.imageEventText.setDialogOption(OPTIONS[7] + Math.min(playerHp-1,leaveCost) + OPTIONS[1]);
                this.screen = CurScreen.ASK;
                break;
            case ASK:
                CardCrawlGame.sound.play("DEBUFF_2");
                switch (buttonPressed) {
                    case 0:
                        this.obtainReward(0);
                        return;
                    case 1:
                        this.obtainReward(1);
                        return;
                    case 2:
                        this.obtainReward(2);
                        return;
                    default:
                        int leaveDamage = Math.min(AbstractDungeon.player.currentHealth - 1,this.leaveCost);
                        AbstractDungeon.player.damage(new DamageInfo((AbstractCreature)null, leaveDamage, DamageInfo.DamageType.HP_LOSS));
                        this.damageTaken += leaveDamage;
                        this.setLeave();
                        return;
                }
            case COMPLETE:
                logMetric("Knowing Skull", this.optionsChosen, this.cards, (List)null, (List)null, (List)null, (List)null, this.potions, (List)null, this.damageTaken, 0, 0, 0, this.goldEarned, 0);
                this.openMap();
        }

    }

    private void obtainReward(int slot) {
        switch (slot) {
            case 0:
                AbstractDungeon.player.damage(new DamageInfo((AbstractCreature)null, this.potionCost, DamageInfo.DamageType.HP_LOSS));
                this.damageTaken += this.potionCost;
                ++this.potionCost;
                this.optionsChosen = this.optionsChosen + "POTION ";
                this.imageEventText.updateBodyText(POTION_MSG + ASK_AGAIN_MSG);
                if (AbstractDungeon.player.hasRelic("Sozu")) {
                    AbstractDungeon.player.getRelic("Sozu").flash();
                } else {
                    AbstractPotion p = PotionHelper.getRandomPotion();
                    this.potions.add(p.ID);
                    AbstractDungeon.player.obtainPotion(p);
                }
                break;
            case 1:
                AbstractDungeon.player.damage(new DamageInfo((AbstractCreature)null, this.goldCost, DamageInfo.DamageType.HP_LOSS));
                this.damageTaken += this.goldCost;
                ++this.goldCost;
                this.optionsChosen = this.optionsChosen + "GOLD ";
                this.imageEventText.updateBodyText(GOLD_MSG + ASK_AGAIN_MSG);
                AbstractDungeon.effectList.add(new RainingGoldEffect(90));
                AbstractDungeon.player.gainGold(90);
                this.goldEarned += 90;
                break;
            case 2:
                AbstractDungeon.player.damage(new DamageInfo((AbstractCreature)null, this.cardCost, DamageInfo.DamageType.HP_LOSS));
                this.damageTaken += this.cardCost;
                ++this.cardCost;
                this.optionsChosen = this.optionsChosen + "CARD ";
                this.imageEventText.updateBodyText(CARD_MSG + ASK_AGAIN_MSG);
                AbstractCard c = AbstractDungeon.returnColorlessCard(AbstractCard.CardRarity.UNCOMMON).makeCopy();
                this.cards.add(c.cardID);
                AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                break;
        }

        int playerHp = AbstractDungeon.player.currentHealth;
        this.imageEventText.clearAllDialogs();
        this.imageEventText.setDialogOption(OPTIONS[4] + this.potionCost + OPTIONS[1],
                (playerHp - leaveCost)<=potionCost);
        this.imageEventText.setDialogOption(OPTIONS[5] + 90 + OPTIONS[6] + this.goldCost + OPTIONS[1],
                (playerHp - leaveCost)<=goldCost);
        this.imageEventText.setDialogOption(OPTIONS[3] + this.cardCost + OPTIONS[1],
                (playerHp - leaveCost)<=cardCost);
        this.imageEventText.setDialogOption(OPTIONS[7] + Math.min(playerHp-1,leaveCost) + OPTIONS[1]);
    }

    private void setLeave() {
        this.imageEventText.updateBodyText(LEAVE_MSG);
        this.imageEventText.clearAllDialogs();
        this.imageEventText.setDialogOption(OPTIONS[8]);
        this.screen = CurScreen.COMPLETE;
    }

    static {
        eventStrings = CardCrawlGame.languagePack.getEventString("Knowing Skull");
        NAME = eventStrings.NAME;
        DESCRIPTIONS = eventStrings.DESCRIPTIONS;
        OPTIONS = eventStrings.OPTIONS;
        INTRO_MSG = DESCRIPTIONS[0];
        INTRO_2_MSG = DESCRIPTIONS[1];
        ASK_AGAIN_MSG = DESCRIPTIONS[2];
        POTION_MSG = DESCRIPTIONS[4];
        CARD_MSG = DESCRIPTIONS[5];
        GOLD_MSG = DESCRIPTIONS[6];
        LEAVE_MSG = DESCRIPTIONS[7];
    }

    private static enum Reward {
        POTION,
        LEAVE,
        GOLD,
        CARD;

        private Reward() {
        }
    }

    private static enum CurScreen {
        INTRO_1,
        ASK,
        COMPLETE;

        private CurScreen() {
        }
    }

}
