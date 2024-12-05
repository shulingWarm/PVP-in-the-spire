package pvp_in_the_spire.dungeon;

import pvp_in_the_spire.SocketServer;
import pvp_in_the_spire.game_event.ModifiedCurseTome;
import pvp_in_the_spire.game_event.ModifiedShiningLight;
import pvp_in_the_spire.game_event.ModifiedSkull;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.SaveHelper;
import com.megacrit.cardcrawl.helpers.TipTracker;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapGenerator;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.monsters.MonsterInfo;
import com.megacrit.cardcrawl.neow.NeowRoom;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile.SaveType;
import com.megacrit.cardcrawl.scenes.TheEndingScene;
import pvp_in_the_spire.ui.CardFilter.CardFilter;
import pvp_in_the_spire.ui.CardFilter.CardFilterScreen;

import java.util.ArrayList;

public class FakeEnding extends AbstractDungeon {
    private static final UIStrings uiStrings;
    public static final String[] TEXT;
    public static final String NAME;
    public static final String ID = "Exordium";

    //最大层数，从0层开始计算
    public static int ROW_NUM = 5;

    public FakeEnding(AbstractPlayer p, ArrayList<String> emptyList) {
        super(NAME, "Exordium", p, emptyList);
        this.initializeRelicList();
        if (Settings.isEndless) {
            if (floorNum <= 1) {
                blightPool.clear();
                blightPool = new ArrayList();
            }
        } else {
            blightPool.clear();
        }

        if (scene != null) {
            scene.dispose();
        }

        scene = new TheEndingScene();
        fadeColor = Color.valueOf("140a1eff");
        sourceFadeColor = Color.valueOf("140a1eff");
        this.initializeLevelSpecificChances();
        mapRng = new Random(Settings.seed + (long)AbstractDungeon.actNum);
        this.generateMapV2();
        //手动修改的背景音乐
        CardCrawlGame.music.changeBGM("TheEnding");
        AbstractDungeon.currMapNode = new MapRoomNode(0, -1);
        if (!Settings.isShowBuild && (Boolean)TipTracker.tips.get("NEOW_SKIP")) {
            AbstractDungeon.currMapNode.room = new NeowRoom(false);
            if (AbstractDungeon.floorNum > 1) {
                SaveHelper.saveIfAppropriate(SaveType.ENDLESS_NEOW);
            } else {
                SaveHelper.saveIfAppropriate(SaveType.ENTER_ROOM);
            }
        } else {
            AbstractDungeon.currMapNode.room = new EmptyRoom();
        }

    }

    public FakeEnding(AbstractPlayer p, SaveFile saveFile) {
        super(NAME, p, saveFile);
        CardCrawlGame.dungeon = this;
        if (scene != null) {
            scene.dispose();
        }

        scene = new TheEndingScene();
        fadeColor = Color.valueOf("140a1eff");
        sourceFadeColor = Color.valueOf("140a1eff");
        this.initializeLevelSpecificChances();
        miscRng = new Random(Settings.seed + (long)saveFile.floor_num);
        CardCrawlGame.music.changeBGM(id);
        mapRng = new Random(Settings.seed + (long)(saveFile.act_num * 300));
        generateMapV2();
        firstRoomChosen = true;
        //关键就是调用了它才进入了涅奥房间
        this.populatePathTaken(saveFile);
        if (this.isLoadingIntoNeow(saveFile)) {
            AbstractDungeon.firstRoomChosen = false;
        }

    }

    //生成假的地图的一个依赖
    private void connectNode(MapRoomNode src, MapRoomNode dst) {
        src.addEdge(new MapEdge(src.x, src.y, src.offsetX, src.offsetY, dst.x, dst.y, dst.offsetX, dst.offsetY, false));
    }

    //随机判断是否需要交换3层的火堆和问号的位置
    public static boolean randomJudge()
    {
        return AbstractDungeon.cardRng.random(100)<50;
    }

