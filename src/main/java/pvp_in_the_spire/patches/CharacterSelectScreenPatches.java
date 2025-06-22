package pvp_in_the_spire.patches;

import com.megacrit.cardcrawl.cards.blue.Strike_Blue;
import pvp_in_the_spire.actions.PsychicSnoopingAction;
import pvp_in_the_spire.card.BurnTransform;
import pvp_in_the_spire.card.PsychicSnooping;
import pvp_in_the_spire.dungeon.FakeEnding;
import pvp_in_the_spire.events.BattleInfoEvent;
import pvp_in_the_spire.events.EndTurnEvent;
import pvp_in_the_spire.events.PlayerPotionEvent;
import pvp_in_the_spire.events.PlayerRelicEvent;
import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.Communication;
import pvp_in_the_spire.screens.FakeSettingScreen;
import pvp_in_the_spire.screens.midExit.MidExitScreen;
import pvp_in_the_spire.actions.MultiPauseAction;
import pvp_in_the_spire.patches.CardShowPatch.UseCardSend;
import pvp_in_the_spire.powers.InvincibleAtStartPower;
import pvp_in_the_spire.relics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.LoseEnergyAction;
import com.megacrit.cardcrawl.actions.watcher.PressEndTurnButtonAction;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.red.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.map.DungeonMap;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.SmokeBomb;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.screens.GameOverScreen;
import com.megacrit.cardcrawl.screens.options.SettingsScreen;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.vfx.combat.SmokeBombEffect;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

//序章那一节的怪物池
import com.megacrit.cardcrawl.dungeons.Exordium;

//自定义的可控制的怪物
import pvp_in_the_spire.character.ControlMoster;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;

import com.megacrit.cardcrawl.neow.NeowEvent;
import pvp_in_the_spire.AutomaticSocketServer;
import pvp_in_the_spire.SocketServer;
import com.megacrit.cardcrawl.vfx.EnemyTurnEffect;

import com.megacrit.cardcrawl.potions.FairyPotion;


public class CharacterSelectScreenPatches
{
    public static int TalentCount = 1;

    public static Hitbox TalentRight;
    public static Hitbox TalentLeft;

    private static float X_fixed = 30.0f *Settings.scale;

    public static Field charInfoField;

    //卡伤害action,当这个开关打开的时候，禁止某个伤害发生作用
//    @SpirePatch(clz = DamageAction.class, method = "update")
//    public static class DamageActionPause
//    {
//
//        public static DamageAction heldAction = null;
//        //是否放行结束位
//        public static int releaseFlag = 0;
//
//        //释放特殊的dmageAction
//        public static void releaseNewAction()
//        {
//            //增加一个放行数量
//            ++releaseFlag;
//        }
//
//        //获得action里面的info,这个怕是会经常要用
//        public static DamageInfo getInfoFromDamageAction(DamageAction action)
//        {
//            try
//            {
//                Field tempField = DamageAction.class.getDeclaredField("info");
//                tempField.setAccessible(true);
//                return (DamageInfo) tempField.get(action);
//            }
//            catch (NoSuchFieldException | IllegalAccessException e)
//            {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        //用于判断一个action是否需要被卡住
//        public static boolean judgeWait(DamageAction __instance)
//        {
//            //如果它在通行证的列表里面，那是不用等待的
//            //通行证列表里面只会有一个人
//            if(__instance == heldAction)
//                return false;
//            //目标伤害必须能触发静电释放才行
//            DamageInfo info = getInfoFromDamageAction(__instance);
//            //如果是反伤型的伤害是不用处理的
//            if(info.type == DamageInfo.DamageType.THORNS)
//                return false;
//            //只有当目标是敌人并且有静电释放的时候才处理
//            return __instance.target instanceof ControlMoster &&
//                    __instance.target.hasPower(StaticDischargePower.POWER_ID);
//        }
//
//        @SpirePrefixPatch
//        public static SpireReturn<Void> fix(DamageAction __instance)
//        {
//            //判断能否执行
//            if(judgeWait(__instance))
//            {
//                //判断能不能执行 这种特殊的伤害禁止执行
//                if(releaseFlag <= 0)
//                {
//                    return SpireReturn.Return();
//                }
//                else {
//                    //放行一个
//                    --releaseFlag;
//                    //记录一下这个已经被允许放行的
//                    heldAction = __instance;
//                }
//            }
//            //如果相等说明这个action正在等待结算
//            return SpireReturn.Continue();
//        }
//    }

    //旋风斩的卡伤害action,和上面的逻辑是一样的，但是会和那边共用同一个action操作
//    @SpirePatch(clz = DamageAllEnemiesAction.class, method = "update")
//    public static class AllDamageActionPause
//    {
//        public static DamageAllEnemiesAction heldAction = null;
//
//        //用于判断一个action是否需要被卡住
//        public static boolean judgeWait(DamageAllEnemiesAction __instance)
//        {
//            //如果它在通行证的列表里面，那是不用等待的
//            //通行证列表里面只会有一个人
//            if(__instance == heldAction)
//                return false;
//            //如果是反伤型的伤害是不用处理的
//            if(__instance.damageType == DamageInfo.DamageType.THORNS)
//                return false;
//            //只有当目标是敌人并且有静电释放的时候才处理
//            return __instance.target instanceof ControlMoster &&
//                    __instance.target.hasPower(StaticDischargePower.POWER_ID);
//        }
//
//        @SpirePrefixPatch
//        public static SpireReturn<Void> fix(DamageAllEnemiesAction __instance)
//        {
//            //判断能否执行
//            if(judgeWait(__instance))
//            {
//                //判断能不能执行 这种特殊的伤害禁止执行
//                if(DamageActionPause.releaseFlag <= 0)
//                {
//                    return SpireReturn.Return();
//                }
//                else {
//                    //放行一个
//                    --DamageActionPause.releaseFlag ;
//                    //记录一下这个已经被允许放行的
//                    heldAction = __instance;
//                }
//            }
//            //如果相等说明这个action正在等待结算
//            return SpireReturn.Continue();
//        }
//    }

    //截流动画更新的一个关键类，进入战斗前会在这里阻塞住进行网络连接
    //2023-8-13 触发时机改成玩家进入的时候
    @SpirePatch(clz = MonsterRoomBoss.class, method = "onPlayerEntry",
            paramtypez = {})
    public static class TestUpdateFading
    {

        public static FakeSettingScreen testScreen=null;
        //动态连接器，用于处理是否已经连接的问题
        //public static DynamicServer server;

        //各种操作是否因为等待被跳过了
        public static boolean applyPreCombatLogicJumpFlag = false;
        public static boolean applyStartOfCombatLogicJumpFlag = false;
        public static boolean applyStartOfCombatPreDrawLogicJumpFlag = false;
        public static boolean applyStartOfTurnRelicsJumpFlag = false;
        public static boolean applyStartOfTurnPostDrawRelicsJumpFlag = false;

        //初始化所有的跳过状态的信息
        public static void initJumpFlag()
        {
            testScreen=null;
            applyPreCombatLogicJumpFlag=false;
            applyStartOfCombatLogicJumpFlag=false;
            applyStartOfCombatPreDrawLogicJumpFlag=false;
            applyStartOfTurnRelicsJumpFlag=false;
            applyStartOfTurnPostDrawRelicsJumpFlag=false;
        }

