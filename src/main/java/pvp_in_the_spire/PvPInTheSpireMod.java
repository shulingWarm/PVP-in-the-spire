package pvp_in_the_spire;



import pvp_in_the_spire.ui.Text.KeyHelper;
import pvp_in_the_spire.ui.TextureManager;
import pvp_in_the_spire.effect_transport.EffectManager;
import pvp_in_the_spire.effect_transport.EmptyTransporter;
import pvp_in_the_spire.effect_transport.XYTransporter;
import pvp_in_the_spire.events.*;
import pvp_in_the_spire.game_event.ModifiedCurseTome;
import pvp_in_the_spire.game_event.ModifiedShiningLight;
import pvp_in_the_spire.game_event.ModifiedSkull;
import pvp_in_the_spire.pvp_api.Communication;
import pvp_in_the_spire.card.*;
import pvp_in_the_spire.helpers.FontLibrary;
import pvp_in_the_spire.helpers.RandMonsterHelper;
import pvp_in_the_spire.relics.*;
import basemod.helpers.RelicType;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.Patcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import basemod.BaseMod;
import basemod.interfaces.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import basemod.abstracts.CustomCard;

import java.util.*;

import org.scannotation.AnnotationDB;


@SpireInitializer
public class PvPInTheSpireMod implements
        EditStringsSubscriber,
        EditKeywordsSubscriber,
        PostInitializeSubscriber,
        EditCardsSubscriber,
        EditRelicsSubscriber,
        PotionGetSubscriber,
        EditCharactersSubscriber,
        AddAudioSubscriber {

    public static ModInfo info;
    public static String modID;
    static { loadModInfo(); }

    public static final Logger logger = LogManager.getLogger(PvPInTheSpireMod.class.getSimpleName());


    public static String makeID(String id) {
        return modID + ":" + id;
    }

    public static void initialize() {
        logger.info("========================= 开始初始化 =========================");
        new PvPInTheSpireMod();
        logger.info("========================= 初始化完成 =========================");
    }

    public PvPInTheSpireMod(){
        BaseMod.subscribe(this);
        logger.debug(modID + "subscribed to BaseMod.");
    }

    //TODO: What are these booleans for? Can these be removed? - Luc
    public static boolean addonRelic = true;
    public static boolean ringRelic = true;
    public static boolean mantleRelic = true;
    public static boolean RuneCountDisplay = true;
    public static boolean baseGameRelic2DK = true;

    public static Properties WarlordEmblemDefaults = new Properties();



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
        cards.add(new MultiplayerTimeWarp());

        for (CustomCard card : cards) {
            BaseMod.addCard(card);
            UnlockTracker.unlockCard(card.cardID);
        }
    }

    @Override
    public void receiveEditRelics() {
        TextureManager.initTexture();

        //把格挡增益添加到遗物池里面，只是为了方便在哈希表里面找到它
        BaseMod.addRelic(new BlockGainer(),RelicType.SHARED);
        BaseMod.addRelic(new OrangePelletsChange(),RelicType.SHARED);
        BaseMod.addRelic(new PVPVelvetChoker(),RelicType.SHARED);
        BaseMod.addRelic(new PVPSozu(),RelicType.SHARED);
        BaseMod.addRelic(new PVPEctoplasm(),RelicType.SHARED);
        BaseMod.removeRelic(new Ectoplasm());
        BaseMod.removeRelic(new Sozu());
        BaseMod.removeRelic(new VelvetChoker());
        UnlockTracker.markRelicAsSeen(PVPVelvetChoker.ID);
        UnlockTracker.markRelicAsSeen(PVPSozu.ID);
        UnlockTracker.markRelicAsSeen(PVPEctoplasm.ID);

        //注册修改过的全知头骨的事件
        BaseMod.addEvent(ModifiedSkull.ID,ModifiedSkull.class);
        //修改过的打防之光
        BaseMod.addEvent(ModifiedShiningLight.ID,ModifiedShiningLight.class);
        BaseMod.addEvent(ModifiedCurseTome.ID,ModifiedCurseTome.class);
    }




    public static void loadConfig() {
        logger.debug("===徽章读取设置======");

        System.out.println("load config!!!!!\n\n\n\n\n\n");

        try {
            SpireConfig config = new SpireConfig("pvp_in_the_spire", "WarlordEmblemSaveData", WarlordEmblemDefaults);
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
            SpireConfig config = new SpireConfig("pvp_in_the_spire", "WarlordEmblemSaveData", WarlordEmblemDefaults);
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

    }


    @Override
    public void receivePotionGet(AbstractPotion abstractPotion) {

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
        Communication.registerEvent(new KillEvent(0));
        Communication.registerEvent(new RemovePowerEvent(0,0));
        Communication.registerEvent(new SetPowerAmountEvent(0,0,0));
        Communication.registerEvent(new ApplyComPowerEvent(null));
        Communication.registerEvent(new LoseGoldEvent(0,0,0));
        Communication.registerEvent(new ToggleTriggerEvent(0,false));

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

    /**
     * This determines the mod's ID based on information stored by ModTheSpire.
     */
    private static void loadModInfo() {
        Optional<ModInfo> infos = Arrays.stream(Loader.MODINFOS).filter((modInfo)->{
            AnnotationDB annotationDB = Patcher.annotationDBMap.get(modInfo.jarURL);
            if (annotationDB == null)
                return false;
            Set<String> initializers = annotationDB.getAnnotationIndex().getOrDefault(SpireInitializer.class.getName(), Collections.emptySet());
            return initializers.contains(PvPInTheSpireMod.class.getName());
        }).findFirst();
        if (infos.isPresent()) {
            info = infos.get();
            modID = info.ID;
        }
        else {
            throw new RuntimeException("Failed to determine mod info/ID based on initializer.");
        }
    }
}