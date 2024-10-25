package pvp_in_the_spire.game_event;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import java.util.ArrayList;

//修改过的死灵书事件，禁止死在死灵书里面
public class ModifiedCurseTome extends AbstractImageEvent {

    public static final String ID = "ModifiedCurseTome";
    private static final EventStrings eventStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    public static final String[] OPTIONS;
    private static final String INTRO_MSG;
    private static final String READ_1;
    private static final String READ_2;
    private static final String READ_3;
    private static final String READ_4;
    private static final String OBTAIN_MSG;
    private static final String IGNORE_MSG;
    private static final String STOP_MSG;
    private static final String OPT_READ;
    private static final String OPT_CONTINUE_1;
    private static final String OPT_CONTINUE_2;
    private static final String OPT_CONTINUE_3;
    private static final String OPT_STOP;
    private static final String OPT_LEAVE;
    private static final int DMG_BOOK_OPEN = 1;
    private static final int DMG_SECOND_PAGE = 2;
    private static final int DMG_THIRD_PAGE = 3;
    private static final int DMG_STOP_READING = 3;
    private static final int DMG_OBTAIN_BOOK = 10;
    private static final int A_2_DMG_OBTAIN_BOOK = 15;
    private int finalDmg;
    private int damageTaken;
    private ModifiedCurseTome.CurScreen screen;

    public ModifiedCurseTome() {
        super(NAME, INTRO_MSG, "images/events/cursedTome.jpg");
        this.screen = ModifiedCurseTome.CurScreen.INTRO;
        this.noCardsInRewards = true;
        this.damageTaken = 0;
        if (AbstractDungeon.ascensionLevel >= 15) {
            this.finalDmg = 15;
        } else {
            this.finalDmg = 10;
        }

        //计算伤害的总量
        int totalDamage = this.finalDmg + 6;

        this.imageEventText.setDialogOption(OPT_READ, totalDamage >= AbstractDungeon.player.currentHealth);
        this.imageEventText.setDialogOption(OPT_LEAVE);
    }