        public static void dealApplyPreCombatLogic()
        {
            if(applyPreCombatLogicJumpFlag){
                applyPreCombatLogicJumpFlag=false;
                AbstractDungeon.player.applyPreCombatLogic();
            }
        }

        public static void dealApplyStartOfCombatLogic()
        {
            if(applyStartOfCombatLogicJumpFlag)
            {
                applyStartOfCombatLogicJumpFlag=false;
                AbstractDungeon.player.applyStartOfCombatLogic();
            }
        }

        public static void dealApplyStartOfCombatPreDrawLogic()
        {
            if(applyStartOfCombatPreDrawLogicJumpFlag)
            {
                applyStartOfCombatPreDrawLogicJumpFlag=false;
                AbstractDungeon.player.applyStartOfCombatPreDrawLogic();
            }
        }

        public static void dealApplyStartOfTurnRelics()
        {
            if(applyStartOfTurnRelicsJumpFlag)
            {
                applyStartOfTurnRelicsJumpFlag=false;
                AbstractDungeon.player.applyStartOfTurnRelics();
            }
        }

        public static void dealApplyStartOfTurnPostDrawRelics()
        {
            if(applyStartOfTurnPostDrawRelicsJumpFlag)
            {
                applyStartOfTurnPostDrawRelicsJumpFlag=false;
                AbstractDungeon.player.applyStartOfTurnPostDrawRelics();
            }
        }

        //通知退出等待状态
        public static void endWaitStage(boolean forceEnd)
        {
            //如果不是强制test,那就需要把确保screen已经开着
            if(forceEnd || testScreen!=null)
            {
                AbstractDungeon.isScreenUp = false;
                initCombatStage();
                //初始化双方的先手状态，先到房间的为先手
//                if(SocketServer.battleNum==0)
//                {
////                    SocketServer.firstHandFlag =
////                            SocketServer.myEnterTime < SocketServer.oppositeEnterTime;
//                    //初始化战斗轮次状态
//                    initCombatStage();
//                }
                //修改血条 这里面会把时间点改成0.7,这样它就会重新计算血条更新的过程
                AbstractDungeon.player.showHealthBar();
                //恢复允许发送血量的操作
                ActionNetworkPatches.HealEventSend.disableSend = false;
                //打开战斗状态时的触发
                ActionNetworkPatches.disableCombatTrigger = false;
                //添加一次经历过的战斗的次数
                SocketServer.battleNum++;

                testScreen = null;
                //发送自己的遗物列表 关于遗物列表，后面再说
                // RelicPanel.sendMyRelic();
                //如果之前处于等待状态，说明遗物初始化的时候被跳过了，需要重新调用它们一次
                //但有时间可能它并不是先手，跳过就跳过了
                dealApplyPreCombatLogic();
                dealApplyStartOfCombatLogic();
                dealApplyStartOfCombatPreDrawLogic();
                dealApplyStartOfTurnRelics();
                dealApplyStartOfTurnPostDrawRelics();
            }
        }

        //判断现在是不是还要继续阻止渲染
        public static boolean needPauseRender()
        {
            //如果自己已经到了但敌人还没到，那就需要阻止渲染
            return testScreen!=null && !SocketServer.oppositePlayerReady;
        }

        //告诉对面我方已经进入了
        public static void entrySend()
        {
            //在这里发送我方的遗物信息
            Communication.sendEvent(new PlayerRelicEvent());
            //在这里发送我方的药水信息
            Communication.sendEvent(new PlayerPotionEvent());
            //先看看自己是不是房主，如果自己是房主的话就自己给自己分配房间
            GlobalManager.playerManager.assignSeatOfPlayer(
                GlobalManager.playerManager.selfPlayerInfo
            );
            //发送我方角色信息
            Communication.sendEvent(new BattleInfoEvent());
        }

        @SpirePostfixPatch
        public static void fix(MonsterRoomBoss __instance)
        {
            if(!SocketServer.USE_NETWORK)
            {
                return;
            }

            System.out.println("player entry!!!");
            //记录自己是否有符文圆顶，这决定了自己是不是能看见对面的牌
            SocketServer.hasDome = AbstractDungeon.player.hasRelic(RunicDome.ID);
            //初始化战斗状态 后面如果更新了先后手信息会再更新一次
//            if(SocketServer.battleNum>0)
//            {
//                initCombatStage();
//            }
            //告诉对面我方已经进入了
            initJumpFlag();
            //展开等待界面，把界面进入阻塞状态
            testScreen = new FakeSettingScreen();
            AbstractDungeon.isScreenUp = true;
            GlobalManager.playerManager.selfPlayerInfo.initEnterTime();
            entrySend();
            //检查进入战斗的条件
            GlobalManager.playerManager.checkCanEnterBattle();
        }
    }

