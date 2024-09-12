package WarlordEmblem;



import UI.Text.KeyHelper;
import UI.TextureManager;
import WarlordEmblem.EffectTransport.EffectManager;
import WarlordEmblem.EffectTransport.EmptyTransporter;
import WarlordEmblem.EffectTransport.XYTransporter;
import WarlordEmblem.Events.*;
import WarlordEmblem.GameEvent.ModifiedShiningLight;
import WarlordEmblem.GameEvent.ModifiedSkull;
import WarlordEmblem.PVPApi.Communication;
import WarlordEmblem.card.*;
import WarlordEmblem.helpers.FontLibrary;
import WarlordEmblem.helpers.RandMonsterHelper;
import WarlordEmblem.relics.BlockGainer;
import WarlordEmblem.relics.OrangePelletsChange;
import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import basemod.abstracts.CustomRelic;
import basemod.helpers.RelicType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import basemod.BaseMod;
import basemod.interfaces.*;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.TheCity;
import com.megacrit.cardcrawl.dungeons.TheEnding;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.monsters.MonsterInfo;
import com.megacrit.cardcrawl.orbs.Dark;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.ui.panels.energyorb.EnergyOrbBlue;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.Color;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import com.evacipated.cardcrawl.mod.stslib.Keyword;
import WarlordEmblem.patches.CharacterSelectScreenPatches;

import javax.management.monitor.StringMonitor;


