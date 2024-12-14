package pvp_in_the_spire;



import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglFileHandle;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.google.gson.Gson;
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

import java.nio.charset.StandardCharsets;
import java.util.*;

import org.scannotation.AnnotationDB;
import pvp_in_the_spire.util.KeywordInfo;


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

    private static final String resourcesFolder = checkResourcesPath();
    public static final Logger logger = LogManager.getLogger(PvPInTheSpireMod.class.getSimpleName());
    public static String makeID(String id) {
        return modID + ":" + id;
    }

    public static void initialize() {
        new PvPInTheSpireMod();
    }

    public PvPInTheSpireMod(){
        BaseMod.subscribe(this);
        logger.debug(modID + "subscribed to BaseMod.");
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

    @Override
    public void receiveEditCharacters() {

    }

    @Override
    public void receiveAddAudio() {

    }

    @Override
    public void receivePotionGet(AbstractPotion abstractPotion) {

    }

    @Override
    public void receivePostInitialize() {

    }

    private static String getLangString() {
        return Settings.language.name().toLowerCase();
    }
    private static final String defaultLanguage = Settings.GameLanguage.ENG.toString().toLowerCase();
    public static final Map<String, KeywordInfo> keywords = new HashMap<>();

    public void receiveEditStrings() {
        /*
            First, load the default localization.
            Then, if the current language is different, attempt to load localization for that language.
            This results in the default localization being used for anything that might be missing.
            The same process is used to load keywords slightly below.
        */
        loadLocalization(defaultLanguage);
        if (!defaultLanguage.equals(getLangString())) {
            try {
                loadLocalization(getLangString());
            }
            catch (GdxRuntimeException e) {
                e.printStackTrace();
            }
        }

        /*
        TODO: This method replaced the block of code that "initializes" the pvp (setting communication events,
         fonts, effects and some other stuff). Could we perhaps split this method into more appropriate parts, so
         we can initialize each piece of code at the appropriate time, instead of initializing it all at the
         receiveEditStrings() method?
         */
        initializePVP();
    }

    private void loadLocalization(String lang) {
        //While this does load every type of localization, most of these files are just outlines so that you can see how they're formatted.
        //Feel free to comment out/delete any that you don't end up using.
        BaseMod.loadCustomStringsFile(CardStrings.class,
                localizationPath(lang, "CardStrings.json"));
        BaseMod.loadCustomStringsFile(CharacterStrings.class,
                localizationPath(lang, "CharacterStrings.json"));
        BaseMod.loadCustomStringsFile(EventStrings.class,
                localizationPath(lang, "EventStrings.json"));
        BaseMod.loadCustomStringsFile(OrbStrings.class,
                localizationPath(lang, "OrbStrings.json"));
        BaseMod.loadCustomStringsFile(PotionStrings.class,
                localizationPath(lang, "PotionStrings.json"));
        BaseMod.loadCustomStringsFile(PowerStrings.class,
                localizationPath(lang, "PowerStrings.json"));
        BaseMod.loadCustomStringsFile(RelicStrings.class,
                localizationPath(lang, "RelicStrings.json"));
        BaseMod.loadCustomStringsFile(UIStrings.class,
                localizationPath(lang, "UIStrings.json"));
    }


    @Override
    public void receiveEditKeywords()
    {
        Gson gson = new Gson();
        String json = Gdx.files.internal(localizationPath(defaultLanguage, "Keywords.json")).readString(String.valueOf(StandardCharsets.UTF_8));
        KeywordInfo[] keywords = gson.fromJson(json, KeywordInfo[].class);
        for (KeywordInfo keyword : keywords) {
            keyword.prep();
            registerKeyword(keyword);
        }

        if (!defaultLanguage.equals(getLangString())) {
            try
            {
                json = Gdx.files.internal(localizationPath(getLangString(), "Keywords.json")).readString(String.valueOf(StandardCharsets.UTF_8));
                keywords = gson.fromJson(json, KeywordInfo[].class);
                for (KeywordInfo keyword : keywords) {
                    keyword.prep();
                    registerKeyword(keyword);
                }
            }
            catch (Exception e)
            {
                logger.warn(modID + " does not support " + getLangString() + " keywords.");
            }
        }
    }

    private void registerKeyword(KeywordInfo info) {
        BaseMod.addKeyword(modID.toLowerCase(), info.PROPER_NAME, info.NAMES, info.DESCRIPTION);
        if (!info.ID.isEmpty())
        {
            keywords.put(info.ID, info);
        }
    }

    //These methods are used to generate the correct filepaths to various parts of the resources folder.
    public static String localizationPath(String lang, String file) {
        return resourcesFolder + "/localization/" + lang + "/" + file;
    }

    public static String imagePath(String file) {
        return resourcesFolder + "/images/" + file;
    }
    public static String characterPath(String file) {
        return resourcesFolder + "/images/character/" + file;
    }
    public static String powerPath(String file) {
        return resourcesFolder + "/images/powers/" + file;
    }
    public static String relicPath(String file) {
        return resourcesFolder + "/images/relics/" + file;
    }

    /**
     * Checks the expected resources path based on the package name.
     */
    private static String checkResourcesPath() {
        String name = PvPInTheSpireMod.class.getName(); //getPackage can be iffy with patching, so class name is used instead.
        int separator = name.indexOf('.');
        if (separator > 0)
            name = name.substring(0, separator);

        FileHandle resources = new LwjglFileHandle(name, Files.FileType.Internal);

        if (!resources.exists()) {
            throw new RuntimeException("\n\tFailed to find resources folder; expected it to be named \"" + name + "\"." +
                    " Either make sure the folder under resources has the same name as your mod's package, or change the line\n" +
                    "\t\"private static final String resourcesFolder = checkResourcesPath();\"\n" +
                    "\tat the top of the " + PvPInTheSpireMod.class.getSimpleName() + " java file.");
        }
        if (!resources.child("images").exists()) {
            throw new RuntimeException("\n\tFailed to find the 'images' folder in the mod's 'resources/" + name + "' folder; Make sure the " +
                    "images folder is in the correct location.");
        }
        if (!resources.child("localization").exists()) {
            throw new RuntimeException("\n\tFailed to find the 'localization' folder in the mod's 'resources/" + name + "' folder; Make sure the " +
                    "localization folder is in the correct location.");
        }

        return name;
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


    public static void initializePVP() {
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
        Communication.registerEvent(new BanCardStageChangeEvent(null,false));
        Communication.registerEvent(new BanCardStream(null,false));

        FontLibrary.getBaseFont();
        FontLibrary.getFontWithSize(24);
        FontLibrary.getFontWithSize(40);
        FontLibrary.getFontWithSize(34);

        //初始化随机怪物的事件
        RandMonsterHelper.initMonsterList();

        EffectManager effectManager = GlobalManager.effectManager;
        //注册通用特效
        effectManager.registerNewTransporter(new XYTransporter());
        effectManager.registerNewTransporter(new EmptyTransporter());

        //注册输入框禁用按钮
        KeyHelper.initKeys();
    }

    //TODO: Can this commented code be removed? - Luc
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
}