    //对于使用双端通信的情况，屏蔽开始的时候生效的一些遗物，让它在第2回合再生效
    @SpirePatch(clz = AbstractPlayer.class, method = "applyPreCombatLogic")
    public static class DisablePreBattleRelic
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> fix(AbstractPlayer __instance)
        {
            if(SocketServer.USE_NETWORK)
            {
                //这是一个针对蛇眼的补丁，如果自己有蛇眼的话，就在这个地方手动添加一个
                AbstractPlayer player = AbstractDungeon.player;
                if(player.hasRelic(SneckoEye.ID))
                {
                    //直接给玩家强行添加进去一个混乱，也不需要判断各种逻辑
                    if(!player.hasPower(ConfusionPower.POWER_ID))
                        player.addPower(new ConfusionPower(player));
                }
                //Bug fix for Centennial Puzzle, it should trigger preBattle normally
                if(player.hasRelic(CentennialPuzzle.ID))
                {
                    player.getRelic(CentennialPuzzle.ID).atPreBattle();
                }
                //如果对方还没有准备好，也就是禁止渲染的状态下，是不需要做这个的
                if(CharacterSelectScreenPatches.TestUpdateFading.needPauseRender())
                {
                    TestUpdateFading.applyPreCombatLogicJumpFlag=true;
                    return SpireReturn.Return();
                }
                //一个针对陀螺的补丁，如果有陀螺的话，调用一次它的开场操作
                //这样才能防止后手意外摸牌
                UnceasingTop unceasingTop = (UnceasingTop) player.getRelic(UnceasingTop.ID);
                if(unceasingTop!=null)
                {
                    unceasingTop.atPreBattle();
                }
                //对方回合里面，第一回合不需要执行遗物的初始化操作
                //等第二回合的时候再操作
                if(!AutomaticSocketServer.firstHandFlag)
                {
                    return SpireReturn.Return();
                }
            }
            return SpireReturn.Continue();
        }
    }

    //修改第一回合的逻辑，战斗开始的时候直接结束回合
    @SpirePatch(clz = AbstractPlayer.class, method = "applyStartOfCombatLogic")
    public static class EndTurnOnBegin
    {

        //用来记录当前是第几回合，虽然它并不是一个用来处理回合数的操作
        public static int idTurn = 0;

        @SpirePrefixPatch
        public static SpireReturn<Void> fix(AbstractPlayer __instance)
        {
            //至少当它被调用的时候说明这是一局新的开始
            if(SocketServer.USE_NETWORK)
            {
                //如果服务器还没准备好就不要处理
                if(CharacterSelectScreenPatches.TestUpdateFading.needPauseRender())
                {
                    TestUpdateFading.applyStartOfCombatLogicJumpFlag=true;
                    return SpireReturn.Return();
                }
                //在活动队列里面添加结束回合的操作
                if(!AutomaticSocketServer.firstHandFlag)
                {
                    System.out.println("end turn!!");
                    SendEndTurnMessage.skipNextSend = true;
                    AbstractDungeon.actionManager.addToBottom(
                            new PressEndTurnButtonAction()
                    );
                    //把结束回合的按钮关掉
                    AbstractDungeon.overlayMenu.endTurnButton.disable(true);
                    //给自己添加15格挡
                    if(AbstractDungeon.player.currentBlock < 15)
                        AbstractDungeon.player.addBlock(15);
                    //把自己的费用减掉，防止后手的冰淇淋产生的加费效果
                    AbstractDungeon.actionManager.addToBottom(
                        new LoseEnergyAction(1000)
                    );
                    //记录一下上一回合被跳过了，需要在下一回合正常执行这些操作
                    return SpireReturn.Return();
                }
                else {
                    System.out.println("my first turn!!");
                }
            }
            return SpireReturn.Continue();
        }
    }

    //初始化战斗状态
    public static void initCombatStage()
    {
        //播放一个随机的音乐
        BGMPatch.playRandomBGM();
        //把两个idTurn弄成0
        EndTurnOnBegin.idTurn = 0;
        RelicPreDrawClose.idTurn = 0;
        StopSecondHandDraw.stopNextDraw = true;
        //先手少摸一张
        if(SocketServer.firstHandFlag)
        {
            if(GlobalManager.playerManager.needFirstHandPunishment())
                StopSecondHandDraw.nextDrawDecrease = 1;
            else
                StopSecondHandDraw.nextDrawDecrease = 0;
        }
        else
            StopSecondHandDraw.nextDrawDecrease = 10;
    }


    //用于作弊的东西，获得涅奥的悲恸的时候直接获得100层
    @SpirePatch(clz = NeowsLament.class, method = SpirePatch.CONSTRUCTOR)
    public static class ChangeNeowBlessing
    {
        @SpirePostfixPatch
        public static void fix(NeowsLament __instance)
        {
            //修改它的数值
            __instance.counter = 100;
        }
    }

    //关闭preDraw的效果，为什么都是开始触发，每个遗物却不一样呢
    @SpirePatch(clz = AbstractPlayer.class, method = "applyStartOfCombatPreDrawLogic")
    public static class RelicPreDrawClose
    {
        //是否允许调用
        public static boolean allowCall = false;

        public static int idTurn = 0;

        @SpirePrefixPatch
        public static SpireReturn<Void> fix(AbstractPlayer __instance)
        {
            //还没准备好的情况下直接跳过
            if(TestUpdateFading.needPauseRender())
            {
                TestUpdateFading.applyStartOfCombatPreDrawLogicJumpFlag=true;
                return SpireReturn.Return();
            }
            //允许调用的时候才调用 如果是后手，第一回合就不用处理
            if(allowCall || AutomaticSocketServer.firstHandFlag)
                return SpireReturn.Continue();
            return SpireReturn.Return();
        }

        //强制调用一次
        public static void forceCall()
        {
            allowCall = true;
            AbstractDungeon.player.applyStartOfCombatPreDrawLogic();
            allowCall = false;
        }

    }

    //每回合开始时生效的遗物，如果是后手需要第2回合处理
    @SpirePatch(clz = AbstractPlayer.class, method = "applyStartOfTurnPostDrawRelics")
    public static class RelicPreDrawEachTurn
    {
        //是否强制执行
        public static boolean forceCall = false;

        @SpirePrefixPatch
        public static SpireReturn<Void> fix(AbstractPlayer __instance)
        {
            if(!SocketServer.USE_NETWORK)
            {
                return SpireReturn.Continue();
            }
            //网络没准备好就什么都不要做
            if(TestUpdateFading.needPauseRender())
            {
                TestUpdateFading.applyStartOfTurnPostDrawRelicsJumpFlag = true;
                return SpireReturn.Return();
            }
            //回合计数
            RelicPreDrawClose.idTurn++;
            AutomaticSocketServer tempServer = AutomaticSocketServer.getServer();
            //后手第一回合不执行
            if(!SocketServer.firstHandFlag)
            {
                //第一回合不执行
                if(RelicPreDrawClose.idTurn == 1)
                {
                    return SpireReturn.Return();
                }
                else if(RelicPreDrawClose.idTurn == 2)
                {
                    //额外执行一次初始化的操作
                    RelicPreDrawClose.forceCall();
                }
            }
            //先手一直都是正常执行的
            return SpireReturn.Continue();
        }

    }

    //对于每回合都生效的效果，取消第一回合生效的效果
    @SpirePatch(clz = AbstractPlayer.class, method = "applyStartOfTurnRelics")
    public static class EachPlayerTurnPatch
    {

        //计算坚不可摧层数
        public static int getInvincibleLayer(int maxHealth)
        {
            int invincibleRate = GlobalManager.invincibleRate;
            if(invincibleRate==1)
                return 0;
            if(invincibleRate>=2 && invincibleRate<=4)
            {
                //坚不可摧层数向上取整
                return (int) Math.ceil(((double) maxHealth)/(double) invincibleRate);
            }
            return (invincibleRate-4)*50;
        }

        //初始化自己的固有power
        public static void initSolidPower()
        {
            AbstractPlayer player = AbstractDungeon.player;
            int powerLayer = getInvincibleLayer(player.maxHealth);
            if(powerLayer>0)
            {
                //给自己加上坚不可摧
                AbstractDungeon.actionManager.addToBottom(
                        new ApplyPowerAction(player,player,new InvincibleAtStartPower(player,powerLayer))
                );
            }
        }

        @SpirePrefixPatch
        public static SpireReturn<Void> fix(AbstractPlayer __instance)
        {
            if(SocketServer.USE_NETWORK)
            {
                //还没准备好的情况下直接跳过
                if(TestUpdateFading.needPauseRender())
                {
                    TestUpdateFading.applyStartOfTurnRelicsJumpFlag = true;
                    return SpireReturn.Return();
                }
                //把当前的回合数+1
                EndTurnOnBegin.idTurn++;
                //清空本回合使用过的牌
                UseCardSend.SendUseCardAction.resetCardCost();
                System.out.println(EndTurnOnBegin.idTurn);
                //如果是第一回合并且是客户端，就跳过它
                if(!AutomaticSocketServer.firstHandFlag)
                {
                    //第一回合需要跳过
                    if(EndTurnOnBegin.idTurn == 1)
                    {
                        initSolidPower();
                        return SpireReturn.Return();
                    }
                    else if(EndTurnOnBegin.idTurn == 2) {
                        //第2回合额外触发一次起始逻辑applyStartOfCombatLogic
                        //但不要调用它 而是把它的内部拿出来单独调用
                        Iterator var1 = __instance.relics.iterator();

                        while(var1.hasNext()) {
                            AbstractRelic r = (AbstractRelic)var1.next();
                            if (r != null) {
                                //如果是百年积木不能这样处理
                                if(r.relicId.equals(CentennialPuzzle.ID))
                                {
                                    continue;
                                }
                                //在正常调用的时候，它原本应该是分两回合调用的
                                //这里改成在这里一次性调用
                                r.atBattleStart();
                                r.atPreBattle();
                            }
                        }

                        var1 = __instance.blights.iterator();

                        while(var1.hasNext()) {
                            AbstractBlight b = (AbstractBlight)var1.next();
                            if (b != null) {
                                b.atBattleStart();
                            }
                        }
                        //死灵诅咒
//                        AbstractDungeon.actionManager.addToBottom(
//                            new MakeTempCardInHandAction(new Necronomicurse(),1)
//                        );
                    }
                }
                //先手的第一回合给他减一费
                else if(EndTurnOnBegin.idTurn == 1)
                {
                    //先手给两张静电释放+
//                    PsychicSnooping dischargeCard = new PsychicSnooping();
//                    dischargeCard.upgrade();
//                    AbstractDungeon.actionManager.addToBottom(
//                            new MakeTempCardInHandAction(dischargeCard,1)
//                    );
                    if(GlobalManager.playerManager.needFirstHandPunishment())
                        AbstractDungeon.actionManager.addToBottom(new LoseEnergyAction(1));
                    initSolidPower();
                }
                //第二回合添加一个友军，用于测试
//                if(EndTurnOnBegin.idTurn == 2)
//                {
//                    AbstractMonster tempMonster = new Cultist((float)Settings.WIDTH/2,
//                            (float)Settings.HEIGHT/2);
//                    //添加一个友军
//                    AbstractDungeon.actionManager.addToBottom(
//                        new AddFriendMonsterAction(
//                            tempMonster));
//                }
            }
            //添加一个灭除之刃
//            AbstractCard tempCard = new BurnTransform();
//            //tempCard.damage = 100;
//            //tempCard.baseDamage = 100;
//            tempCard.upgrade();
//            AbstractDungeon.actionManager.addToBottom(
//                new MakeTempCardInHandAction(tempCard,1)
//            );
            return SpireReturn.Continue();
        }
    }

    //为了让连续拳这个牌能触发各种网络传输，这里把它换成另一种牌
    @SpirePatch(clz = Pummel.class, method = "use")
    public static class PummelUseChange
    {
        //直接覆盖这个连续拳的操作
        @SpirePrefixPatch
        public static SpireReturn<Void> fix(Pummel __instance,
            AbstractPlayer p, AbstractMonster m)
        {
            //直接添加对应数量的操作
            for(int idAdd=0;idAdd<__instance.magicNumber;++idAdd)
            {
                AbstractDungeon.actionManager.addToBottom(
                    new DamageAction(m, new DamageInfo(p, __instance.damage,
                    __instance.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY)
                );
            }
            //不再执行原来的版本
            return SpireReturn.Return();
        }

    }

    //回合结束时生效的效果，阻止后手的回合结束遗物第一回合生效
    @SpirePatch(clz = AbstractRoom.class, method = "applyEndOfTurnRelics")
    public static class EndTurnRelicPatch
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> fix(AbstractRoom __instance)
        {
            if(SocketServer.USE_NETWORK) {
                //还没准备好的情况下直接跳过
                if (TestUpdateFading.needPauseRender()) {
                    return SpireReturn.Return();
                }
                //如果是后手并且还是第一回合，也跳过
                if(!AutomaticSocketServer.firstHandFlag &&
                    EndTurnOnBegin.idTurn < 2)
                {
                    return SpireReturn.Return();
                }
            }
            //如果正常运行的话，检查一下毒触发的问题
            // BlockablePoisonPower.checkAddPoisonDamage();
            return SpireReturn.Continue();
        }
    }

    //敌人的行动前准备操作，需要让它检查有没有保留格挡的buff
    @SpirePatch(clz = MonsterGroup.class, method = "applyPreTurnLogic")
    public static class RemoveMonsterBlockPatch
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> fix(MonsterGroup __instance)
        {
            Iterator var1 = __instance.monsters.iterator();

            //遍历群组里面的所有敌人，看是否需要删除格挡
            while(var1.hasNext()) {
                AbstractMonster m = (AbstractMonster)var1.next();
                if (!m.isDying && !m.isEscaping) {
                    if (!m.hasPower(BarricadePower.POWER_ID) &&
                        !m.hasPower(BlurPower.POWER_ID)) {
                        //另外做的一个判断，如果目标是敌方玩家，那么需要另外判断一下有没有外卡钳
                        if(m instanceof ControlMoster && SocketServer.hasCaliper>0)
                        {
                            m.loseBlock(15);
                        }
                        else {
                            m.loseBlock();
                        }
                    }

                    m.applyStartOfTurnPowers();
                }
            }
            //彻底屏蔽后面的函数
            return SpireReturn.Return();
        }
    }


    //结束回合时的操作，告诉对方这边出牌已经结束了
    @SpirePatch(clz = EnemyTurnEffect.class, method = "update")
    public static class SendEndTurnMessage
    {

        //跳过下次end turn的发送
        public static boolean skipNextSend = false;

        @SpirePostfixPatch
        public static void fix(EnemyTurnEffect __instance)
        {
            if(SocketServer.USE_NETWORK && __instance.isDone)
            {
                //标记为阻塞状态
                AbstractDungeon.actionManager.addToBottom(
                    new MultiPauseAction()
                );
                if(skipNextSend)
                {
                    skipNextSend = false;
                    return;
                }
                Communication.sendEvent(new EndTurnEvent());
                //维护回合结束的信息
                GlobalManager.playerManager.battleInfo.updateTurnInfo(
                    GlobalManager.playerManager.selfPlayerInfo
                );
                //发送一个同步血量的操作信息
//                AbstractDungeon.actionManager.addToBottom(
//                    new HealthSyncAction()
//                );
            }
        }
    }

    //用于处理在进入涅奥房间的一瞬间获得各种资源
    @SpirePatch(clz = NeowEvent.class, method = "buttonEffect")
    public static class NeowGetRelic
    {

        //玩家的初始生命上限
        public static final int INIT_HEALTH = 200;

        //是否已经发过礼物，发过的话就不要再发了
        public static boolean hasGiveGift = false;

        //进入函数之前的状态
        public static int rewardStage = 0;

        @SpirePrefixPatch
        public static void fix(NeowEvent __instance,int buttonPressed)
        {
            if(SocketServer.USE_NETWORK)
            {
                try
                {
                    //临时测试清理掉涅奥房间的奖励，防止小屋子的内容再出现
                    AbstractDungeon.getCurrRoom().rewards.clear();
                    //强行修改它的私有成员，禁用掉涅奥的祝福
                    Field tempField = NeowEvent.class.getDeclaredField("screenNum");
                    tempField.setAccessible(true);
                    //记录当前的奖品状态
                    rewardStage = (int) tempField.get(__instance);
                    //99是离开的状态，3是选奖品的状态
                    if(rewardStage!=99 && rewardStage!=3 &&
                        rewardStage!=2 && rewardStage!=10
                    )
                    {
                        //需要设置的新的flag
                        int newFlag = GlobalManager.useModFlag ? 10 : 2;
                        tempField.set(__instance,newFlag);
                        rewardStage = newFlag;
                        //更新随机数种子，防止它生成的每次都一样
                        Settings.seed += 22;
                    }
                    //如果到涅奥奖励了，注意把pick选项关掉
                    if(rewardStage==3)
                    {
                        Field pickCardField = NeowEvent.class.getDeclaredField("pickCard");
                        pickCardField.setAccessible(true);
                        pickCardField.set(__instance,false);
                    }
                    //如果已经经历了一次战斗，那就什么都不需要给了
                    if(hasGiveGift)
                        return;
                    //获得尼利的宝典
                    if(!AbstractDungeon.player.hasRelic(UserNiliCodex.ID))
                    {
                        //没有的话就给他一个
                        (new UserNiliCodex()).instantObtain();
                    }
                    //获得会员卡
                    if(!AbstractDungeon.player.hasRelic(MembershipCard.ID))
                    {
                        //没有的话就给他一个
                        (new MembershipCard()).instantObtain();
                    }
                    //获得送货员
                    if(!AbstractDungeon.player.hasRelic(Courier.ID))
                    {
                        //没有的话就给他一个
                        (new Courier()).instantObtain();
                    }
                    //获得1500金币
                    if(AbstractDungeon.player.gold < GlobalManager.startGold)
                    {
                        AbstractDungeon.player.gainGold(GlobalManager.startGold - AbstractDungeon.player.gold);
                    }
                    //如果尾巴数量是零，那就添加一个无用的尾巴
                    if(GlobalManager.beginTailNum == 0)
                    {
                        PVPTail tempTail = new PVPTail();
                        tempTail.usedUp();
                        tempTail.instantObtain();
                    }
                    //根据设置获得对应数量的尾巴
                    for(int idTail = 0;idTail< GlobalManager.beginTailNum;++idTail)
                    {
                        (new PVPTail()).instantObtain();
                    }
                    //判断是否需要额外获得尾巴
                    if(GlobalManager.playerManager.isSelfLandlord() &&
                        GlobalManager.landlordMoreTail)
                    {
                        (new PVPTail()).instantObtain();
                    }
                    //获得手链
                    (new JuzuBracelet()).instantObtain();
                    //如果是地主并且需要加费的话，就多给一费
                    if(GlobalManager.playerManager.isSelfLandlord() &&
                        GlobalManager.landlordEnergyFlag)
                    {
                        ++AbstractDungeon.player.energy.energyMaster;
                    }
                    //卡牌测试
//                    if(AbstractDungeon.player instanceof Ironclad)
//                    {
//                        AbstractDungeon.actionManager.addToBottom(
//                            new AddCardToDeckAction(new MultiplayerTimeWarp())
//                        );
//                    }
                    //修改生命上限
//                    if(AbstractDungeon.player.maxHealth<INIT_HEALTH)
//                    {
//                        AbstractDungeon.player.increaseMaxHp((INIT_HEALTH - AbstractDungeon.player.maxHealth),false);
//                    }
                    //记录已经送过礼物了
                    hasGiveGift = true;
                }
                catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        //后处理，检查是不是变成了99
        @SpirePostfixPatch
        public static void postfix(NeowEvent __instance,int buttonPressed)
        {
            if(!GlobalManager.useModFlag)
                return;
            //再次检查
            try
            {
                Field tempField = NeowEvent.class.getDeclaredField("screenNum");
                tempField.setAccessible(true);
                //获取现在的reward
                int currStage = (int)tempField.get(__instance);
                //如果之前不是99也不是10,但现在成了99,就把它设置成2
                //这种情况下是之前执行了现开套牌，现在该执行正常的涅奥奖励了
                if(rewardStage!=99 && rewardStage!=2 && currStage==99)
                {
                    //ModHelper.setModsFalse();
                    //取消拿牌的设置
                    tempField.set(__instance,2);
                    GlobalManager.useModFlag = false;
                }
            }
            catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    //进入涅奥房间时的检查，会在这个时候进行网络连接
//    @SpirePatch(clz = NeowEvent.class, method = "buttonEffect")
//    public static class NeowRootConnect
//    {
//        @SpirePrefixPatch
//        public static void fix(NeowEvent __instance)
//        {
//            if(SocketServer.USE_NETWORK)
//            {
//                //初始化连接器
//                AutomaticSocketServer server = AutomaticSocketServer.getServer();
//            }
//        }
//    }

    //更新房间时的操作，直接把开局的房间改成终幕
    @SpirePatch(clz = CardCrawlGame.class, method = "getDungeon",
            paramtypez = {String.class, AbstractPlayer.class})
    public static class EnterEndingAtBegin
    {
        @SpirePrefixPatch
        public static SpireReturn<AbstractDungeon> fix(CardCrawlGame __instance,
           String key, AbstractPlayer p)
        {
            //如果不使用网络就什么都不需要做
            if(!SocketServer.USE_NETWORK)
            {
                return SpireReturn.Continue();
            }
            System.out.println("fake ending trigger!!!\n\n");
            //如果是底层，把它改成终局
            if(key.equals(Exordium.ID))
            {
                ArrayList<String> emptyList = new ArrayList();
                return SpireReturn.Return(new FakeEnding(p,emptyList));
            }
            //正常情况下直接返回
            return SpireReturn.Continue();
        }
    }

    //另一个版本的生成房间的操作
    @SpirePatch(clz = CardCrawlGame.class, method = "getDungeon",
            paramtypez = {String.class, AbstractPlayer.class, SaveFile.class})
    public static class EnterEndingAtBeginForSave
    {
        @SpirePrefixPatch
        public static SpireReturn<AbstractDungeon> fix(CardCrawlGame __instance,
                       String key, AbstractPlayer p,SaveFile saveFile)
        {
            if(!SocketServer.USE_NETWORK)
            {
                return SpireReturn.Continue();
            }
            //如果是底层，把它改成终局
            if(key.equals(Exordium.ID))
            {
                return SpireReturn.Return(new FakeEnding(p,saveFile));
            }
            //正常情况下直接返回
            return SpireReturn.Continue();
        }
    }

    //用于检查是否进入下一个房间的操作，没有什么实际的用
    @SpirePatch(clz = AbstractDungeon.class, method = "nextRoomTransitionStart")
    public static class CheckEnterRoom
    {
        @SpirePrefixPatch
        public static void fix()
        {
            System.out.println("call nextRoomTransitionStart");
        }
    }

    //截流设置界面的open,连接的时候不准打开设置界面
    @SpirePatch(clz = SettingsScreen.class, method = "open",
            paramtypez = {boolean.class})
    public static class StopSettingOpen
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> fix(SettingsScreen __instance,boolean animated)
        {
            //不用网络的时候不需要考虑这个问题
            if(!SocketServer.USE_NETWORK)
            {
                return SpireReturn.Continue();
            }
            //打开退出界面的渲染
            MidExitScreen.onOpen();
            //判断是不是要停止渲染，这种情况下禁止打开设置界面
//            if(TestUpdateFading.needPauseRender())
//            {
//                return SpireReturn.Continue();
//            }
            //如果还是等待连接的状态，就禁止打开设置界面
            return SpireReturn.Return();
        }
    }

//
    //对内容的渲染函数，这个是用来渲染自己的干扰页面的
    @SpirePatch(clz = AbstractDungeon.class, method = "render")
    public static class TestRenderForRename
    {

        @SpirePostfixPatch
        public static void fix(AbstractDungeon __instance,SpriteBatch sb)
        {
            //判断一下是否需要渲染名字框
            //如果读取已经结束了那也不用渲染了
            if(TestUpdateFading.testScreen != null &&
                !SocketServer.oppositePlayerReady)
            {
                TestUpdateFading.testScreen.render(sb);
            }
            //对退出界面的渲染，在关键的时候可以选择中途退出
            MidExitScreen.onRender(sb);
        }
    }

    //用来检查地图点击时是否达成了调用进入房间的条件
    @SpirePatch(clz = DungeonMap.class, method = "update")
    public static class CheckEnterRoomMapUpdate
    {

        static public int saveNodeY = -1;

        @SpirePrefixPatch
        public static void fix(DungeonMap __instance)
        {
            //如果是战斗状态就不用处理了
            if(AbstractDungeon.getCurrRoom() instanceof MonsterRoom)
            {
                return;
            }
            //临时把id改成在结尾
            if(SocketServer.USE_NETWORK)
            {
                //把id改成ending
                AbstractDungeon.id = "TheEnding";
            }
            saveNodeY = AbstractDungeon.getCurrMapNode().y;
            //debug的时候打开这里，这样可以直接进入boss房间
            // AbstractDungeon.getCurrMapNode().y = 2;
            //如果当前到了3层说明可以进boss房间了
            if(saveNodeY>=FakeEnding.ROW_NUM-1)
            {
                AbstractDungeon.getCurrMapNode().y = 2;
            }
            else if(saveNodeY>1) {
                AbstractDungeon.getCurrMapNode().y = 1;
            }
        }

        //结束操作
        @SpirePostfixPatch
        public static void postFix(DungeonMap __instance)
        {
            //如果是战斗状态就不用处理了
            if(AbstractDungeon.getCurrRoom() instanceof MonsterRoom)
            {
                return;
            }
            if(SocketServer.USE_NETWORK)
            {
                //把id改成ending
                AbstractDungeon.id = Exordium.ID;
            }
            //恢复改过的数值
            AbstractDungeon.getCurrMapNode().y = saveNodeY;
        }
    }

    //处理地图里面boss显示的位置，因为这里面使用的只是终幕，所以目前只能这样
    @SpirePatch(clz = DungeonMap.class, method = "calculateMapSize")
    public static class ChangeBossShowLocation
    {
        @SpirePrefixPatch
        public static SpireReturn<Float> fix(DungeonMap __instance)
        {
            //如果是使用网络的情况，那就按照终局来显示心脏
            //这里和终局也不一样，是一个特殊的数字，那边有几层就是几个
            if(SocketServer.USE_NETWORK)
            {
                float heartLocation = Settings.MAP_DST_Y * (FakeEnding.ROW_NUM+1) - 1380.0F * Settings.scale;
                return SpireReturn.Return(heartLocation);
            }
            //正常情况下使用原始函数返回
            return SpireReturn.Continue();
        }
    }

    //令初始的弱怪物里面只生成kaka
    @SpirePatch(clz = Exordium.class, method = "generateWeakEnemies")
    public static class OnlyKakaEnemy
    {

        @SpireInstrumentPatch
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                //记录这是第几次调用目标函数
                private int callCount = 0;
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    //如果不用连接，也就不用添加kaka了
                    if(!SocketServer.USE_NETWORK)
                    {
                        return;
                    }
                    if (m.getClassName().equals("java.util.ArrayList") &&
                        m.getMethodName().equals("add")) {
                        //如果是第一次调用是不需要换的
                        if(callCount == 2)
                            m.replace("{$_ = true;}");
                        else if(callCount==0)//替换掉kaka
                        {
                            m.replace("{ $_ = $proceed(new com.megacrit.cardcrawl.monsters.MonsterInfo(\"ControlMoster\", 100.0F)); }");
                        }
                        //正常情况沔都要把计数增加一次
                        callCount++;
                    }
                }
            };
        }
    }

    //卡面伤害数值的计算补丁，计算完之后判断一下对面有没有暴怒，有暴怒的时候伤害*2