    //第二种生成地图的模式
    public void generateMapV2()
    {
        //生成地图的工具
        MapDescription mapDescription = new MapDescription();
        //添加地图 1层的商店
        mapDescription.addNode(MapDescription.RANDOM_NODE,0,0);
        mapDescription.addNode(MapDescription.SHOP_NODE,0,1);
        mapDescription.addNode(MapDescription.RANDOM_NODE,0,2);
        //2层左边宝箱，右边商店
        mapDescription.addNode(MapDescription.RELIC_NODE,1,0);
        mapDescription.addNode(MapDescription.RANDOM_NODE,1,1);
        mapDescription.addNode(MapDescription.SHOP_NODE,1,2);
        //最后一层全是火堆
        mapDescription.addNode(MapDescription.REST_NODE,ROW_NUM-1,0);
        mapDescription.addNode(MapDescription.REST_NODE,ROW_NUM-1,1);
        mapDescription.addNode(MapDescription.REST_NODE,ROW_NUM-1,2);
        //倒数第2层中间火堆，两边随机
        mapDescription.addNode(MapDescription.RANDOM_NODE,ROW_NUM-2,0);
        mapDescription.addNode(MapDescription.REST_NODE,ROW_NUM-2,1);
        mapDescription.addNode(MapDescription.RANDOM_NODE,ROW_NUM-2,2);
        //剩下的全是随机的
        for(int idRow=2;idRow<=ROW_NUM-3;++idRow)
        {
            mapDescription.addNode(MapDescription.RANDOM_NODE,idRow,0);
            mapDescription.addNode(MapDescription.RANDOM_NODE,idRow,1);
            mapDescription.addNode(MapDescription.RANDOM_NODE,idRow,2);
        }
        //生成地图
        map = mapDescription.generateMap();
        firstRoomChosen = false;
        fadeIn();
    }

    //特殊的生成地图，从ending那里抄来的
    private void generateSpecialMap() {
        long startTime = System.currentTimeMillis();
        map = new ArrayList();
        ArrayList<MapRoomNode> row1 = new ArrayList();
        MapRoomNode shopNode = new MapRoomNode(3, 0);
        //临时设置成事件房间
        shopNode.room = new ShopRoom();
        //第2个商店
        MapRoomNode shopNode2 = new MapRoomNode(4,1);
        shopNode2.room = new ShopRoom();
        //第2个商店旁边的宝箱房
        MapRoomNode treasureNode = new MapRoomNode(2,1);
        treasureNode.room = new TreasureRoom();
        //商店后面的火堆
        MapRoomNode restNode = new MapRoomNode(4, 2);
        restNode.room = new RestRoom();
        //商店后面的问号房间
        MapRoomNode eventNode = new MapRoomNode(2,2);
        eventNode.room = new EventRoom();
        if(randomJudge())
        {
            AbstractRoom tempRoom = restNode.room;
            restNode.room = eventNode.room;
            eventNode.room = tempRoom;
        }
        MapRoomNode restNode2 = new MapRoomNode(3, 3);
        restNode2.room = new RestRoom();
        MapRoomNode bossNode = new MapRoomNode(3, 4);
        bossNode.room = new MonsterRoomBoss();
        MapRoomNode victoryNode = new MapRoomNode(3, 5);
        victoryNode.room = new TrueVictoryRoom();
        this.connectNode(shopNode, shopNode2);
        //把第1个商店和宝箱房相连
        this.connectNode(shopNode,treasureNode);
        this.connectNode(shopNode2,restNode);
        //宝箱房和问号房间相连
        this.connectNode(treasureNode,eventNode);
        //宝箱房连接最后一个火堆
        this.connectNode(eventNode,restNode2);
        this.connectNode(restNode, restNode2);
        restNode2.addEdge(new MapEdge(restNode2.x, restNode2.y, restNode2.offsetX, restNode2.offsetY, bossNode.x, bossNode.y, bossNode.offsetX, bossNode.offsetY, false));
        row1.add(new MapRoomNode(0, 0));
        row1.add(new MapRoomNode(1, 0));
        row1.add(new MapRoomNode(2, 0));
        row1.add(shopNode);
        row1.add(new MapRoomNode(4, 0));
        row1.add(new MapRoomNode(5, 0));
        row1.add(new MapRoomNode(6, 0));
        ArrayList<MapRoomNode> row2 = new ArrayList();
        row2.add(new MapRoomNode(0, 1));
        row2.add(new MapRoomNode(1, 1));
        //并排放置
        row2.add(treasureNode);
        row2.add(new MapRoomNode(2, 1));
        row2.add(new MapRoomNode(4, 1));
        row2.add(shopNode2);
        row2.add(new MapRoomNode(5, 1));
        row2.add(new MapRoomNode(6, 1));
        ArrayList<MapRoomNode> row3 = new ArrayList();
        row3.add(new MapRoomNode(0, 2));
        row3.add(new MapRoomNode(1, 2));
        //问号房间
        row3.add(eventNode);
        row3.add(new MapRoomNode(2, 2));
        row3.add(new MapRoomNode(4, 2));
        row3.add(restNode);
        row3.add(new MapRoomNode(5, 2));
        row3.add(new MapRoomNode(6, 2));
        ArrayList<MapRoomNode> row4 = new ArrayList();
        row4.add(new MapRoomNode(0, 3));
        row4.add(new MapRoomNode(1, 3));
        row4.add(new MapRoomNode(2, 3));
        row4.add(restNode2);
        row4.add(new MapRoomNode(4, 3));
        row4.add(new MapRoomNode(5, 3));
        row4.add(new MapRoomNode(6, 3));
        ArrayList<MapRoomNode> row5 = new ArrayList();
        row5.add(new MapRoomNode(0, 4));
        row5.add(new MapRoomNode(1, 4));
        row5.add(new MapRoomNode(2, 4));
        row5.add(bossNode);
        row5.add(new MapRoomNode(4, 4));
        row5.add(new MapRoomNode(5, 4));
        row5.add(new MapRoomNode(6, 4));
        ArrayList<MapRoomNode> row6 = new ArrayList();
        row6.add(new MapRoomNode(0, 5));
        row6.add(new MapRoomNode(1, 5));
        row6.add(new MapRoomNode(2, 5));
        row6.add(victoryNode);
        row6.add(new MapRoomNode(4, 5));
        row6.add(new MapRoomNode(5, 5));
        row6.add(new MapRoomNode(6, 5));
        map.add(row1);
        map.add(row2);
        map.add(row3);
        map.add(row4);
        map.add(row5);
        map.add(row6);
        logger.info("Generated the following dungeon map:");
        logger.info(MapGenerator.toString(map, true));
        logger.info("Game Seed: " + Settings.seed);
        logger.info("Map generation time: " + (System.currentTimeMillis() - startTime) + "ms");
        firstRoomChosen = false;
        fadeIn();
    }