    protected void buttonEffect(int buttonPressed) {
        switch (this.screen) {
            case INTRO:
                this.imageEventText.clearAllDialogs();
                if (buttonPressed == 0) {
                    CardCrawlGame.sound.play("EVENT_TOME");
                    this.imageEventText.clearAllDialogs();
                    this.imageEventText.setDialogOption(OPT_CONTINUE_1);
                    this.imageEventText.updateBodyText(READ_1);
                    this.screen = ModifiedCurseTome.CurScreen.PAGE_1;
                } else {
                    this.imageEventText.clearAllDialogs();
                    this.imageEventText.setDialogOption(OPT_LEAVE);
                    this.imageEventText.updateBodyText(IGNORE_MSG);
                    this.screen = ModifiedCurseTome.CurScreen.END;
                    logMetricIgnored("Cursed Tome");
                }
                break;
            case PAGE_1:
                CardCrawlGame.sound.play("EVENT_TOME");
                AbstractDungeon.player.damage(new DamageInfo((AbstractCreature)null, 1, DamageInfo.DamageType.HP_LOSS));
                ++this.damageTaken;
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPT_CONTINUE_2);
                this.imageEventText.updateBodyText(READ_2);
                this.screen = ModifiedCurseTome.CurScreen.PAGE_2;
                break;
            case PAGE_2:
                CardCrawlGame.sound.play("EVENT_TOME");
                AbstractDungeon.player.damage(new DamageInfo((AbstractCreature)null, 2, DamageInfo.DamageType.HP_LOSS));
                this.damageTaken += 2;
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPT_CONTINUE_3);
                this.imageEventText.updateBodyText(READ_3);
                this.screen = ModifiedCurseTome.CurScreen.PAGE_3;
                break;
            case PAGE_3:
                CardCrawlGame.sound.play("EVENT_TOME");
                AbstractDungeon.player.damage(new DamageInfo((AbstractCreature)null, 3, DamageInfo.DamageType.HP_LOSS));
                this.damageTaken += 3;
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[5] + this.finalDmg + OPTIONS[6]);
                this.imageEventText.setDialogOption(OPT_STOP);
                this.imageEventText.updateBodyText(READ_4);
                this.screen = ModifiedCurseTome.CurScreen.LAST_PAGE;
                break;
            case LAST_PAGE:
                if (buttonPressed == 0) {
                    AbstractDungeon.player.damage(new DamageInfo((AbstractCreature)null, this.finalDmg, DamageInfo.DamageType.HP_LOSS));
                    this.damageTaken += this.finalDmg;
                    this.imageEventText.updateBodyText(OBTAIN_MSG);
                    this.randomBook();
                    this.imageEventText.clearAllDialogs();
                    this.imageEventText.setDialogOption(OPT_LEAVE);
                } else {
                    AbstractDungeon.player.damage(new DamageInfo((AbstractCreature)null, 3, DamageInfo.DamageType.HP_LOSS));
                    this.damageTaken += 3;
                    this.imageEventText.updateBodyText(STOP_MSG);
                    logMetricTakeDamage("Cursed Tome", "Stopped", this.damageTaken);
                    this.imageEventText.clearAllDialogs();
                    this.imageEventText.setDialogOption(OPT_LEAVE);
                    this.screen = ModifiedCurseTome.CurScreen.END;
                }
                break;
            case END:
                this.imageEventText.updateDialogOption(0, OPT_LEAVE);
                this.imageEventText.clearRemainingOptions();
                this.openMap();
        }

    }

    private void randomBook() {
        ArrayList<AbstractRelic> possibleBooks = new ArrayList();
        if (!AbstractDungeon.player.hasRelic("Necronomicon")) {
            possibleBooks.add(RelicLibrary.getRelic("Necronomicon").makeCopy());
        }

        if (!AbstractDungeon.player.hasRelic("Enchiridion")) {
            possibleBooks.add(RelicLibrary.getRelic("Enchiridion").makeCopy());
        }

        if (!AbstractDungeon.player.hasRelic("Nilry's Codex")) {
            possibleBooks.add(RelicLibrary.getRelic("Nilry's Codex").makeCopy());
        }

        if (possibleBooks.size() == 0) {
            possibleBooks.add(RelicLibrary.getRelic("Circlet").makeCopy());
        }

        AbstractRelic r = (AbstractRelic)possibleBooks.get(AbstractDungeon.miscRng.random(possibleBooks.size() - 1));
        logMetricTakeDamage("Cursed Tome", "Obtained Book", this.damageTaken);
        AbstractDungeon.getCurrRoom().rewards.clear();
        AbstractDungeon.getCurrRoom().addRelicToRewards(r);
        AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
        AbstractDungeon.combatRewardScreen.open();
        this.screen = ModifiedCurseTome.CurScreen.END;
    }

    static {
        eventStrings = CardCrawlGame.languagePack.getEventString("Cursed Tome");
        NAME = eventStrings.NAME;
        DESCRIPTIONS = eventStrings.DESCRIPTIONS;
        OPTIONS = eventStrings.OPTIONS;
        INTRO_MSG = DESCRIPTIONS[0];
        READ_1 = DESCRIPTIONS[1];
        READ_2 = DESCRIPTIONS[2];
        READ_3 = DESCRIPTIONS[3];
        READ_4 = DESCRIPTIONS[4];
        OBTAIN_MSG = DESCRIPTIONS[5];
        IGNORE_MSG = DESCRIPTIONS[6];
        STOP_MSG = DESCRIPTIONS[7];
        OPT_READ = OPTIONS[0];
        OPT_CONTINUE_1 = OPTIONS[1];
        OPT_CONTINUE_2 = OPTIONS[2];
        OPT_CONTINUE_3 = OPTIONS[3];
        OPT_STOP = OPTIONS[4];
        OPT_LEAVE = OPTIONS[7];
    }

    private static enum CurScreen {
        INTRO,
        PAGE_1,
        PAGE_2,
        PAGE_3,
        LAST_PAGE,
        END;

        private CurScreen() {
        }
    }

}