//    @SpirePatch(clz = AbstractCard.class, method = "calculateCardDamage")
//    public static class CardDamageCalculateForStance
//    {
//        @SpirePostfixPatch
//        public static void fix(AbstractCard __instance, AbstractMonster mo)
//        {
//            //需要保证类型是可控制的敌人，不是的话就不用管
//            if(mo instanceof ControlMoster)
//            {
//                ControlMoster tempMonster = (ControlMoster) mo;
//                //先记录临时的数值
//                int tempDamage = __instance.damage;
//                //判断有没有暴怒姿态
//                __instance.damage = (int)tempMonster.stance.atDamageReceive(
//                        __instance.damage, DamageInfo.DamageType.NORMAL
//                );
//                //如果伤害变化了，需要标记一下伤害变化了
//                if(tempDamage != __instance.damage)
//                {
//                    __instance.isDamageModified = true;
//                }
//            }
//        }
//    }

    //禁用触发无实体，主要是为了解决发条靴的bug,当触发发条靴的时候禁用无实体
    @SpirePatch(clz = AbstractCreature.class,method = "hasPower")
    public static class StopIntangible
    {

        //是否禁用无实体
        public static boolean stopFlag = false;

        //前置的处理，如果寻找的是无实体，在相应的情况下不直接不找了
        @SpirePrefixPatch
        public static SpireReturn<Boolean> fix(AbstractCreature __instance,
               String targetID)
        {
            //如果要找的是无实体，那就直接不给找
            if(stopFlag && targetID.equals("IntangiblePlayer"))
            {
                return SpireReturn.Return(false);
            }
            return SpireReturn.Continue();
        }

    }

    //对发条靴的处理，当伤害来源有发条靴的时候，对伤害做相应的增加处理
    @SpirePatch(clz = AbstractPlayer.class,method = "damage")
    public static class DamageChangeForBoot
    {

        //后手操作，damage处理完之后，解除对无实体效果的禁用
        @SpirePostfixPatch
        public static void postfix(AbstractPlayer __instance,
           DamageInfo info)
        {
            StopIntangible.stopFlag = false;
            //受伤后后处理的事情，如果玩家受伤后死了，也禁止触发战斗相关的事件
            if(__instance.isDead)
            {
                ActionNetworkPatches.disableCombatTrigger = true;
            }
        }

        //前缀处理，如果可以使用发条靴的话，就触发发条靴的逻辑
        @SpirePrefixPatch
        public static void fix(AbstractPlayer __instance,
           DamageInfo info)
        {
            //需要确保是存在发条靴的 另外只有普通的打击才生效
            if(SocketServer.bootNum>0 &&
                info.owner instanceof AbstractMonster &&
                info.type == DamageInfo.DamageType.NORMAL)
            {
                //判断目标是不是有无实体
                if(AbstractDungeon.player.hasPower("IntangiblePlayer") &&
                    AbstractDungeon.player.currentBlock ==0)
                {
                    //对发条靴加无实体情况的单独处理
                    if(info.output>0)
                    {
                        info.output=5;
                        //禁用无实体的触发
                        StopIntangible.stopFlag = true;
                    }
                }
                else {
                    //计算实际的伤害
                    int realDamage = info.output - __instance.currentBlock;
                    //判断是否满足发条靴的进入条件
                    if(realDamage>0 && realDamage<5)
                    {
                        info.output += (5-realDamage);
                    }
                }
            }
        }
    }

    //打出球伤害的补丁，判断对方有没有愤怒姿态，有的话把伤害*2
    @SpirePatch(clz = AbstractOrb.class, method = "applyLockOn")
    public static class OrbDamageDoubleForStance
    {
        @SpirePrefixPatch
        public static SpireReturn<Integer> fix(AbstractCreature target, int dmg)
        {
            int retVal = dmg;
            if (target.hasPower("Lockon")) {
                retVal = (int)((float)dmg * 1.5F);
            }
            //判断对方有没有愤怒，如果有愤怒再把伤害*2
            if(target instanceof ControlMoster)
            {
                ControlMoster tempMonster = (ControlMoster)target;
                retVal = (int)tempMonster.stance.atDamageReceive(retVal,DamageInfo.DamageType.NORMAL);
            }

            return SpireReturn.Return(retVal);
        }
    }

    //打出伤害信息时的处理补丁，应用power之后再检查一下对方是不是有暴怒