    protected void initializeLevelSpecificChances() {
        shopRoomChance = 0.05F;
        restRoomChance = 0.12F;
        treasureRoomChance = 0.0F;
        eventRoomChance = 0.22F;
        eliteRoomChance = 0.08F;
        smallChestChance = 0;
        mediumChestChance = 100;
        largeChestChance = 0;
        commonRelicChance = 0;
        uncommonRelicChance = 100;
        rareRelicChance = 0;
        colorlessRareChance = 0.3F;
        if (AbstractDungeon.ascensionLevel >= 12) {
            cardUpgradedChance = 0.25F;
        } else {
            cardUpgradedChance = 0.5F;
        }
    }

    protected void generateMonsters() {
        this.generateWeakEnemies(3);
        this.generateStrongEnemies(12);
        this.generateElites(10);
    }

    protected void generateWeakEnemies(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList();
        monsters.add(new MonsterInfo("Cultist", 2.0F));
        monsters.add(new MonsterInfo("Jaw Worm", 2.0F));
        monsters.add(new MonsterInfo("2 Louse", 2.0F));
        monsters.add(new MonsterInfo("Small Slimes", 2.0F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateMonsterList(monsters, count, false);
    }

    protected void generateStrongEnemies(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList();
        monsters.add(new MonsterInfo("Blue Slaver", 2.0F));
        monsters.add(new MonsterInfo("Gremlin Gang", 1.0F));
        monsters.add(new MonsterInfo("Looter", 2.0F));
        monsters.add(new MonsterInfo("Large Slime", 2.0F));
        monsters.add(new MonsterInfo("Lots of Slimes", 1.0F));
        monsters.add(new MonsterInfo("Exordium Thugs", 1.5F));
        monsters.add(new MonsterInfo("Exordium Wildlife", 1.5F));
        monsters.add(new MonsterInfo("Red Slaver", 1.0F));
        monsters.add(new MonsterInfo("3 Louse", 2.0F));
        monsters.add(new MonsterInfo("2 Fungi Beasts", 2.0F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateFirstStrongEnemy(monsters, this.generateExclusions());
        this.populateMonsterList(monsters, count, false);
    }

    protected void generateElites(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList();
        monsters.add(new MonsterInfo("Gremlin Nob", 1.0F));
        monsters.add(new MonsterInfo("Lagavulin", 1.0F));
        monsters.add(new MonsterInfo("3 Sentries", 1.0F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateMonsterList(monsters, count, true);
    }

    protected ArrayList<String> generateExclusions() {
        ArrayList<String> retVal = new ArrayList();
        switch ((String)monsterList.get(monsterList.size() - 1)) {
            case "Looter":
                retVal.add("Exordium Thugs");
            case "Jaw Worm":
            case "Cultist":
            default:
                break;
            case "Blue Slaver":
                retVal.add("Red Slaver");
                retVal.add("Exordium Thugs");
                break;
            case "2 Louse":
                retVal.add("3 Louse");
                break;
            case "Small Slimes":
                retVal.add("Large Slime");
                retVal.add("Lots of Slimes");
        }

        return retVal;
    }

    protected void initializeBoss() {
        bossList.add("The Heart");
        bossList.add("The Heart");
        bossList.add("The Heart");
    }

    protected void initializeEventList() {
        //如果初始化过就不用初始化了
        if(!eventList.isEmpty())
            return;
        eventList.add(ModifiedShiningLight.ID);//打防之光
        eventList.add(ModifiedSkull.ID);
        eventList.add("Addict");
        eventList.add("Bonfire Elementals");//小精灵
        eventList.add("Duplicator");
        eventList.add("Designer");
        eventList.add("FaceTrader");
        eventList.add("Golden Shrine");
        eventList.add("Match and Keep!");
        eventList.add("Accursed Blacksmith");
        eventList.add("Lab");
        eventList.add("Purifier");
        eventList.add("Transmorgrifier");
        eventList.add("Wheel of Change");
        eventList.add("Upgrade Shrine");
        eventList.add("WeMeetAgain");
        eventList.add("The Woman in Blue");
        eventList.add("Big Fish");
        eventList.add("The Cleric");
        eventList.add("Golden Idol");//金神像 血神像:Forgotten Altar
        eventList.add("Golden Wing");
        eventList.add("World of Goop");
        eventList.add("Scrap Ooze");
        eventList.add("Liars Game");
        eventList.add("Living Wall");
        eventList.add("Vampires");
        //eventList.add("Ghosts"); 灵体事件
        eventList.add("Addict");
        eventList.add("Back to Basics");
        eventList.add("Beggar");
        eventList.add(ModifiedCurseTome.ID);
        eventList.add("Drug Dealer");
        // eventList.add("Knowing Skull"); 全知头骨
        eventList.add("Nest");
        eventList.add("The Library");
        eventList.add("The Mausoleum");
        eventList.add("The Joust");
        eventList.add("Tomb of Lord Red Mask");
        eventList.add("Falling");
        eventList.add("Winding Halls");
        eventList.add("The Moai Head");
        eventList.add("SensoryStone");
    }

    protected void initializeShrineList() {
        shrineList.add("Match and Keep!");
        shrineList.add("Golden Shrine");
        shrineList.add("Transmorgrifier");
        shrineList.add("Purifier");
        shrineList.add("Upgrade Shrine");
        shrineList.add("Wheel of Change");
    }

    protected void initializeEventImg() {
        if (eventBackgroundImg != null) {
            eventBackgroundImg.dispose();
            eventBackgroundImg = null;
        }

        eventBackgroundImg = ImageMaster.loadImage("images/ui/event/panel.png");
    }

    @Override
    public void initializeCardPools() {
        if(!(AbstractDungeon.commonCardPool.isEmpty() || SocketServer.battleNum == 0))
            return;
        super.initializeCardPools();
        CardFilter filter = CardFilterScreen.instance.cardFilter;
        AbstractDungeon.commonCardPool.group=filter.filterCard(AbstractDungeon.commonCardPool.group);
        AbstractDungeon.uncommonCardPool.group=filter.filterCard(AbstractDungeon.uncommonCardPool.group);
        AbstractDungeon.rareCardPool.group=filter.filterCard(AbstractDungeon.rareCardPool.group);
        AbstractDungeon.colorlessCardPool.group=filter.filterCard(AbstractDungeon.colorlessCardPool.group);
        AbstractDungeon.curseCardPool.group=filter.filterCard(AbstractDungeon.curseCardPool.group);
        AbstractDungeon.srcCommonCardPool.group=filter.filterCard(AbstractDungeon.srcCommonCardPool.group);
        AbstractDungeon.srcUncommonCardPool.group=filter.filterCard(AbstractDungeon.srcUncommonCardPool.group);
        AbstractDungeon.srcRareCardPool.group=filter.filterCard(AbstractDungeon.srcRareCardPool.group);
        AbstractDungeon.srcColorlessCardPool.group=filter.filterCard(AbstractDungeon.srcColorlessCardPool.group);
        AbstractDungeon.srcCurseCardPool.group=filter.filterCard(AbstractDungeon.srcCurseCardPool.group);
    }

    static {
        uiStrings = CardCrawlGame.languagePack.getUIString("Exordium");
        TEXT = uiStrings.TEXT;
        NAME = TEXT[0];
    }
}
