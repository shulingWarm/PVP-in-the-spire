package WarlordEmblem.actions;

import UI.PotionPanel;
import UI.RelicPanel;
import WarlordEmblem.GlobalManager;
import WarlordEmblem.PVPApi.BaseEvent;
import WarlordEmblem.Screens.midExit.MidExitScreen;
import WarlordEmblem.SocketServer;
import WarlordEmblem.character.ControlMoster;
import WarlordEmblem.orbs.OrbPatch;
import WarlordEmblem.patches.ActionNetworkPatches;
import WarlordEmblem.patches.CardShowPatch.DrawPileSender;
import WarlordEmblem.patches.CardShowPatch.HandCardSend;
import WarlordEmblem.patches.CardShowPatch.UseCardSend;
import WarlordEmblem.patches.CharacterSelectScreenPatches;
import WarlordEmblem.patches.RenderPatch;
import WarlordEmblem.powers.FakeTimePower;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

//两个玩家对战时的通信协议
public class FightProtocol extends AbstractActionProtocol {

    //对用户自定义事件的解码
    public static void handleCustomEvent(DataInputStream stream)
    {
        //所有事件的列表
        ArrayList<BaseEvent> eventList = GlobalManager.eventList;
        //读取事件id
        try
        {
            int eventId = stream.readInt();
            //判断是否存在这个event
            if(eventId < eventList.size())
            {
                BaseEvent event = eventList.get(eventId);
                System.out.printf("Trigger custom event: %s\n",event.eventId);
                event.decode(stream);
            }
            else {
                System.out.printf("Invalid event index %d\n",eventId);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static final int INVALID_TAG = 0;
    //各种数据
    public static final int END_TURN = 10028;
    //攻击行为的字段头
    public static final int DAMAGE = 10001;
    //获得格挡的数据类型
    public static final int GAIN_BLOCK = 10002;
    //添加power时的操作
    public static final int APPLY_POWER = 10003;
    //失去生命的操作
    public static final int LOSE_HP = 10004;
    //造球信息的标头
    public static final int CHANNEL_ORB = 10005;
    //发送特效时的信息
    public static final int EFFECT_INFO = 10006;
    //充能球激发时的操作
    public static final int EVOKE_INFO = 10007;
    //添加扩容信息的操作
    public static final int INCREASE_SLOT = 10008;
    //触发循环buff的信息
    public static final int LOOP_BUFF_INFO = 10009;
    //减少球位的操作
    public static final int DECREASE_SLOT = 10010;
    //移除所有的格挡
    public static final int REMOVE_ALL_BLOCK = 10011;
    //改变姿态的操作
    public static final int CHANGE_STANCE = 10012;
    //发送自己的健康状态
    public static final int SEND_HEALTH = 10013;
    //跳过敌人回合
    public static final int JUMP_ENEMY_TURN = 10014;
    //治疗信息
    public static final int HEAL_INFO = 10015;
    //对怪物的斩杀信息
    public static final int SUICIDE_INFO = 10016;
    //玩家死亡后重生的信息，把这个信息通知给对方
    public static final int REBIRTH_INFO = 10017;
    //通知对面执行斩杀操作
    public static final int MAKE_IT_DIE = 10018;
    //审判的数据头
    public static final int JUDGEMENT = 10019;
    //新版本的伤害编码
    public static final int DAMAGE_CODE_0812 = 10020;
    //移除所有debuff的编码
    public static final int REMOVE_DEBUFF = 10021;
    //黑球回合结束时的正常操作
    public static final int DARK_END_TURN = 10022;
    //球更新描述的数据头
    public static final int ORB_UPDATE_DES = 10023;
    //使用牌的消息
    public static final int USE_CARD = 10024;
    //卡牌信息的数据头
    public static final int CARD_INFO_HEAD = 10025;
    //更新手里的卡牌内容的数据头
    public static final int UPDATE_HAND_CARD = 10026;
    //对斩杀信息的编码
    public static final int INSTANT_KILL = 10027;
    //steam里面基本的打招呼信息 这个消息不需要被响应，接收到之后直接忽略就可以了
    //仅仅是用于steam通信时的双端同步
    public static final int STEAM_HELLO = 10029;
    //强制发送power的标头 当power映射表里面没有这个power的时候会走这个分支
    public static final int FORCE_SEND_POWER = 10030;
    //移除power的消息
    public static final int REMOVE_POWER = 10031;
    //减少buff的层数
    public static final int REDUCE_POWER = 10032;
    //对方结束游戏的信号
    public static final int EXIT_GAME_INFO = 10033;
    //移除球相关的操作
    public static final int REMOVE_ORB_INFO = 10034;
    //时间吞噬的信息更新
    public static final int TIME_EAT_UPDATE = 10035;
    //给对方施加卡牌
    public static final int TRANSFORM_CARD = 10036;
    //延迟测试的数据头
    public static final int DELAY_TEST = 10037;
    //战斗过程中收到的状态信息
    public static final int BATTLE_HEALTH = 10038;
    //遗物列表
    public static final int RELIC_LIST = 10039;
    //传输药水的列表
    public static final int POTION_LIST = 10040;
    //能量更新
    public static final int ENERGY_UPDATE = 10041;
    //即将抽到的牌的更新
    public static final int DRAWING_CARD_UPDATE = 10042;
    //强制消耗对方某张牌的命令
    public static final int FORCE_EXHAUST_CARD = 10043;
    //用户自定义事件，以后大部分事件都会在这里面实现
    public static final int CUSTOM_EVENT = 10044;
    //用于区分不同类型的目标
    public static final int MONSTER = 1;
    public static final int PLAYER = 0;
    //不同球的种类的标号
    public static final int ORB_LIGHTING = 0;
    public static final int ORB_BLOCK = 1;//冰球
    //黑球
    public static final int ORB_DARK = 2;
    //离子球
    public static final int ORB_PLASMA = 3;

    //如果读取到结束标志符就会把它改成true
    public static boolean endReadFlag = true;

    @Override
    public boolean isNeedData()
    {
        return !endReadFlag;
    }

    //从输入流里面读取数据
    //这个函数永远都会返回false
    public boolean readData(SocketServer server)
    {
        try
        {
            //判断是否有数据可以被读取
            if(!server.isDataAvailable())
            {
                return false;
            }
            //临时读取一个数据
            int tempData = server.inputHandle.readInt();
//            if(tempData != DELAY_TEST)
//                System.out.printf("receive %d\n",tempData);
            switch (tempData)
            {
                case INVALID_TAG:
                    break;
                //收到延迟处理的消息的tag
                case DELAY_TEST:
                    RenderPatch.receiveDelayInfo(server.inputHandle);
                    break;
                case END_TURN:
                    endReadFlag = true;
                    //清空输入流
//                    while(server.inputHandle.available()>0)
//                    {
//                        server.inputHandle.readInt();
//                    }
                    break;
                case DAMAGE:
                    //从数据里面解码出攻击操作
                    ActionNetworkPatches.damageDecode(server.inputHandle);
                    break;
                case GAIN_BLOCK:
                    ActionNetworkPatches.gainBlockDecode(server.inputHandle);
                    break;
                //对方打出牌的信息，显示对方打出的牌
                case USE_CARD:
                    UseCardSend.useCardDecode(server.inputHandle);
                    break;
                //更新对手的手牌内容变化
                case UPDATE_HAND_CARD:
                    HandCardSend.handCardDecode(server.inputHandle);
                    break;
                //更新抽牌堆的显示牌
                case DRAWING_CARD_UPDATE:
                    DrawPileSender.drawCardDecode(server.inputHandle);
                    break;
                //能量框显示的更新
                case ENERGY_UPDATE:
                    ControlMoster.energyChangeDecode(server.inputHandle);
                    break;
                case APPLY_POWER:
                    ActionNetworkPatches.applyPowerDecode(server.inputHandle);
                    break;
                //移除power的操作
                case REMOVE_POWER:
                    ActionNetworkPatches.removePowerDecode(server.inputHandle);
                    break;
                //用户自定义的信息
                case CUSTOM_EVENT:
                    handleCustomEvent(server.inputHandle);
                    break;
                //强制发送power的信息，没有被编码的power通过这个方式发送
                case FORCE_SEND_POWER:
                    ActionNetworkPatches.forceDecodePower(server.inputHandle);
                    break;
                //新版本的伤害信息
                case DAMAGE_CODE_0812:
                    ActionNetworkPatches.onAttackReceive(server.inputHandle);
                    break;
                case LOSE_HP:
                    ActionNetworkPatches.loseHpDecode(server.inputHandle);
                    break;
                case CHANNEL_ORB:
                    //调用解码操作，但不一样的是，这里它会自己把action添加进去
                    ActionNetworkPatches.channelOrbDecode(server.inputHandle);
                    break;
                case EVOKE_INFO:
                    ControlMoster.instance.evokeOrb();
                    break;
                //当接收到牌信息的时候，把牌的信息记录到哈希表里面
                case CARD_INFO_HEAD:
                    UseCardSend.receiveCardInfo(server.inputHandle);
                    break;
                case INCREASE_SLOT:
                    ActionNetworkPatches.increaseSlotDecode(server.inputHandle);
                    break;
                //更新黑球信息的操作
                case ORB_UPDATE_DES:
                    OrbPatch.orbDescriptionDecode(server.inputHandle);
                    break;
                //循环buff触发的时候，修改头部的黑球信息
                case LOOP_BUFF_INFO:
                    ActionNetworkPatches.loopDecode(server.inputHandle);
                    break;
                //老头的表的更新
                case TIME_EAT_UPDATE:
                    FakeTimePower.receiveUpdate(server.inputHandle);
                    break;
                //减少球位的操作，直接通知敌人减少对应的球位
                case DECREASE_SLOT:
                    ControlMoster.instance.decreaseMaxOrbSlots(1);
                    break;
                //移除所有格挡的操作
                case REMOVE_ALL_BLOCK:
                    ActionNetworkPatches.removeAllBlockDecode(server.inputHandle);
                    break;
                //改变姿态
                case CHANGE_STANCE:
                    ActionNetworkPatches.changeStanceDecode(server.inputHandle);
                    break;
                //发送战斗内的健康信息
                case BATTLE_HEALTH:
                    HealthSyncAction.battleHealthDecode(server.inputHandle);
                    break;
                case SEND_HEALTH:
                    ActionNetworkPatches.receiveHealth(server.inputHandle);
                    break;
                case JUMP_ENEMY_TURN:
                    ActionNetworkPatches.jumpEnemyTurnDecode();
                    break;
                //回血
                case HEAL_INFO:
                    ActionNetworkPatches.healDecode(server.inputHandle);
                    break;
                //斩杀信息
                case SUICIDE_INFO:
                    ActionNetworkPatches.suicideDecode(server.inputHandle);
                    break;
                //斩杀后回血的信息
                case REBIRTH_INFO:
                    ActionNetworkPatches.rebirthDecode(server.inputHandle);
                    break;
                //对敌人执行斩杀的操作
                case MAKE_IT_DIE:
                    ControlMoster.instance.makeItDie();
                    break;
                //对buff调用相应的减少层数
                case REDUCE_POWER:
                    ActionNetworkPatches.reducePowerDecode(server.inputHandle);
                    break;
                //审判信息
                case JUDGEMENT:
                    ActionNetworkPatches.judgementDecode(server.inputHandle);
                    break;
                //移除所有debuff的操作
                case REMOVE_DEBUFF:
                    ControlMoster.instance.removeAllDebuff();
                    break;
                //黑球回合结束时的解码操作
                case DARK_END_TURN:
                    OrbPatch.darkEndTurnDecode(server.inputHandle);
                    break;
                //收到斩杀信息的时候对斩杀信息做解码操作
                case INSTANT_KILL:
                    ActionNetworkPatches.instantKillDecode(server.inputHandle);
                    break;
                //退出游戏的消息，对方强行退出的时候，强制这边也退出
                case EXIT_GAME_INFO:
                    MidExitScreen.receiveExitInfo();
                    break;
                //移除球位的消息
                case REMOVE_ORB_INFO:
                    if(ControlMoster.instance!=null)
                    {
                        //在敌人显示这边，激发操作就是移除操作
                        ControlMoster.instance.evokeOrb();
                    }
                    break;
                //对塞牌信息的解码
                case TRANSFORM_CARD:
                    TransformCardAction.addCardDecode(server.inputHandle);
                    break;
                //战斗开始时接收对方的遗物列表
                case RELIC_LIST:
                    RelicPanel.relicInfoDecode(server.inputHandle);
                    break;
                //对药水的解码操作
                case POTION_LIST:
                    PotionPanel.potionDecode(server.inputHandle);
                    break;
                case EFFECT_INFO:
                    ActionNetworkPatches.decodeEffect(server.inputHandle);
                    break;
                //强制消耗某一张牌
                case FORCE_EXHAUST_CARD:
                    PsychicSnoopingAction.exhaustCardDecode(server.inputHandle);
                    break;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

}