//    @SpirePatch(clz = DamageInfo.class, method = "applyPowers")
//    public static class DamageInfoCalculateForStance
//    {
//        @SpirePostfixPatch
//        public static void fix(DamageInfo __instance,
//           AbstractCreature owner, AbstractCreature target)
//        {
//            //如果目标是可控制的敌人，就应用一下姿态
//            if(target instanceof ControlMoster)
//            {
//                ControlMoster tempMonster = (ControlMoster) target;
//                __instance.output = (int)tempMonster.stance.atDamageReceive(
//                        __instance.output, DamageInfo.DamageType.NORMAL
//                );
//            }
//        }
//    }

    //修改获取怪物队列的函数
    //如果是心脏的话，也把它换成kaka
    @SpirePatch(clz = MonsterHelper.class, method = "getEncounter")
    public static class ControlMonsterGetEncounter
    {
        @SpirePrefixPatch
        public static SpireReturn<MonsterGroup> controlEncounter(String key)
        {
            if(SocketServer.USE_NETWORK)
            {
                //判断key是不是自己定义的类型
                if(key.equals("ControlMoster") || key.equals("The Heart"))
                //if(key.equals("ControlMoster"))
                {
                    //直接返回自己定义的怪物类型
                    return SpireReturn.Return(GlobalManager.playerManager.getMonsterGroup());
                }
                else if(key.equals("Cultist"))
                {
                    System.out.print("find KaKa !!!!!\n\n\n\n\n\n");
                }
            }
            return SpireReturn.Continue();
        }
    }

    //查看boss房到底有没有人进去
    //还是没有响应，一层一层向上找，看到底是哪一些没响应
