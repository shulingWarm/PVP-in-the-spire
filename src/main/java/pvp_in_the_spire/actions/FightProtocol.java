package pvp_in_the_spire.actions;

import pvp_in_the_spire.ui.PotionPanel;
import pvp_in_the_spire.ui.RelicPanel;
import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.BaseEvent;
import pvp_in_the_spire.screens.midExit.MidExitScreen;
import pvp_in_the_spire.SocketServer;
import pvp_in_the_spire.character.ControlMoster;
import pvp_in_the_spire.network.MessageTriggerInterface;
import pvp_in_the_spire.orbs.MonsterDark;
import pvp_in_the_spire.patches.ActionNetworkPatches;
import pvp_in_the_spire.patches.CardShowPatch.DrawPileSender;
import pvp_in_the_spire.patches.CardShowPatch.HandCardSend;
import pvp_in_the_spire.patches.CardShowPatch.UseCardSend;
import pvp_in_the_spire.patches.RenderPatch;
import pvp_in_the_spire.powers.FakeTimePower;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

//两个玩家对战时的通信协议
public class FightProtocol extends AbstractActionProtocol
    implements MessageTriggerInterface
{

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
    public static final int END_TURN = 10028; //已废弃
    //攻击行为的字段头
    public static final int DAMAGE = 10001;
    //获得格挡的数据类型
    public static final int GAIN_BLOCK = 10002;
    //添加power时的操作
    public static final int APPLY_POWER = 10003;
    //失去生命的操作
    public static final int LOSE_HP = 10004;
    //造球信息的标头
    public static final int CHANNEL_ORB = 10005; //已废弃
    //发送特效时的信息
    public static final int EFFECT_INFO = 10006;
    //充能球激发时的操作
    public static final int EVOKE_INFO = 10007; //已废弃
    //添加扩容信息的操作
    public static final int INCREASE_SLOT = 10008; //已废弃
    //触发循环buff的信息
    public static final int LOOP_BUFF_INFO = 10009; //废弃
    //减少球位的操作
    public static final int DECREASE_SLOT = 10010; //废弃
    //移除所有的格挡
    public static final int REMOVE_ALL_BLOCK = 10011; //废弃
    //改变姿态的操作
    public static final int CHANGE_STANCE = 10012; //废弃
    //发送自己的健康状态
    public static final int SEND_HEALTH = 10013;//废弃
    //跳过敌人回合
    public static final int JUMP_ENEMY_TURN = 10014; //废弃
    //治疗信息
    public static final int HEAL_INFO = 10015; //废弃
    //对怪物的斩杀信息
    public static final int SUICIDE_INFO = 10016; //废弃
    //玩家死亡后重生的信息，把这个信息通知给对方
    public static final int REBIRTH_INFO = 10017; //废弃
    //通知对面执行斩杀操作
    public static final int MAKE_IT_DIE = 10018; //废弃
    //审判的数据头
    public static final int JUDGEMENT = 10019; //废弃
    //新版本的伤害编码
    public static final int DAMAGE_CODE_0812 = 10020;
    //移除所有debuff的编码
    public static final int REMOVE_DEBUFF = 10021;//废弃，主要是药丸没了
    //黑球回合结束时的正常操作
    public static final int DARK_END_TURN = 10022;
    //球更新描述的数据头
    public static final int ORB_UPDATE_DES = 10023;
    //使用牌的消息
    public static final int USE_CARD = 10024;
    //卡牌信息的数据头
    public static final int CARD_INFO_HEAD = 10025; //废弃
    //更新手里的卡牌内容的数据头
    public static final int UPDATE_HAND_CARD = 10026; //后面需要大改
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
    public static final int REMOVE_ORB_INFO = 10034; //废弃
    //时间吞噬的信息更新
    public static final int TIME_EAT_UPDATE = 10035; //后面需要大改
    //给对方施加卡牌
    public static final int TRANSFORM_CARD = 10036; //后面需要大改
    //延迟测试的数据头
    public static final int DELAY_TEST = 10037; //后面需要大改
    //战斗过程中收到的状态信息
    public static final int BATTLE_HEALTH = 10038; //废弃
    //遗物列表
    public static final int RELIC_LIST = 10039; //后面需要大改
    //传输药水的列表
    public static final int POTION_LIST = 10040; //后面需要大改
    //能量更新
    public static final int ENERGY_UPDATE = 10041; //后面需要大改
    //即将抽到的牌的更新
    public static final int DRAWING_CARD_UPDATE = 10042; //后面需要大改
    //强制消耗对方某张牌的命令
    public static final int FORCE_EXHAUST_CARD = 10043; //后面需要大改
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

    //这是确定已经有消息情况下的读取接口
    public boolean readData(DataInputStream stream)
    {
        try
        {
            //临时读取一个数据
            int tempData = stream.readInt();
//            if(tempData != DELAY_TEST)
//                System.out.printf("receive %d\n",tempData);
            switch (tempData)
            {
                case INVALID_TAG:
                    break;
                //收到延迟处理的消息的tag
                case DELAY_TEST:
                    RenderPatch.receiveDelayInfo(stream);
                    break;
                case END_TURN:
                    endReadFlag = true;
                    //清空输入流
//                    while(stream.available()>0)
//                    {
//                        stream.readInt();
//                    }
                    break;
                case DAMAGE:
                    //从数据里面解码出攻击操作
                    ActionNetworkPatches.damageDecode(stream);
                    break;
                case GAIN_BLOCK:
                    ActionNetworkPatches.gainBlockDecode(stream);
                    break;
                //对方打出牌的信息，显示对方打出的牌
                case USE_CARD:
                    UseCardSend.useCardDecode(stream);
                    break;
                //更新对手的手牌内容变化
                case UPDATE_HAND_CARD:
                    HandCardSend.handCardDecode(stream);
                    break;
                //更新抽牌堆的显示牌
                case DRAWING_CARD_UPDATE:
                    DrawPileSender.drawCardDecode(stream);
                    break;
                //能量框显示的更新
                case ENERGY_UPDATE:
                    ControlMoster.energyChangeDecode(stream);
                    break;
                case APPLY_POWER:
                    ActionNetworkPatches.applyPowerDecode(stream);
                    break;
                //移除power的操作
                case REMOVE_POWER:
                    ActionNetworkPatches.removePowerDecode(stream);
                    break;
                //用户自定义的信息
                case CUSTOM_EVENT:
                    handleCustomEvent(stream);
                    break;
                //强制发送power的信息，没有被编码的power通过这个方式发送
                case FORCE_SEND_POWER:
                    ActionNetworkPatches.forceDecodePower(stream);
                    break;
                //新版本的伤害信息
                case DAMAGE_CODE_0812:
                    ActionNetworkPatches.onAttackReceive(stream);
                    break;
                case LOSE_HP:
                    ActionNetworkPatches.loseHpDecode(stream);
                    break;
                case CHANNEL_ORB:
                    //调用解码操作，但不一样的是，这里它会自己把action添加进去
                    ActionNetworkPatches.channelOrbDecode(stream);
                    break;
                case EVOKE_INFO:
                    ControlMoster.instance.evokeOrb();
                    break;
                //当接收到牌信息的时候，把牌的信息记录到哈希表里面
                case CARD_INFO_HEAD:
                    UseCardSend.receiveCardInfo(stream);
                    break;
                case INCREASE_SLOT:
                    ActionNetworkPatches.increaseSlotDecode(stream);
                    break;
                //更新黑球信息的操作
                case ORB_UPDATE_DES:
                    MonsterDark.orbDescriptionDecode(stream);
                    break;
                //循环buff触发的时候，修改头部的黑球信息
                case LOOP_BUFF_INFO:
                    ActionNetworkPatches.loopDecode(stream);
                    break;
                //老头的表的更新
                case TIME_EAT_UPDATE:
                    FakeTimePower.receiveUpdate(stream);
                    break;
                //减少球位的操作，直接通知敌人减少对应的球位
                case DECREASE_SLOT:
                    ControlMoster.instance.decreaseMaxOrbSlots(1);
                    break;
                //移除所有格挡的操作
                case REMOVE_ALL_BLOCK:
                    ActionNetworkPatches.removeAllBlockDecode(stream);
                    break;
                //改变姿态
                case CHANGE_STANCE:
                    ActionNetworkPatches.changeStanceDecode(stream);
                    break;
                //发送战斗内的健康信息
                case BATTLE_HEALTH:
                    HealthSyncAction.battleHealthDecode(stream);
                    break;
                case SEND_HEALTH:
                    ActionNetworkPatches.receiveHealth(stream);
                    break;
                case JUMP_ENEMY_TURN:
                    ActionNetworkPatches.jumpEnemyTurnDecode();
                    break;
                //回血
                case HEAL_INFO:
                    ActionNetworkPatches.healDecode(stream);
                    break;
                //斩杀信息
                case SUICIDE_INFO:
                    ActionNetworkPatches.suicideDecode(stream);
                    break;
                //斩杀后回血的信息
                case REBIRTH_INFO:
                    ActionNetworkPatches.rebirthDecode(stream);
                    break;
                //对敌人执行斩杀的操作
                case MAKE_IT_DIE:
                    ControlMoster.instance.makeItDie();
                    break;
                //对buff调用相应的减少层数
                case REDUCE_POWER:
                    ActionNetworkPatches.reducePowerDecode(stream);
                    break;
                //审判信息
                case JUDGEMENT:
                    ActionNetworkPatches.judgementDecode(stream);
                    break;
                //移除所有debuff的操作
                case REMOVE_DEBUFF:
                    ControlMoster.instance.removeAllDebuff();
                    break;
                //黑球回合结束时的解码操作
                case DARK_END_TURN:
                    MonsterDark.darkEndTurnDecode(stream);
                    break;
                //收到斩杀信息的时候对斩杀信息做解码操作
                case INSTANT_KILL:
                    ActionNetworkPatches.instantKillDecode(stream);
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
                    TransformCardAction.addCardDecode(stream);
                    break;
                //战斗开始时接收对方的遗物列表
                case RELIC_LIST:
                    RelicPanel.relicInfoDecode(stream);
                    break;
                //对药水的解码操作
                case POTION_LIST:
                    PotionPanel.potionDecode(stream);
                    break;
                case EFFECT_INFO:
                    ActionNetworkPatches.decodeEffect(stream);
                    break;
                //强制消耗某一张牌
                case FORCE_EXHAUST_CARD:
                    PsychicSnoopingAction.exhaustCardDecode(stream);
                    break;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }


    //从输入流里面读取数据
    //这个函数永远都会返回false
    public boolean readData(SocketServer server)
    {
        //判断是否有数据可以被读取
        if(!server.isDataAvailable())
        {
            return false;
        }
        return readData(server.inputHandle);
    }

    //这是多线程情况下接收到消息时的回调函数
    @Override
    public void triggerMessage(DataInputStream stream) {
        this.readData(stream);
    }
}