@SpireInitializer
public class WarlordEmblem implements
        PostInitializeSubscriber,
        EditCardsSubscriber,
        EditStringsSubscriber,
        AddAudioSubscriber,
        EditRelicsSubscriber,
        EditKeywordsSubscriber,
        PotionGetSubscriber,
        EditCharactersSubscriber{
    public static final Logger logger = LogManager.getLogger(WarlordEmblem.class.getSimpleName());


    public static String MOD_ID = "WarlordEmblem";
    public static String makeID(String id) {
        return MOD_ID + ":" + id;
    }
    public static String assetPath(String path) {
        return MOD_ID + "/" + path;
    }

    public static final String MODNAME = "Warlord Emblem";
    public static final String AUTHOR = "Rita";
    public static final String DESCRIPTION = "Porting Warlord Emblem to latest version.";
    public static String  DK_bgImg = assetPath("img/character/DeathKnight/dk_blood.png");

    public static boolean addonRelic = true;
    public static boolean ringRelic = true;
    public static boolean mantleRelic = true;
    public static boolean RuneCountDisplay = true;
    public static boolean baseGameRelic2DK = true;

    public static Properties WarlordEmblemDefaults = new Properties();

    public static final Color DeathKnight_Color = new Color(0.171F,0.722F,0.722F,1.0F);
    public static final Color BloodRealm_Color = new Color(0.835f,0.25f,0.187f,1.0f);
    public static final Color IceRealm_Color = new Color(0.125F,0.219F,0.633F,1.0F);
    public static final Color EvilRealm_Color = new Color(0.043F,0.875F,0.195F,1.0F);
    public static final Color RuneShadow_Color = new Color(0.0F,0.3F,0.35F,0.8F);
    public static final Color Transparent_Color = new Color(0.0F,0.0F,0.0F,0.0F);

    public WarlordEmblem(){
        logger.debug("Constructor started.");
        BaseMod.subscribe(this);
        //CaseMod.subscribe(this);


        //loadConfig();
        logger.debug("Constructor finished.");
    }

    public static void initialize() {
        logger.info("========================= 开始初始化 =========================");
        new WarlordEmblem();
        logger.info("========================= 初始化完成 =========================");
    }

    public static void loadConfig() {
        logger.debug("===徽章读取设置======");

        System.out.println("load config!!!!!\n\n\n\n\n\n");

        try {
            SpireConfig config = new SpireConfig("WarlordEmblem", "WarlordEmblemSaveData", WarlordEmblemDefaults);
            config.load();
            addonRelic = config.getBool("addonRelic");
            ringRelic = config.getBool("ringRelic");
            mantleRelic = config.getBool("mantleRelic");
            RuneCountDisplay = config.getBool("RuneCountDisplay");
            baseGameRelic2DK = config.getBool("baseGameRelic2DK");
        } catch (Exception e) {
            e.printStackTrace();
            clearConfig();
        }
        logger.debug("===徽章读取设置完成======");
    }

    public static void saveConfig() {
        logger.debug("===徽章存储设置======");

        try {
            SpireConfig config = new SpireConfig("WarlordEmblem", "WarlordEmblemSaveData", WarlordEmblemDefaults);
            config.setBool("addonRelic", addonRelic);
            config.setBool("ringRelic", ringRelic);
            config.setBool("mantleRelic", mantleRelic);
            config.setBool("RuneCountDisplay", RuneCountDisplay);
            config.setBool("baseGameRelic2DK", baseGameRelic2DK);
            config.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("===徽章存储设置完成======");
    }

    public static void clearConfig() {
        saveConfig();
    }


    @Override
    public void receivePostInitialize() {

    }


    @Override
    public void receiveEditCharacters() {
    }



    @Override
    public void receiveAddAudio() {
        //BaseMod.addAudio(this.makeID("VO_Kael_Intimidate"), assetPath("/audio/sound/Kael/VO/嘲讽2.wav"));
    }


    @Override
    public void receiveEditCards() {

        //添加自定义的牌
        List<CustomCard> cards = new ArrayList<>();
        // cards.add(new TimeEat());
        cards.add(new HexCard());
        cards.add(new BurnTransform());
        cards.add(new ComputeTransform());
        cards.add(new VirusTransform());
        cards.add(new FateTransform());
        cards.add(new DoubleSword());
        cards.add(new PainSword());
        cards.add(new ElectronicInterference());
        cards.add(new PsychicSnooping());

        for (CustomCard card : cards) {
            BaseMod.addCard(card);
            UnlockTracker.unlockCard(card.cardID);
        }
    }

    @Override
    public void receivePotionGet(AbstractPotion abstractPotion) {
        //    BaseMod.addPotion(ReserveRunePotion.class,DeathKnight_Color,DeathKnight_Color,DeathKnight_Color,WarlordEmblem.makeID("ReserveRunePotion"),AbstractPlayerEnum.DeathKnight);
    }

    @Override
    public void receiveEditRelics() {
        TextureManager.initTexture();

        //把格挡增益添加到遗物池里面，只是为了方便在哈希表里面找到它
        BaseMod.addRelic(new BlockGainer(),RelicType.SHARED);
        BaseMod.addRelic(new OrangePelletsChange(),RelicType.SHARED);

        //注册修改过的全知头骨的事件
        BaseMod.addEvent(ModifiedSkull.ID,ModifiedSkull.class);
        //修改过的打防之光
        BaseMod.addEvent(ModifiedShiningLight.ID,ModifiedShiningLight.class);
    }



    private Settings.GameLanguage languageSupport()
    {
        switch (Settings.language) {
            case ZHS:
                //return Settings.language;
            case JPN:
                return Settings.language;
            default:
                return Settings.GameLanguage.ENG;
        }
    }
    public void receiveEditStrings()
    {
        Settings.GameLanguage language = languageSupport();

        // Load english first to avoid crashing if translation doesn't exist for something
        loadLocStrings(Settings.GameLanguage.ENG);
        if(!language.equals(Settings.GameLanguage.ENG)) {
            loadLocStrings(language);
        }

    }

    private void loadLocStrings(Settings.GameLanguage language)
    {
        String path = "languages/" + language.toString().toLowerCase() + "/";

        //载入卡牌相关的语言包
        BaseMod.loadCustomStringsFile(CardStrings.class, path + "CardStrings.json");
        //载入buff相关的语言包
        BaseMod.loadCustomStringsFile(PowerStrings.class, path + "PowerStrings.json");
        //遗物相关的语言包
        BaseMod.loadCustomStringsFile(RelicStrings.class, path + "RelicStrings.json");
        //用户界面相关的语言包
        BaseMod.loadCustomStringsFile(UIStrings.class, path + "UIStrings.json");

        //注册事件
        Communication.registerEvent(new AddMonsterEvent());
        Communication.registerEvent(new MonsterIntentChangeEvent());
        Communication.registerEvent(new MonsterDamageEvent());
        Communication.registerEvent(new DamageOnMonsterEvent());
        Communication.registerEvent(new VFXEffectEvent());
        Communication.registerEvent(new ChatMessageEvent(null));
        Communication.registerEvent(new RegisterPlayerEvent());
        Communication.registerEvent(new AssignTeamEvent());
        Communication.registerEvent(new ExecuteAssignTeamEvent(-1));
        Communication.registerEvent(new ConfigReadyEvent(false));
        Communication.registerEvent(new BattleInfoEvent());
        Communication.registerEvent(new EndTurnEvent());
        Communication.registerEvent(new ChannelOrbEvent(null));
        Communication.registerEvent(new EvokeOrbEvent());
        Communication.registerEvent(new IncreaseOrbSlotEvent(0));
        Communication.registerEvent(new ChangeStanceEvent(null));
        Communication.registerEvent(new JumpTurnEvent());
        Communication.registerEvent(new HealEvent(0));
        Communication.registerEvent(new PlayerTurnBegin());
        Communication.registerEvent(new CardInfoEvent(null,0));
        Communication.registerEvent(new UseCardEvent(0));
        Communication.registerEvent(new DeadEvent());
        Communication.registerEvent(new TransformCardEvent(null,0,0,null));
        Communication.registerEvent(new EndOfRoundEvent());
        Communication.registerEvent(new UpdateHandCardEvent(null));
        Communication.registerEvent(new DrawCardUpdateEvent(null));
        Communication.registerEvent(new ChangeTeamEvent(0));
        Communication.registerEvent(new UpdateCharacterEvent(null));
        Communication.registerEvent(new UpdateEnergyEvent(0));
        Communication.registerEvent(new RemoveCardEvent(0,0));
        Communication.registerEvent(new MelterEvent(null));
        Communication.registerEvent(new PlayerRelicEvent());
        Communication.registerEvent(new PlayerPotionEvent());
        Communication.registerEvent(new DelayRequestEvent(0,0));
        Communication.registerEvent(new DelayResponseEvent(0,0));
        Communication.registerEvent(new PlayerSeatEvent(null,0));
        Communication.registerEvent(new EnterBattleEvent());
        Communication.registerEvent(new BeginTurnEvent(0));
        Communication.registerEvent(new BeginTurnResponseEvent());

        FontLibrary.getBaseFont();
        FontLibrary.getFontWithSize(24);

        //初始化随机怪物的事件
        RandMonsterHelper.initMonsterList();

        EffectManager effectManager = GlobalManager.effectManager;
        //注册通用特效
        effectManager.registerNewTransporter(new XYTransporter());
        effectManager.registerNewTransporter(new EmptyTransporter());

        //注册输入框禁用按钮
        KeyHelper.initKeys();
    }


    private void loadLocKeywords(Settings.GameLanguage language)
    {
//        String path = "localization/" + language.toString().toLowerCase() + "/";
//        Gson gson = new Gson();
//        //String json = Gdx.files.internal(assetPath(path + "KeywordStrings.json")).readString(String.valueOf(StandardCharsets.UTF_8));
//        Keyword[] keywords = gson.fromJson(json, Keyword[].class);
//
//        logger.info("========================= 开始加载关键字 =========================");
//        if (keywords != null) {
//            for (Keyword keyword : keywords) {
//                BaseMod.addKeyword("warlord_emblem", keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
//            }
//        }
    }

    @Override
    public void receiveEditKeywords()
    {

        Settings.GameLanguage language = languageSupport();

        // Load english first to avoid crashing if translation doesn't exist for something
        loadLocKeywords(Settings.GameLanguage.ENG);
        if(!language.equals(Settings.GameLanguage.ENG)) {
            loadLocKeywords(language);
        }


    }
}