//    @SpirePatch(clz = MonsterRoomBoss.class, method = "onPlayerEntry")
//    public static class CheckBossRoom
//    {
//        @SpirePrefixPatch
//        public static void fix()
//        {
//            System.out.println("player enter boss!!!");
//        }
//    }

    //截流计算分数的页面，直接返回一个很高的分数
    @SpirePatch(clz = GameOverScreen.class, method = "calcScore")
    public static class GetHighScore
    {
        @SpirePrefixPatch
        public static SpireReturn<Integer> controlEncounter(boolean victory)
        {
            return SpireReturn.Return(5000);
        }
    }

    //添加敌人数据的补充函数
    @SpirePatch(clz = MonsterHelper.class, method = "uploadEnemyData")
    public static class UpdateEnemyDataPatch
    {
        //对函数的插入操作 需要把自定义的东西添加到data里面
        @SpireInsertPatch(rloc = 5,localvars = {"data"})
        public static void Insert(@ByRef ArrayList<EnemyData>[] data)
        {
            //给data添加那一个特殊的可以控制的敌人
            data[0].add(new EnemyData(ControlMoster.ID,1, EnemyData.MonsterType.WEAK));
        }
    }

    //可以多次触发的尾巴，每触发一次尾巴，就把这个尾巴弄成别的名字，这样就找不到这个尾巴了
    @SpirePatch(clz = LizardTail.class, method = "onTrigger")
    public static class ChangeTailName
    {
        //触发之前禁止发送回血信息
        @SpirePrefixPatch
        public static SpireReturn<Void> fixPre(LizardTail __instance)
        {
            //禁止发送回血信息
//            ActionNetworkPatches.HealEventSend.disableSend = true;
//            //把最大生命值提高一倍 这个时候自动会有回血的效果
//            AbstractDungeon.player.increaseMaxHp(
//                AbstractDungeon.player.maxHealth,true);
//            //把这个遗物弄成灰色
//            __instance.flash();
//            __instance.setCounter(-2);
//            try
//            {
//                //触发过之后就给它改个名字，防止它重复触发
//                Field tempField = AbstractRelic.class.getDeclaredField("relicId");
//                tempField.setAccessible(true);
//                //强行修改字段的值
//                tempField.set((AbstractRelic)__instance,LizardTail.ID + "_used");
//                //获得一个格挡加倍的遗物
//                if(BlockGainer.blockGainRate > 0.01f)
//                    (new BlockGainer()).instantObtain();
//                //只有当处于战斗房间时才会做这些事情，平常不用做
//                //有时候可能死在事件房间里
//                if(AbstractDungeon.getCurrRoom() instanceof MonsterRoom)
//                {
//                    //执行丢钱的动画
//                    ControlMoster monster = ControlMoster.instance;
//                    AbstractPlayer player = AbstractDungeon.player;
//                    //将要丢掉的钱
//                    int loseGold = (int)(player.gold * SocketServer.loseGoldRate);
//                    if(loseGold>0)
//                    {
//                        player.loseGold(loseGold);
//                    }
//                    //根据丢钱的数量执行动画
//                    for(int idLose=0;idLose<loseGold;++idLose)
//                    {
//                        AbstractDungeon.effectList.add(new GainPennyEffect(ControlMoster.instance,
//                                player.hb.cX, player.hb.cY, monster.hb.cX, monster.hb.cY, false));
//                    }
//                    //调用尾巴逻辑之后执行逃跑操作
//                    endCombatAsSmoke();
//                }
//            }
//            catch (NoSuchFieldException | IllegalAccessException e)
//            {
//                e.printStackTrace();
//            }
//            //恢复发送回血信息
//            ActionNetworkPatches.HealEventSend.disableSend = false;
            //完全禁止使用原本的触发模式
            return SpireReturn.Return();
        }

        //调用类似于烟雾弹的操作，直接结束玩家的游戏
        public static void endCombatAsSmoke()
        {
            //记录下次为先手
            SocketServer.firstHandFlag = true;
            SocketServer.oppositePlayerReady = false;
            //禁用战斗状态下的动作
            ActionNetworkPatches.disableCombatTrigger = true;
            ActionNetworkPatches.HealEventSend.disableSend = true;
            //下面是逃跑对应的逻辑
            // AbstractDungeon.getCurrRoom().smoked = true;
            //指定失败的一方跳过下次的奖励
            RewardPatch.loserJumpRewardFlag = true;
            AbstractDungeon.actionManager.addToBottom(new VFXAction(new SmokeBombEffect(
                AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY)));
            AbstractDungeon.player.hideHealthBar();
            AbstractDungeon.player.isEscaping = true;
            AbstractDungeon.player.flipHorizontal = !AbstractDungeon.player.flipHorizontal;
            AbstractDungeon.overlayMenu.endTurnButton.disable();
            AbstractDungeon.player.escapeTimer = 2.5F;
        }
    }


    //禁止一些可以获取的遗物，目前还无法处理的
//    @SpirePatch(clz = AbstractDungeon.class, method = "returnRandomRelicEnd",
//        paramtypez = {AbstractRelic.RelicTier.class})
//    public static class BanSomeRelic
//    {
//
//        //是否为来自自身的调用
//        public static boolean callFromSelf = false;
//
//        @SpirePrefixPatch
//        public static SpireReturn<AbstractRelic> fix(AbstractRelic.RelicTier tier)
//        {
//            //只有是来自自身的调用才会正常返回
//            if(callFromSelf)
//            {
//                return SpireReturn.Continue();
//            }
//            else {
//                callFromSelf = true;
//                AbstractRelic tempRelic;
//                while (true)
//                {
//                    boolean isOk = true;
//                    tempRelic = AbstractDungeon.returnRandomRelicEnd(tier);
//                    switch (tempRelic.relicId)
//                    {
//                        case Omamori.ID://御守
//                        case GremlinHorn.ID://地精之角
//                        case PreservedInsect.ID://昆虫标本
//                        case BottledFlame.ID:
//                        case BottledLightning.ID:
//                        case BottledTornado.ID:
//                        case PaperCrane.ID://猎人纸鹤
//                        case SingingBowl.ID://唱歌碗
//                        case WingBoots.ID://飞鞋
//                        case Torii.ID://鸟居
//                        case TungstenRod.ID://钨合金锟
//                        case Turnip.ID://洋葱
//                        case Sling.ID://精英敌人获得两点力量
//                        case PrismaticShard.ID://彩虹棱镜
//                        case MembershipCard.ID:
//                        case Anchor.ID://锚
//                            isOk = false;
//                    }
//                    if(isOk)
//                        break;
//                }
//                callFromSelf = false;
//                return SpireReturn.Return(tempRelic);
//            }
//        }
//    }

    //截流获取随机药水的东西，禁止出现瓶中精灵
    @SpirePatch(clz = AbstractDungeon.class, method = "returnRandomPotion",
        paramtypez = {})
    public static class BanSomePotion
    {

        //是否来自自身调用
        public static boolean callFromSelf = false;

        @SpirePrefixPatch
        public static SpireReturn<AbstractPotion> fix()
        {
            //这里需要把某些药水挡住，禁止某些药水的出现
            if(callFromSelf)
            {
                return SpireReturn.Continue();
            }
            else {
                //改成来自自身调用
                callFromSelf = true;
                AbstractPotion tempPotion;
                //循环调用这个数据
                while(true)
                {
                    tempPotion = AbstractDungeon.returnRandomPotion();
                    //目前药水里面烟雾弹是不行的
                    if(!tempPotion.ID.equals(SmokeBomb.POTION_ID) &&
                     !tempPotion.ID.equals(FairyPotion.POTION_ID))
                    {
                        break;
                    }
                }
                //恢复自身调用的标志
                callFromSelf = false;
                return SpireReturn.Return(tempPotion);
            }
        }
    }

    //触发买牌的时候强制处理成完全随机的刷新牌
    @SpirePatch(clz = ShopScreen.class, method = "purchaseCard")
    public static class PurchaseCardForceRandom
    {
        //把获得牌的逻辑变成完全随机
        @SpirePrefixPatch
        public static void fixPre(ShopScreen __instance,
              AbstractCard hoveredCard)
        {
            GetRanddomCardWithNoType.totalRandom = true;
        }

        //恢复变成完成随机的操作
        @SpirePostfixPatch
        public static void fixPost(ShopScreen __instance,
                                  AbstractCard hoveredCard)
        {
            GetRanddomCardWithNoType.totalRandom = false;
        }
    }

    //获得随机卡牌的处理，商店里面获得随机卡牌的时候，不再按照卡牌类型来送货
    @SpirePatch(clz = CardGroup.class, method = "getRandomCard",
        paramtypez = {AbstractCard.CardType.class, boolean.class})
    public static class GetRanddomCardWithNoType
    {
        public static boolean totalRandom = false;

        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> fix(CardGroup __instance,
            AbstractCard.CardType type, boolean useRng)
        {
            //如果是强制全随机的情况
            if(totalRandom)
            {
                //最后获得的卡牌
                AbstractCard ansCard;
                if (useRng) {
                    ansCard = (AbstractCard)__instance.group.get(AbstractDungeon.cardRng.random(__instance.group.size() - 1));
                } else {
                    ansCard = (AbstractCard)__instance.group.get(MathUtils.random(__instance.group.size() - 1));
                }
                return SpireReturn.Return(ansCard);
            }
            return SpireReturn.Continue();
        }
    }

    //处理后手摸牌的问题，禁用后手的第一次摸牌
    @SpirePatch(clz = DrawCardAction.class,method = "update")
    public static class StopSecondHandDraw
    {
        //是否需要禁用下一次摸牌
        public static boolean stopNextDraw = false;

        public static int nextDrawDecrease = 5;

        //如果需要禁用下一次摸牌的话，就把摸牌取消
        @SpirePostfixPatch
        public static void fix(DrawCardAction __instance)
        {
            //如果需要取消摸牌，就取消它
            if(stopNextDraw)
            {
                stopNextDraw = false;
                __instance.amount -= nextDrawDecrease;
                if(__instance.amount<0)
                    __instance.amount=0;
            }
        }
    }

    //获取怪物组名称的补丁，似乎是为了处理死亡地之类的问题
    public static class GetEncounterNamePatch
    {
        @SpirePrefixPatch
        public static SpireReturn<String> controlEncounterName(String key)
        {
            //判断key是不是自己定义的类型
            if(key.equals(ControlMoster.ID))
            {
                //返回这个怪物类型的名字
                return SpireReturn.Return(ControlMoster.NAME);
            }
            return SpireReturn.Continue();
        }
    }
}