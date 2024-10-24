package pvp_in_the_spire.patches;

import pvp_in_the_spire.AutomaticSocketServer;
import pvp_in_the_spire.events.*;
import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.Communication;
import pvp_in_the_spire.SocketServer;
import pvp_in_the_spire.stance.CalmStanceEnemy;
import pvp_in_the_spire.stance.DivinityStanceEnemy;
import pvp_in_the_spire.stance.WrathStanceEnemy;
import pvp_in_the_spire.actions.*;
import pvp_in_the_spire.actions.EffectMapping.EffectMapping;
import pvp_in_the_spire.actions.OrbAction.UpdateOrbDescriptionAction;
import pvp_in_the_spire.character.ControlMoster;
import pvp_in_the_spire.character.PlayerMonster;
import pvp_in_the_spire.network.PlayerInfo;
import pvp_in_the_spire.orbs.FakeFrost;
import pvp_in_the_spire.orbs.FakeLighting;
import pvp_in_the_spire.orbs.FakrDark;
import pvp_in_the_spire.orbs.OrbMapping;
import pvp_in_the_spire.patches.connection.MeunScreenFadeout;
import pvp_in_the_spire.powers.BlockablePoisonPower;
import pvp_in_the_spire.powers.FakePoisonPower;
import pvp_in_the_spire.powers.PowerMapping;
import pvp_in_the_spire.powers.PowerShell;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.defect.DecreaseMaxOrbAction;
import com.megacrit.cardcrawl.actions.defect.IncreaseMaxOrbAction;
import com.megacrit.cardcrawl.actions.unique.GreedAction;
import com.megacrit.cardcrawl.actions.unique.RemoveDebuffsAction;
import com.megacrit.cardcrawl.actions.watcher.WallopAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.*;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.FairyPotion;
import com.megacrit.cardcrawl.powers.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Boot;
import com.megacrit.cardcrawl.relics.Calipers;
import com.megacrit.cardcrawl.relics.LizardTail;
import com.megacrit.cardcrawl.stances.AbstractStance;
import com.megacrit.cardcrawl.stances.WrathStance;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;

import com.megacrit.cardcrawl.stances.CalmStance;
import com.megacrit.cardcrawl.stances.DivinityStance;
import com.megacrit.cardcrawl.stances.NeutralStance;
import pvp_in_the_spire.util.Pair;

//两个端口之间的action传输操作
public class ActionNetworkPatches {

    //一个公共的变量，禁止接收战斗相关的事件
    //刚开始的时候不接收战斗相关的事件
    public static boolean disableCombatTrigger = true;

    //对damageInfo做解码
    public static DamageInfo damageInfoDecode(DataInputStream streamHandle)
    {
        try
        {
            //如果是玩家就记录成玩家
            //2024-3-28 这里现在只处理玩家之间的伤害，玩家与monster之间的伤害
            //不在这里处理
            AbstractCreature source = creatureDecode(streamHandle,false);
            //读取伤害的种类
            String typeName = streamHandle.readUTF();
            //读取base数值和output数值
            int baseDamage = streamHandle.readInt();
            int outputDamage = streamHandle.readInt();
            //构造读取的damageInfo
            DamageInfo info = new DamageInfo(source,baseDamage,
                    DamageInfo.DamageType.valueOf(typeName));
            //记录输出的数值
            info.output = outputDamage;
            return info;
        }
        catch (IOException | NullPointerException | IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    //对damageInfo做编码
    public static void damageInfoEncode(DataOutputStream streamHandle,
            DamageInfo info)
    {
        try
        {
            //如果目标是玩家，就改成目标是敌人，因为要换成对方视角
            creatureEncode(streamHandle,info.owner,true);
            //name字段似乎始终没人用，就省略了
            //传入type类型，用字符串传
            streamHandle.writeUTF(info.type.name());
            //记录base数值
            streamHandle.writeInt(info.base);
            streamHandle.writeInt(info.output);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //目标物种的分析方法
    //如果使用了invert 就把玩家换成敌人，把敌人换成玩家
    public static void creatureEncode(DataOutputStream streanHandle,
          AbstractCreature creature, boolean invert)
    {
        try
        {
            //判断是不是player
            if(creature == null || creature.isPlayer)
            {
                streanHandle.writeInt(GlobalManager.myPlayerTag);
            }
            else {
                PlayerMonster playerMonster = (PlayerMonster) creature;
                streanHandle.writeInt(playerMonster.playerTag);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //目标物的反编码
    public static AbstractCreature creatureDecode(DataInputStream streanHandle,boolean invert)
    {
        try
        {
            //读取tag
            int playerTag = streanHandle.readInt();
            //如果tag是自身，那就返回player
            if(playerTag == GlobalManager.myPlayerTag)
                return AbstractDungeon.player;
            //获取目标的player info
            PlayerInfo info = GlobalManager.playerManager.getPlayerInfo(playerTag);
            //如果不是有效的monster,那就直接返回空指针
            if(info == null)
                return null;
            return GlobalManager.playerManager.getPlayerInfo(playerTag).playerMonster;
        } catch (IOException | NullPointerException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    //是否停止发送伤害信息
    public static boolean stopSendAttack = false;

    //对敌人的受伤事件的处理，如果这边敌人受到的伤害，需要告诉对面玩家受到了伤害
    public static void onAttackSend(DamageInfo info,AbstractCreature target)
    {
        //如果目前没有使用网络就不处理了
        if(!SocketServer.USE_NETWORK || stopSendAttack)
            return;
        //目标服务器
        AutomaticSocketServer server = AutomaticSocketServer.getServer();
        System.out.printf("Sending normal damage %s\n",target.name);
        //发送基本的伤害编码信息
        try
        {
            server.streamHandle.writeInt(FightProtocol.DAMAGE_CODE_0812);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        //发送触发伤害的信息
        damageInfoEncode(server.streamHandle,info);
        //发送受到伤害的目标
        creatureEncode(server.streamHandle,target,true);
        //正式地发送消息
        server.send();
    }

    //对敌人受伤事件的解码操作
    public static void onAttackReceive(DataInputStream streamHandle)
    {
        //读取解码出来的伤害信息
        DamageInfo info = damageInfoDecode(streamHandle);
        //读取处理的目标
        AbstractCreature target = creatureDecode(streamHandle,false);
        if(info==null || target == null)
        {
            System.out.println("Receive damage failed");
            return;
        }
        System.out.println("Receive normal damage");
        //调用对目标的伤害
        //此时停止触发伤害信息
        stopSendAttack = true;
        //必须是能处理战斗事件的时候才这样做
        if(!disableCombatTrigger)
            target.damage(info);
        stopSendAttack = false;
    }

    //玩家受伤时的补丁，把这个伤害信息传给对面，主要是处理掉血的操作
    @SpirePatch(clz = AbstractPlayer.class, method = "damage")
    public static class PlayerDamageSendPatch
    {
        @SpirePostfixPatch
        public static void fix(AbstractPlayer __instance,
           DamageInfo info)
        {
            //如果是禁用伤害触发的情况下，直接跳过这个地方就可以
            if(stopSendAttack)
            {
                return;
            }
            //调用发送伤害信息，到底用不用发送，那里会判定
            onAttackSend(info,__instance);
        }
    }


    //对攻击行为的编码
    //由于action里面的info是私有的，只能通过这种方式把它传进来了
    public static void damageEncode(DataOutputStream streamHandle,
            DamageAction action,DamageInfo info)
    {
        System.out.println("encode damage action");
        //写入数据头的类型
        try
        {
            streamHandle.writeInt(FightProtocol.DAMAGE);
            //写入它的内部dmageInfo的信息
            damageInfoEncode(streamHandle,info);
            //写入攻击目标
            creatureEncode(streamHandle,action.target,true);
            //写入打击效果
            streamHandle.writeUTF(action.attackEffect.name());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //打击效果的反编码
    //但加入的是敌人版本的攻击，这样就不会触发发送网络的事件了
    public static void damageDecode(DataInputStream streamHandle)
    {
        System.out.println("decode damage action");
        //初始化damageInfo,读取info里面的内容
        DamageInfo info = damageInfoDecode(streamHandle);
        //读取操作的目标 编码的时候是反的，但读取的时候就不需要反了
        AbstractCreature target = creatureDecode(streamHandle,false);
        try
        {
            //记录打击的效果
            String attackName = streamHandle.readUTF();
            if(info == null || target == null)
                return;
            //针对电球释放的特别处理，如果是电球就把它放上面优先处理
            if(info.type == DamageInfo.DamageType.THORNS)
            {
                AbstractDungeon.actionManager.addToTop(
                    new DamageActionEnemy(target,info,AbstractGameAction.AttackEffect.valueOf(attackName))
                );
            }
            else {
                AbstractDungeon.actionManager.addToBottom(
                    new DamageActionEnemy(target,info,AbstractGameAction.AttackEffect.valueOf(attackName))
                );
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //对获得格挡信息的编码
//    public static void gainBlockEncode(DataOutputStream streamHandle,
//           GainBlockAction action)
//    {
//        //记录action的数据头
//        try
//        {
//            streamHandle.writeInt(FightProtocol.GAIN_BLOCK);
//            //记录操作目标
//            creatureEncode(streamHandle,action.target,true);
//            creatureEncode(streamHandle,action.source,true);
//            //记录格挡数
//            streamHandle.writeInt(action.amount);
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//    }

    //是否阻止发送获得格挡的操作
    //这是为了编码和解码反复循环传导
    public static boolean stopSendBlockGain = false;

    //直接获得格挡信息的编码
    public static void directGainBlockEncode(DataOutputStream streamHandle,
         int blockAmount,AbstractCreature target)
    {
        //记录action的数据头
        try
        {
            streamHandle.writeInt(FightProtocol.GAIN_BLOCK);
            //记录目标
            creatureEncode(streamHandle,target,true);
            //记录格挡数
            streamHandle.writeInt(blockAmount);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }



    //获得格挡的解码
    public static void gainBlockDecode(DataInputStream streamHandle)
    {
        //记录格挡数
        try
        {
            //解码目标
            AbstractCreature target = creatureDecode(streamHandle,false);
            int blockNum = streamHandle.readInt();
            if(target==null)
                return;
//            AbstractDungeon.actionManager.addToBottom(
//                    new GainBlockEnemy(target,source,blockNum)
//            );
            //直接给敌人添加格挡
            //这个时候不要触发格挡信息的发送
            stopSendBlockGain = true;
            //处于非战斗状态时不触发这个消息
            if(!disableCombatTrigger)
                target.addBlock(blockNum);
            stopSendBlockGain = false;
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //玩家直接获得格挡时的信息操作
    @SpirePatch(clz = AbstractCreature.class,method = "addBlock")
    public static class DirectGainBlockPatch
    {
        @SpirePostfixPatch
        public static void fix(AbstractCreature __instance,int blockAmount)
        {
            //如果不使用网络的话就不必处理了
            if(!SocketServer.USE_NETWORK || stopSendBlockGain)
                return;
            //对格挡信息做编码
            AutomaticSocketServer server = AutomaticSocketServer.getServer();
            directGainBlockEncode(server.streamHandle,blockAmount,__instance);
            server.send();
        }
    }

    //解析球的种类
    public static int getOrbType(AbstractOrb orb)
    {
        //依次判断是哪种球
        if(orb instanceof Lightning)
            return FightProtocol.ORB_LIGHTING;
        if(orb instanceof Frost)
            return FightProtocol.ORB_BLOCK;
        if(orb instanceof Dark)
            return FightProtocol.ORB_DARK;
        if(orb instanceof Plasma)
            return FightProtocol.ORB_PLASMA;
        return -1;
    }

    //获得球信息的编码器
    public static void channelOrbEncode(DataOutputStream streamHandle,
        AbstractOrb orb)
    {
        try
        {
            //球的种类的标号
            int orbType = getOrbType(orb);
            //如果是一个未知的球的种类就算了
            if(orbType == -1)
                return;
            //发送基本的造球信号
            streamHandle.writeInt(FightProtocol.CHANNEL_ORB);
            //发送球的键值，用于让两边的球一一对应
            int orbKey = OrbMapping.addPlayerOrb(orb);
            streamHandle.writeInt(orbKey);
            //发送球的种类
            streamHandle.writeInt(orbType);
            //发送这个球的激发数值，这是给黑球递归用的
            streamHandle.writeInt(orb.evokeAmount);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //获得球信息时的解码器
    public static void channelOrbDecode(DataInputStream streamHandle)
    {
        //读取球的种类
        try
        {
            //读取球的键值
            int orbKey = streamHandle.readInt();
            int orbType = streamHandle.readInt();
            //接收球的激发数值
            int evokeAmount = streamHandle.readInt();
            //目标的球的种类
            AbstractOrb targetOrb;
            //电球的情况
            if(orbType == FightProtocol.ORB_LIGHTING)
            {
                targetOrb = new FakeLighting();
            }
            else if(orbType == FightProtocol.ORB_BLOCK)
            {
                //冰球
                targetOrb = new FakeFrost();
            }
            else if(orbType == FightProtocol.ORB_DARK)
            {
                //黑球 但这里加的是假的黑球
                targetOrb = new FakrDark();
            }
            else if(orbType == FightProtocol.ORB_PLASMA)
            {
                targetOrb = new Plasma();
            }
            else {
                return;
            }
            //修改球的激发数值
            targetOrb.evokeAmount = evokeAmount;
            //把这个球添加到列表里面
            //如果数字是-1这多半是有问题的，这个时候就不加入了
            if(orbKey==-1)
            {
                System.out.println("invalid id orb");
            }
            else {
                OrbMapping.addMonsterOrb(targetOrb,orbKey);
            }
            //添加一个新的目标球，添加到敌人的列表里面
            ControlMoster.instance.channelOrb(targetOrb);
//            AbstractDungeon.actionManager.addToTop(
//                new ChannelActionEnemy(
//                    targetOrb, ControlMoster.instance
//                )
//            );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

//    @SpirePatch(clz = DamageAction.class, method = SpirePatch.CONSTRUCTOR,
//            paramtypez = {AbstractCreature.class, DamageInfo.class, AbstractGameAction.AttackEffect.class})
//    //传导打击相关的操作
//    public static class DamageInfoSend
//    {
//
//        //调用构造函数的后处理
//        @SpirePostfixPatch
//        public static void postFix(DamageAction __instance,
//                                   AbstractCreature target, DamageInfo info, AbstractGameAction.AttackEffect effect)
//        {
//            if(!SocketServer.USE_NETWORK)
//                return;
//            //对damageAction做编码 但只有处于自己的回合的时候才需要这样做
//            //所有信息都会发送，但接收到的伤害信息会被转换成另一种形式
//            //if(FightProtocol.endReadFlag)
//            {
//                System.out.println("direct send");
//                AutomaticSocketServer server = AutomaticSocketServer.getServer();
//                //把打击信息发送出去
//                ActionNetworkPatches.damageEncode(server.streamHandle,
//                        __instance,info);
//                server.send();
////                else {//正常情况下伤害信息是延迟发送的
////                    System.out.println("build send action");
////                    SendDamageAction tempAction = new SendDamageAction(__instance,info);
////
////                    //准备发送信息的action
////                    System.out.println("add to action queue");
////                    AbstractDungeon.actionManager.addToTop(tempAction);
////                }
//            }
//        }
//    }

    //发送添加球信息的操作的情况
    //当有新的球被添加时，把这个信息告诉对面
    @SpirePatch(clz = AbstractPlayer.class, method = "channelOrb")
    public static class ChannelOrbSend
    {
        @SpirePostfixPatch
        public static void fix(AbstractPlayer __instance, AbstractOrb orbToSet)
        {
            //如果不使用网络就不弄了
            if(!SocketServer.USE_NETWORK)
                return;
            //如果当前是自己的回合再发送
            //if(FightProtocol.endReadFlag)
            {
                //发送添加球的信息
                Communication.sendEvent(new ChannelOrbEvent(orbToSet));
            }
        }
    }

    //对所有敌人的攻击操作
    //直接按照普通攻击就行，因为这是单挑
//    @SpirePatch(clz = DamageAllEnemiesAction.class, method = SpirePatch.CONSTRUCTOR,
//            paramtypez = {AbstractCreature.class, int[].class,
//                    DamageInfo.DamageType.class,AbstractGameAction.AttackEffect.class,
//            boolean.class})
//    //传导打击相关的操作
//    public static class DamageAllSend
//    {
//        //调用构造函数的后处理
//        @SpirePostfixPatch
//        public static void postFix(DamageAllEnemiesAction __instance,
//                                   AbstractCreature source, int[] amount, DamageInfo.DamageType type,
//                                   AbstractGameAction.AttackEffect effect, boolean isFast)
//        {
//            if(!SocketServer.USE_NETWORK)
//                return;
//            //对damageAction做编码 但只有处于自己的回合的时候才需要这样做
//            if(FightProtocol.endReadFlag)
//            {
//                //整合出一个全体攻击的效果
//                DamageInfo info = new DamageInfo(source,amount[0],type);
//                //整合出假的damageAction 当它被构造的时候它会自动发送的，不必再发送
//                DamageAction fakeAction =
//                        new DamageAction(AbstractDungeon.getCurrRoom().monsters.monsters.get(0),
//                            info,effect);
//            }
//        }
//    }

    //构造防御的操作有两种，需要分别捕捉 这是另一种构造函数的参数列表
//    @SpirePatch(clz = GainBlockAction.class, method = SpirePatch.CONSTRUCTOR,
//            paramtypez = {AbstractCreature.class, int.class})
//    //传导打击相关的操作
//    public static class GainBlockSendParam2
//    {
//        //调用构造函数的后处理
//        @SpirePostfixPatch
//        public static void postFix(GainBlockAction __instance,
//                                   AbstractCreature target, int amount)
//        {
//            if(!SocketServer.USE_NETWORK)
//                return;
//            //if(FightProtocol.endReadFlag)
//            {
//                //为了防止出现空指针，把source也确保它成为target
//                __instance.source = target;
//                AutomaticSocketServer server = AutomaticSocketServer.getServer();
//                //把打击信息发送出去
//                gainBlockEncode(server.streamHandle,__instance);
//                server.send();
//            }
//        }
//    }
//
//    //构造防御时的操作
//    @SpirePatch(clz = GainBlockAction.class, method = SpirePatch.CONSTRUCTOR,
//            paramtypez = {AbstractCreature.class, AbstractCreature.class, int.class})
//    //传导打击相关的操作
//    public static class GainBlockSend
//    {
//        //调用构造函数的后处理
//        @SpirePostfixPatch
//        public static void postFix(GainBlockAction __instance,
//                                   AbstractCreature target, AbstractCreature source, int amount)
//        {
//            if(!SocketServer.USE_NETWORK)
//                return;
//            //if(FightProtocol.endReadFlag)
//            {
//                AutomaticSocketServer server = AutomaticSocketServer.getServer();
//                //把打击信息发送出去
//                gainBlockEncode(server.streamHandle,__instance);
//                server.send();
//            }
//        }
//    }

    //解析炸弹的伤害值
    public static int parseBombDamage(AbstractPower power)
    {
        TheBombPower bombPower = (TheBombPower) power;
        try
        {
            //获取伤害信息
            Field tempField = TheBombPower.class.getDeclaredField("damage");
            //解析出炸弹里面的伤害信息
            tempField.setAccessible(true);
            return (int)tempField.get(bombPower);
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    //对power信息的编码
    public static void powerEncode(DataOutputStream streamHandle,
           AbstractPower power)
    {
        try
        {
            //要发送的数量
            int sendAmount = power.amount;
            //需要另外判断一下是不是炸弹的id,如果是炸弹的话需要额外处理一下
            if(power instanceof TheBombPower)
            {
                streamHandle.writeUTF(TheBombPower.POWER_ID);
                //更改amount,炸弹的情况下需要使用伤害值
                sendAmount = parseBombDamage(power);
            }
            else {
                //发送poewr的id
                streamHandle.writeUTF(power.ID);
            }
            //发送power的作用目标
            creatureEncode(streamHandle,power.owner,true);
            //发送buff的层数
            streamHandle.writeInt(sendAmount);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //对power信息的解码操作
    public static AbstractPower powerDecode(DataInputStream streamHandle)
    {
        //读取buff的层数
        try
        {
            //获得power的id
            String powerId = streamHandle.readUTF();
            //读取power作用的目标
            AbstractCreature owner = creatureDecode(streamHandle,false);
            int buffNum = streamHandle.readInt();
            //返回生成的抽象buff
            return PowerMapping.getPowerById(
                powerId,owner,buffNum,false
            );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    //添加buff时的编码操作
    public static void applyPowerEncode(DataOutputStream streamHandle,
        ApplyPowerAction action,AbstractPower power)
    {
        //添加给power时的专属操作
        try
        {
            streamHandle.writeInt(FightProtocol.APPLY_POWER);
            //编码里面的power信息
            powerEncode(streamHandle,power);
            //添加目标
            creatureEncode(streamHandle,action.target,true);
            creatureEncode(streamHandle,action.source,true);
            //添加的层数直接从power里面取就可以
            //记录buff的效果
            String effectType = action.attackEffect.name();
            streamHandle.writeUTF(effectType);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //添加buff时的解码操作
    public static void applyPowerDecode(DataInputStream streamHandle)
    {
        //解析power
        AbstractPower power = powerDecode(streamHandle);
        //解析目标
        AbstractCreature target = creatureDecode(streamHandle,false);
        AbstractCreature source = creatureDecode(streamHandle,false);
        try
        {
            //buff的作用效果
            String buffEffect = streamHandle.readUTF();
            if(power==null || target==null || source==null)
                return;
            //阻断触发
            BuffInfoSend.stopTrigger=true;
            AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(
                        target,source,power,power.amount,false,
                        AbstractGameAction.AttackEffect.valueOf(buffEffect)
                )
            );
            //恢复阻断触发
            BuffInfoSend.stopTrigger=false;
            //如果使用的power是集中并且目标是敌方的话，通知敌人修改充能球显示
            if(power instanceof FocusPower &&
                power.owner instanceof ControlMoster)
            {
                //更新集中显示的action,一定要放那个后面
                AbstractDungeon.actionManager.addToBottom(
                        new UpdateOrbDescriptionAction((ControlMoster)power.owner)
                );
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //通过反射强制发送所有的power
    public static void forceEncodePower(DataOutputStream streamHandle,
        AbstractPower power,
        int amount,AbstractCreature target,AbstractCreature source,
        AbstractGameAction.AttackEffect effect //施加buff时的特效
    )
    {
        //通过反映射确定power的类名
        String className = power.getClass().getName();
        //发送强制power的标头
        try
        {
            streamHandle.writeInt(FightProtocol.FORCE_SEND_POWER);
            //发送power的类名
            streamHandle.writeUTF(className);
            //streamHandle.writeUTF(power.ID);
            //发送power的施加者
            creatureEncode(streamHandle,source,true);
            creatureEncode(streamHandle,target,true);
            //发送施加的power层数
            streamHandle.writeInt(amount);
            //记录施加buff时的特效
            streamHandle.writeUTF(effect.name());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //测试获得power的第一种构造器
    public static Constructor<?> getPowerConstructor(Class<?> classType,int type)
    {
        try
        {
            switch (type)
            {
                case 0:
                    return classType.getConstructor(AbstractCreature.class,int.class);
                case 1:
                    return classType.getConstructor(AbstractCreature.class);
                case 2:
                    return classType.getConstructor(AbstractCreature.class,
                            AbstractCreature.class,int.class);
            }
        }
        catch (NoSuchMethodException e)
        {
            return null;
        }
        return null;
    }

    //返回构造函数的类型和对应的构造函数
    public static Pair<Integer,Constructor<?>> getPowerConstructor(Class<?> classType)
    {
        for(int i=0;i<3;++i)
        {
            Constructor<?> tempConstructor = getPowerConstructor(classType,i);
            //判断是否有实际内容
            if(tempConstructor!=null)
                return new Pair<Integer,Constructor<?>>(i,tempConstructor);
        }
        return new Pair<Integer,Constructor<?>>(-1,null);
    }

    //根据构造函数和构造函数的类型执行构造
    public static AbstractPower constructPowerByType(
        int type,
        Constructor<?> constructor,
        AbstractCreature source,
        AbstractCreature target,
        int amount
    )
    {
        try
        {
            switch (type)
            {
                case (0):
                    return (AbstractPower) constructor.newInstance(target,amount);
                case (1):
                    return (AbstractPower) constructor.newInstance(target);
                case (2):
                    return (AbstractPower) constructor.newInstance(target,source,amount);
            }
        }
        catch (IllegalAccessException |
               InstantiationException |
               InvocationTargetException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static AbstractPower buildPowerByReflect(String className,
        AbstractCreature source,
        AbstractCreature target, int amount)
    {
        //寻找这个class
        Class<?> classType;
        try
        {
            classType = Class.forName(className);
        }
        catch (ClassNotFoundException e)
        {
            System.out.printf("%s not found\n",className);
            return null;
        }
        //最后构造出的power
        AbstractPower ansPower = null;
        Pair<Integer,Constructor<?>> constructor = getPowerConstructor(classType);
        if(constructor.second != null)
        {
            ansPower = constructPowerByType(constructor.first,constructor.second,
                    source,target,amount);
        }
        return ansPower;
    }


    //通过反射机制对power的强制解码
    public static void forceDecodePower(DataInputStream streamHandle)
    {
        String className;
        AbstractCreature source;
        AbstractCreature target;
        //施加buff时的特效
        AbstractGameAction.AttackEffect effect;
        int amount;
        try
        {
            className = streamHandle.readUTF();
            //读取source信息
            source = creatureDecode(streamHandle,false);
            target = creatureDecode(streamHandle,false);
            //读取施加的buff的数值
            amount = streamHandle.readInt();
            //读取施加buff时的特效
            String effectString = streamHandle.readUTF();
            effect = AbstractGameAction.AttackEffect.valueOf(effectString);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return;
        }
        //构造buff
        AbstractPower power = buildPowerByReflect(className,source,target,amount);
        if(target==null)
        {
            return;
        }
        //如果构造成功的话，添加这个power
        if(power!=null && power.region48!=null && power.region128!=null)
        {
            //临时禁用发送buff的触发
            BuffInfoSend.stopTrigger = true;
            AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(target,source,
                PowerShell.createShell(power),amount,effect)
            );
            BuffInfoSend.stopTrigger = false;
        }
    }

    //添加buff时的操作
    @SpirePatch(clz = ApplyPowerAction.class, method = SpirePatch.CONSTRUCTOR,
            paramtypez = {AbstractCreature.class, AbstractCreature.class,
                    AbstractPower.class,int.class,boolean.class,AbstractGameAction.AttackEffect.class})
    //传导打击相关的操作
    public static class BuffInfoSend
    {

        //阻断触发的标志
        public static boolean stopTrigger = false;

        //把poweraction里面的毒强行换成自定义的毒
        public static void changePoisonPower(ApplyPowerAction action,AbstractPower oldPower,
             AbstractCreature source)
        {
            //把它的毒设置成可读取
            try
            {
                //获取info信息
                Field tempField = ApplyPowerAction.class.getDeclaredField("powerToApply");
                //把info设置成可用
                tempField.setAccessible(true);
                tempField.set(action, oldPower.owner.isPlayer ?
                    new BlockablePoisonPower(oldPower.owner,source,oldPower.amount) :
                    new FakePoisonPower(oldPower.owner,source,oldPower.amount));
            }
            catch (NoSuchFieldException | IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }

        //调用构造函数的后处理
        @SpirePostfixPatch
        public static void postFix(ApplyPowerAction __instance,
                                   AbstractCreature target, AbstractCreature source,
                                   AbstractPower powerToApply, int stackAmount, boolean isFast,
                                   AbstractGameAction.AttackEffect effect)
        {
            //阻断触发的情况也什么都不做
            if(!SocketServer.USE_NETWORK || stopTrigger)
                return;

            {
                //另外需要观察一下这种buff有没有映射过，没有映射过就别发了
                PowerMapping.initCreatorMapper();
                AutomaticSocketServer server = AutomaticSocketServer.getServer();
                if(!(powerToApply instanceof TheBombPower) &&
                    !PowerMapping.creatorMapper.containsKey(powerToApply.ID))
                {
                    //强制对这个buff进行编码
                    forceEncodePower(server.streamHandle,powerToApply,
                        __instance.amount,__instance.target,
                        __instance.source,__instance.attackEffect);
                }
                else {
                    //把添加power的信息发出去
                    applyPowerEncode(server.streamHandle,__instance,powerToApply);
                }
                server.send();
            }
            //判断施加的是不是毒，如果是毒的话就把它换成可格挡的毒
            if(powerToApply instanceof PoisonPower)
            {
                changePoisonPower(__instance,powerToApply,source);
            }
        }
    }

    //编码失去生命的信息
    public static void loseHpEncode(DataOutputStream streamHandle,
        LoseHPAction action)
    {
        //写入失去生命的基本操作
        try
        {
            streamHandle.writeInt(FightProtocol.LOSE_HP);
            //写入操作目标
            creatureEncode(streamHandle,action.target,true);
            creatureEncode(streamHandle,action.source,true);
            //记录数据
            streamHandle.writeInt(action.amount);
            //记录操作效果
            String effectType = action.attackEffect.name();
            streamHandle.writeUTF(effectType);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //解码失去生命时的操作
    public static void loseHpDecode(DataInputStream streamHandle)
    {
        System.out.println("find target and source");
        //解析目标
        AbstractCreature target = creatureDecode(streamHandle,false);
        AbstractCreature source = creatureDecode(streamHandle,false);
        try
        {
            //读取数据
            System.out.println("read amount");
            int amount = streamHandle.readInt();
            //buff的作用效果
            System.out.println("read effect");
            String buffEffect = streamHandle.readUTF();
            if(target==null)
                return;
            AbstractDungeon.actionManager.addToBottom(
                new LoseHPAction(
                    target,source,amount,AbstractGameAction.AttackEffect.valueOf(buffEffect)
                )
            );
        }
        catch (IOException | IllegalArgumentException e)
        {
            e.printStackTrace();
        }
    }


    //失去生命的操作
//    @SpirePatch(clz = LoseHPAction.class, method = SpirePatch.CONSTRUCTOR,
//            paramtypez = {AbstractCreature.class,AbstractCreature.class,int.class,AbstractGameAction.AttackEffect.class})
//    //传导打击相关的操作
//    public static class LoseHpPatch
//    {
//        //调用构造函数的后处理
//        @SpirePostfixPatch
//        public static void postFix(LoseHPAction __instance,
//                                   AbstractCreature target, AbstractCreature source,
//                                   int amount, AbstractGameAction.AttackEffect effect)
//        {
//            if(!SocketServer.USE_NETWORK)
//                return;
//            //对damageAction做编码 但只有处于自己的回合的时候才需要这样做
//            if(FightProtocol.endReadFlag)
//            {
//                AutomaticSocketServer server = AutomaticSocketServer.getServer();
//                //编码失去生命的信息
//                loseHpEncode(server.streamHandle,__instance);
//                server.send();
//            }
//        }
//    }

    //对特效信息做编码
    public static void encodeEffect(DataOutputStream streamHandle,
        AbstractGameEffect effect,float duration)
    {
        try
        {
            //获取特效本身的编号，如果特效本身是无效的就不用发了
            int effectId = EffectMapping.getEffectId(effect);
            if(effectId == -1)
                return;
            //发送特效时的编码
            streamHandle.writeInt(FightProtocol.EFFECT_INFO);
            //特效本身的编号
            streamHandle.writeInt(effectId);
            //特效的作用目标，目前默认发到对面去的都是作用于对面的玩家的
            //也就是这边的敌人
            streamHandle.writeInt(FightProtocol.PLAYER);
            //写入特效的间隔时长
            streamHandle.writeFloat(duration);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //对特效信息的解码
    public static void decodeEffect(DataInputStream streamHandle)
    {
        try
        {
            int effectId = streamHandle.readInt();
            //读取目标
            AbstractCreature target = creatureDecode(streamHandle,false);
            //去找creature的映射表做解码操作
            AbstractGameEffect effect = null;
            //如果处于禁用状态，把该读取的读了就行了
            if(target != null)
                effect = EffectMapping.effectIdDecode(effectId,target);
            //读取特效的时长
            float duration = streamHandle.readFloat();
            //把这个effect添加到action里面
            if(effect != null)
                AbstractDungeon.actionManager.addToTop(
                        new VFXActionEnemy(effect,duration)
                );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    //特效传导的action
    //只传导部分特效
//    @SpirePatch(clz = VFXAction.class, method = SpirePatch.CONSTRUCTOR,
//            paramtypez = {AbstractCreature.class, AbstractGameEffect.class,float.class})
//    public static class VFXEffectSend
//    {
//        //在构造特效的时候准备一个发送特效的操作
//        @SpirePostfixPatch
//        public static void fix(VFXAction __instance,AbstractCreature source, AbstractGameEffect effect, float duration)
//        {
//            if(!SocketServer.USE_NETWORK)
//                return;
//            //if(FightProtocol.endReadFlag)
//            {
//                //添加一个发送信息的action
//                AbstractDungeon.actionManager.addToTop(
//                        new SendEffectAction(effect,duration)
//                );
//            }
//        }
//    }

    //对特效信息做编码的另一种形式
//    @SpirePatch(clz = VFXAction.class, method = SpirePatch.CONSTRUCTOR,
//            paramtypez = {AbstractCreature.class, AbstractGameEffect.class,float.class,boolean.class})
//    public static class VFXEffectSend2
//    {
//        //在构造特效的时候准备一个发送特效的操作
//        @SpirePostfixPatch
//        public static void fix(VFXAction __instance,AbstractCreature source,
//           AbstractGameEffect effect, float duration,boolean topLevel)
//        {
//            if(!SocketServer.USE_NETWORK)
//                return;
//            //if(FightProtocol.endReadFlag)
//            {
//                //添加一个发送信息的action
//                AbstractDungeon.actionManager.addToTop(
//                        new SendEffectAction(effect,duration)
//                );
//            }
//        }
//    }

    //激发球时触发的操作
    @SpirePatch(clz = AbstractPlayer.class, method = "evokeOrb")
    public static class EvokeInfoSend
    {
        @SpirePrefixPatch
        public static void fix()
        {
            if(SocketServer.USE_NETWORK)
            {
                //通知对面激发一个球
                //if(FightProtocol.endReadFlag)
                {
                    Communication.sendEvent(new EvokeOrbEvent());
                }
            }
        }
    }

    //对扩容信息的编码
    public static void increaseSlotEncode(DataOutputStream streamHandle,
        int slotNum)
    {
        //发送扩容的操作信息
        try
        {
            streamHandle.writeInt(FightProtocol.INCREASE_SLOT);
            //记录需要扩充多少个球
            streamHandle.writeInt(slotNum);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //对扩容信息的解码
    public static void increaseSlotDecode(DataInputStream streamHandle)
    {
        try
        {
            //读取需要扩充多少个球
            int slotNum = streamHandle.readInt();
            //通知敌人那边准备开球
            if(ControlMoster.instance!=null)
                ControlMoster.instance.increaseMaxOrbSlots(slotNum,true);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    //充能球的扩容操作
    @SpirePatch(clz = IncreaseMaxOrbAction.class, method = SpirePatch.CONSTRUCTOR)
    public static class IncreaseOrbSlotSend
    {
        @SpirePostfixPatch
        public static void fix(IncreaseMaxOrbAction __instance,
           int slotIncrease)
        {
            //判断是否需要发送信息
            if(SocketServer.USE_NETWORK)
            {
                Communication.sendEvent(new IncreaseOrbSlotEvent(slotIncrease));
            }
        }
    }

    //减少球位的操作
    @SpirePatch(clz = DecreaseMaxOrbAction.class, method = SpirePatch.CONSTRUCTOR)
    public static class DecreaseSlotSend
    {
        @SpirePostfixPatch
        public static void fix(DecreaseMaxOrbAction __instance,
                               int slotDecrease)
        {
            //判断是否需要发送信息
            if(SocketServer.USE_NETWORK)
            {
                if(FightProtocol.endReadFlag)
                {
                    Communication.sendEvent(new IncreaseOrbSlotEvent(-1));
                }
            }
        }
    }

    //黑球激发时的响应函数，黑球激发时不会直接触发伤害信息，因此需要捕捉黑球的伤害信息
    //然后把黑球的伤害信息直接转换成一个普通的伤害信息
    @SpirePatch(clz = DarkOrbEvokeAction.class, method = SpirePatch.CONSTRUCTOR)
    public static class EvokeDarkSend
    {
        @SpirePostfixPatch
        public static void fix(DarkOrbEvokeAction __instance,
           DamageInfo info, AbstractGameAction.AttackEffect effect)
        {
            if(SocketServer.USE_NETWORK)
            {
                //激发事件不再单独处理
                //if(FightProtocol.endReadFlag)
                {
                    AutomaticSocketServer server = AutomaticSocketServer.getServer();
                    //构造一个虚假的damageAction,构造的时候它会自己发出去的
                    DamageAction tempAction =
                        new DamageAction(AbstractDungeon.getCurrRoom().monsters.monsters.get(0),
                        info,effect);
                }
            }
        }
    }

    //我方出牌时也需要监听对面的一些信息，虽然这个时候不需要监听所有信息
    //但专门针对静电释放的时候需要做这样的工作
    @SpirePatch(clz = CardCrawlGame.class, method = "update")
    public static class MyTurnListenPatch
    {

        //静态的接收消息，防止频繁构造对象
        public static FightProtocol tempProtocol;
        public static int callTime = 0;

        @SpirePostfixPatch
        public static void fix(CardCrawlGame __instance)
        {
            //不用网的情况下就不用管这里了 并且必须等连接成功了再处理这个地方
            if(!SocketServer.USE_NETWORK || (!MeunScreenFadeout.connectOk) ||
                AutomaticSocketServer.globalServer == null)
                return;
            //只要连接上了就会一直处于监听状态
            if(tempProtocol == null)
            {
                tempProtocol = new FightProtocol();
            }
            //叠加调用的次数
            callTime++;
            //如果达到了目标次数，就读取一次数据
            if(callTime >= 1)
            {
                callTime = 0;
                //调用一次数据读取，主要是球相关的事件
                tempProtocol.readData(AutomaticSocketServer.getServer());
            }
        }
    }

    //对loop信息的编码
    public static void loopEncode(DataOutputStream streamHandle,
          LoopPower power)
    {
        //发送触发循环信息的数据头
        try
        {
            streamHandle.writeInt(FightProtocol.LOOP_BUFF_INFO);
            //写入循环的层数
            streamHandle.writeInt(power.amount);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //对loop信息的解码
    public static void loopDecode(DataInputStream streamHandle)
    {
        try
        {
            //读取loop的层数
            int amount = streamHandle.readInt();
            //通知monster触发黑球
            ControlMoster.instance.loopOrbStartTurn(amount);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //循环buff的触发，开场的时候检查一下有没有黑球，有的话加上相应的层数
//    @SpirePatch(clz = LoopPower.class, method = "atStartOfTurn")
//    public static class LoopBuffTurnInfo
//    {
//        @SpirePostfixPatch
//        public static void fix(LoopPower __instance)
//        {
//            //触发的时候发送相关的信息
//            if(SocketServer.USE_NETWORK)
//            {
//                AutomaticSocketServer server = AutomaticSocketServer.getServer();
//                loopEncode(server.streamHandle,__instance);
//                server.send();
//            }
//        }
//    }

    //对移除所有格挡做编码
    public static void removeAllBlockEncode(DataOutputStream streamHandle,
        AbstractCreature target,AbstractCreature source)
    {
        try
        {
            streamHandle.writeInt(FightProtocol.REMOVE_ALL_BLOCK);
            //发送target和source
            creatureEncode(streamHandle,target,true);
            creatureEncode(streamHandle,source,true);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //解码移除所有格挡的操作
    public static void removeAllBlockDecode(DataInputStream streamHandle)
    {
        //读取target
        AbstractCreature target = creatureDecode(streamHandle,false);
        AbstractCreature source = creatureDecode(streamHandle,false);
        if(target==null || source==null)
        {
            return;
        }
        //生成移除所有格挡的操作
        AbstractDungeon.actionManager.addToBottom(
                new RemoveAllBlockAction(target,source)
        );
    }

//    //移除所有格挡的操作
//    @SpirePatch(clz = RemoveAllBlockAction.class, method = SpirePatch.CONSTRUCTOR)
//    public static class RemoveAllBlockSend
//    {
//        @SpirePostfixPatch
//        public static void fix(RemoveAllBlockAction __instance,AbstractCreature target, AbstractCreature source)
//        {
//            //触发的时候发送相关的信息
//            if(SocketServer.USE_NETWORK)
//            {
//                //这个只需要单方面发就行，不然就死循环了
//                if(FightProtocol.endReadFlag)
//                {
//                    AutomaticSocketServer server = AutomaticSocketServer.getServer();
//                    removeAllBlockEncode(server.streamHandle,target,source);
//                    server.send();
//                }
//            }
//        }
//    }

    //对姿态转变信息的编码
    public static void changeStanceEncode(DataOutputStream streamHandle,
          String stanceId)
    {
        //写入转换姿态的操作
        try
        {
            streamHandle.writeInt(FightProtocol.CHANGE_STANCE);
            //发送新的姿态的id
            streamHandle.writeUTF(stanceId);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //对改变姿态的信息做解码
    public static void changeStanceDecode(DataInputStream streamHandle)
    {
        //读取姿态的种类
        try
        {
            //读取新的姿态的id
            String stanceId = streamHandle.readUTF();
            //要改变的新的姿态
            AbstractStance newStance;
            //根据姿态的id确定一下要转换成哪个姿态
            switch (stanceId)
            {
                case WrathStance.STANCE_ID:
                    newStance = new WrathStanceEnemy(ControlMoster.instance);
                    break;
                case CalmStance.STANCE_ID:
                    newStance = new CalmStanceEnemy(ControlMoster.instance);
                    break;
                case DivinityStance.STANCE_ID:
                    newStance = new DivinityStanceEnemy(ControlMoster.instance);
                    break;
                default:
                    newStance = new NeutralStance();
            }
            //直接改变玩家的姿态，不用调用action了
            ControlMoster.instance.changeStance(newStance);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    //对修改姿态的action的捕获
    @SpirePatch(clz = ChangeStanceAction.class, method = SpirePatch.CONSTRUCTOR,
            paramtypez = {String.class})
    public static class ChangeStanceActionSend
    {
        //后处理的形式，遇到就发送
        @SpirePostfixPatch
        public static void fix(ChangeStanceAction __instance,
           String stanceId)
        {
            Communication.sendEvent(new ChangeStanceEvent(stanceId));
        }
    }

    //接收玩家目前的健康状态
    public static void receiveHealth(DataInputStream streamHandle)
    {
        try
        {
            //读取最大生命值
            SocketServer.monsterMaxHealth = streamHandle.readInt();
            SocketServer.monsterCurrentHealth = streamHandle.readInt();
            //收取角色类型
            String charType = streamHandle.readUTF();
            SocketServer.monsterChar = AbstractPlayer.PlayerClass.valueOf(charType);
            //接收尾巴的数量
            SocketServer.tailNum = streamHandle.readInt();
            //接收靴子的数量
            SocketServer.bootNum = streamHandle.readInt();
            //接收外卡钳的信息
            SocketServer.hasCaliper = streamHandle.readInt();
            //接收瓶中精灵的数量
            SocketServer.fairyPotionNum = streamHandle.readInt();
            //接收对方的金钱信息
            SocketServer.oppositeGold = streamHandle.readInt();
            //接收对方的初始球位
            SocketServer.beginOrbNum = streamHandle.readInt();
            //接收每回合的能量
            SocketServer.masterEnergy = streamHandle.readInt();
            //接收对方的时间戮
            SocketServer.oppositeEnterTime = streamHandle.readLong();
            //设置对方已经到场
            SocketServer.oppositePlayerReady = true;
            //尝试退出等待状态，但这不一定真的就会退出
            CharacterSelectScreenPatches.TestUpdateFading.endWaitStage(false);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //发送玩家的健康状态的操作
    public static void sendHealth(DataOutputStream streamHandle)
    {
        try
        {
            streamHandle.writeInt(FightProtocol.SEND_HEALTH);
            //发送目前的最大生命上限
            streamHandle.writeInt(AbstractDungeon.player.maxHealth);
            streamHandle.writeInt(AbstractDungeon.player.currentHealth);
            //发送自己的角色类型
            streamHandle.writeUTF(AbstractDungeon.player.chosenClass.name());
            //发送自己的尾巴数量
            int tailCount = 0;
            //判断是不是有发条靴
            int hasBoot = 0;
            //是否有外卡钳的信息
            int hasCaliper=0;
            for(AbstractRelic eachRelic : AbstractDungeon.player.relics)
            {
                //判断是不是尾巴
                if(eachRelic.relicId.equals(LizardTail.ID))
                {
                    tailCount++;
                }
                else if(hasBoot==0 && eachRelic.relicId.equals(Boot.ID))
                {
                    hasBoot++;
                }
                else if(hasCaliper==0 && eachRelic.relicId.equals(Calipers.ID))
                {
                    hasCaliper++;
                }
            }
            //发送尾巴的数量
            streamHandle.writeInt(tailCount);
            //初始化自己的尾巴数量
            SocketServer.myTailNum = tailCount;
            //发送是否有靴子
            streamHandle.writeInt(hasBoot);
            //发送外卡钳的信息
            streamHandle.writeInt(hasCaliper);
            int fairyPotionNum = 0;
            //获取瓶中精灵的数量
            for(AbstractPotion eachPotion : AbstractDungeon.player.potions)
            {
                //判断是不是瓶中精灵
                if(eachPotion instanceof FairyPotion)
                {
                    fairyPotionNum++;
                }
            }
            //发送瓶中精灵的数量
            streamHandle.writeInt(fairyPotionNum);
            //发送金钱的信息
            streamHandle.writeInt(AbstractDungeon.player.gold);
            //发送自己的初始球数
            streamHandle.writeInt(AbstractDungeon.player.masterMaxOrbs);
            //发送自己的每回合能量
            streamHandle.writeInt(AbstractDungeon.player.energy.energyMaster);
            //发送我方的日期信息
            streamHandle.writeLong(SocketServer.myEnterTime);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //打钱手的操作 它本身不会调用damageAction 所以需要手动调用一下
    @SpirePatch(clz = GreedAction.class, method = SpirePatch.CONSTRUCTOR)
    public static class HandGreedSend
    {
        @SpirePostfixPatch
        public static void fix(GreedAction __instance,
           AbstractCreature target, DamageInfo info, int goldAmount)
        {
            //新建一个damageAction,产生伤害的时候自然会触发伤害信息
            new DamageAction(target,info);
        }
    }

    //对跳过敌人回合操作的解码
    public static void jumpEnemyTurnDecode()
    {
        //调用敌人的回合开始操作 主要是去除它的格挡
        AbstractDungeon.getCurrRoom().monsters.applyPreTurnLogic();
    }

//    //发送腾跃信息，通知对面给敌人清除格挡
//    @SpirePatch(clz = SkipEnemiesTurnAction.class, method = SpirePatch.CONSTRUCTOR)
//    public static class JumpEnemyTurnSend
//    {
//        @SpirePostfixPatch
//        public static void fix(SkipEnemiesTurnAction __instance)
//        {
//            Communication.sendEvent(new JumpTurnEvent());
//        }
//    }

    //发条鞋的伤害判定
    @SpirePatch(clz = Boot.class, method = "onAttackToChangeDamage")
    public static class BootDamageSend
    {
        @SpirePrefixPatch
        public static void fix(Boot __instance,DamageInfo info, int damageAmount)
        {
            if (info.owner != null && info.type != DamageInfo.DamageType.HP_LOSS && info.type != DamageInfo.DamageType.THORNS && damageAmount > 0 && damageAmount < 5) {
                //这说明伤害会产生一个增量，所以在这里补充这个增量
                new DamageAction(ControlMoster.instance,
                        new DamageInfo(AbstractDungeon.player,5-damageAmount));
            }
        }
    }

    //对治疗信息的解码
    public static void healDecode(DataInputStream streamHandle)
    {
        System.out.println("receive heal decode");
        //获取解码信息
        try
        {
            int healAmount = streamHandle.readInt();
            //调用回血
            if(ControlMoster.instance!=null)
                ControlMoster.instance.heal(healAmount);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    //对治疗信息的编码
    public static void healEncode(DataOutputStream streamHandle,int healAmount)
    {
        System.out.println("send heal encode");
        try
        {
            //发送编码
            streamHandle.writeInt(FightProtocol.HEAL_INFO);
            //发送回血数量
            streamHandle.writeInt(healAmount);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //发生治疗时的处理
    @SpirePatch(clz = AbstractCreature.class, method = "heal",
        paramtypez = {int.class, boolean.class})
    public static class HealEventSend
    {

        //为true的时候会禁止发送这个东西
        public static boolean disableSend = true;

        @SpirePostfixPatch
        public static void fix(AbstractCreature __instance,int healAmount,
               boolean showEffect)
        {
            //如果还没有连接正常是不用处理的
            if(disableSend)
            {
                return;
            }
            //发送治疗事件 目前只处理对玩家的治疗信息
            if(__instance instanceof AbstractPlayer)
            {
                Communication.sendEvent(new HealEvent(healAmount));
            }
        }

    }

    //重生信息的解码
    public static void rebirthDecode(DataInputStream streamHandle)
    {
        try
        {
            //读取新的生命状态
            int newHealth = streamHandle.readInt();
            //强行修改敌人的状态
            ControlMoster.instance.currentHealth = newHealth;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //允许斩杀的信息
//    public static void sendMakeItDie(DataOutputStream streamHandle)
//    {
//        //通知对方对敌人进行斩杀，并不需要做什么别的
//        try
//        {
//            System.out.println("send make it die");
//            streamHandle.writeInt(FightProtocol.MAKE_IT_DIE);
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//    }


    //对血量更新的编码
    //当发生斩杀事件的时候会使用这个，告诉对面这边使用了瓶中精灵，需要复活
    public static void reBirthInfoSend(DataOutputStream streamHandle)
    {
        //告诉对方准备重生
        try
        {
            streamHandle.writeInt(FightProtocol.REBIRTH_INFO);
            //发送玩家目前的血量
            streamHandle.writeInt(AbstractDungeon.player.currentHealth);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //对瓶中精灵效果的截取,每当使用瓶中精灵的时候，就会过来把这个地方标记一下
    @SpirePatch(clz = FairyPotion.class, method = "use")
    public static class FairyPotionRecord
    {

        //近期使用瓶中精灵的标记，因为不确定斩杀信息和瓶中精灵的使用信息哪个会先到
        //所以两种逻辑都需要做一下
        public static boolean usedFlag = false;
        //是否已经触发了斩杀事件，如果是已经触发了斩杀事件在等待瓶中精灵，那就从这里发送同步血量信息
        public static boolean waitSuicide = false;

        //触发瓶中精灵的时候，禁用治疗信息的发送
        @SpirePrefixPatch
        public static void fix(FairyPotion __instance,AbstractCreature target)
        {
            //取消治疗信息的发送
            HealEventSend.disableSend = true;
        }

        //瓶中精灵触发结束后，再把允许发送健康状态的操作打开
        @SpirePostfixPatch
        public static void fidEnd(FairyPotion __instance,AbstractCreature target)
        {
            //恢复治疗信息的发送
            HealEventSend.disableSend = false;
        }
    }

    //执行斩杀信息的编码，如果发现这边的人死了，告诉对方处理死亡信息
//    @SpirePatch(clz = AbstractPlayer.class, method = "damage")
//    public static class DamageJudgeDie
//    {
//        @SpirePostfixPatch
//        public static void fix(AbstractPlayer __instance,DamageInfo info)
//        {
//            if(SocketServer.USE_NETWORK)
//            {
//                //判断打完之后是否死了
//                if(__instance.isDead)
//                {
//                    //发送执行斩杀信息
//                    AutomaticSocketServer server = AutomaticSocketServer.getServer();
//                    sendMakeItDie(server.streamHandle);
//                    server.send();
//                }
//            }
//        }
//    }

    //对怪物斩杀事件的解码
    public static void suicideDecode(DataInputStream streamHandle)
    {
        //判断是否已经使用了瓶中精灵
        if(FairyPotionRecord.usedFlag)
        {
            //通知对面这边没死，更新血量
            AutomaticSocketServer server = AutomaticSocketServer.getServer();
            reBirthInfoSend(server.streamHandle);
            server.send();
            //发送完成后关闭使用了瓶中精灵的记录
            FairyPotionRecord.usedFlag = false;
        }
        else if(AbstractDungeon.player.hasPotion(FairyPotion.POTION_ID))
        {
            //等待使用瓶中精灵，这说明还没有触发瓶中精灵
            FairyPotionRecord.waitSuicide = true;
        }
    }


    //把怪物的斩杀事件发送出去
    public static void suicideEncode(DataOutputStream streamHandle)
    {
        try
        {
            //发送基本的编码信息
            streamHandle.writeInt(FightProtocol.SUICIDE_INFO);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //当头棒喝的处理逻辑，它不是一般的damage,因此需要额外处理
    @SpirePatch(clz = WallopAction.class, method = "update")
    public static class WallopInfoSend
    {
        //后处理，如果处理完了再发送伤害信息
        @SpirePostfixPatch
        public static void fix(WallopAction __instance)
        {
            //判断是否处理完了
            if(__instance.isDone)
            {
                try
                {
                    //获取info信息
                    Field tempField = WallopAction.class.getDeclaredField("info");
                    //把info设置成可用
                    tempField.setAccessible(true);
                    //发送伤害信息 构造的时候它会自己发送
                    DamageAction tempAction = new DamageAction(__instance.target,
                        (DamageInfo) tempField.get(__instance),__instance.attackEffect);
                }
                catch (NoSuchFieldException | IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            }
        }

    }

    //对审判的编码信息
    //cutoff是截断的值，低于这个生命会被斩杀
    public static void judgementEncode(DataOutputStream streamHandle,
            AbstractCreature creature,
           int cutoff)
    {
        //发送截断值
        try
        {
            //发送审判的数据头
            streamHandle.writeInt(FightProtocol.JUDGEMENT);
            //发送目标的编码信息
            creatureEncode(streamHandle,creature,true);
            streamHandle.writeInt(cutoff);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //强制斩杀
    public static void instantKill(AbstractCreature creature)
    {
        //如果目标已经死了就不用管了
        if(creature.isDying || creature.isDead || disableCombatTrigger)
            return;
        creature.currentHealth = 0;
        creature.currentBlock = 0;
        creature.healthBarUpdatedEvent();
        //临时禁止buff的触发
        ArrayList<AbstractPower> powerList = creature.powers;
        creature.powers = new ArrayList<>();
        //给0点伤害触发死亡
        //不发送这次伤害信息
        stopSendAttack = true;
        creature.damage(new DamageInfo((AbstractCreature)null, 1, DamageInfo.DamageType.HP_LOSS));
        stopSendAttack = false;
        //伤害处理完了再把power送回去
        creature.powers = powerList;
    }

    //审判信息的解码
    public static void judgementDecode(DataInputStream streamHandle)
    {
        //读取作用的目标
        try
        {
            AbstractCreature target = creatureDecode(streamHandle,false);
            //写入截断值
            int cutoff = streamHandle.readInt();
            if(target==null)
                return;
            //判断是否可以杀
            if(target.currentHealth <= cutoff)
            {
                instantKill(target);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }



    //审判操作
//    @SpirePatch(clz = JudgementAction.class, method = SpirePatch.CONSTRUCTOR)
//    public static class JudgementActionSend
//    {
//        //生成时的处理操作
//        @SpirePostfixPatch
//        public static void fix(JudgementAction __instance,
//           AbstractCreature target, int cutoff)
//        {
//            //判断是否需要发送
//            if(SocketServer.USE_NETWORK && FightProtocol.endReadFlag)
//            {
//                //发送消息
//                AutomaticSocketServer server = AutomaticSocketServer.getServer();
//                judgementEncode(server.streamHandle,target,cutoff);
//                server.send();
//            }
//        }
//    }

    //发送移除所有debuff的信息
    //接收时的函数就是通知controlMonster移除所有的debuff
    public static void sendRemoveDebuff(DataOutputStream streamHandle)
    {
        //发送移除所有debuff的基本编码
        try
        {
            streamHandle.writeInt(FightProtocol.REMOVE_DEBUFF);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    //移除所有debuff的操作
    @SpirePatch(clz = RemoveDebuffsAction.class,method = "update")
    public static class RemoveAllDebuffSend
    {
        //触发这个action的时候，准备移除所有的debuff
        @SpirePrefixPatch
        public static void fix(RemoveDebuffsAction __instance)
        {
            //不使用网络的情况下不需要操作
            if(!SocketServer.USE_NETWORK)
            {
                return;
            }
            //告诉对面，移除敌人的所有debuff
            AutomaticSocketServer server = AutomaticSocketServer.getServer();
            sendRemoveDebuff(server.streamHandle);
            server.send();
        }
    }

    //告诉对面已经处理完了一个针对静电释放的伤害
//    public static void dischargeDamageEncode(DataOutputStream streamHandle)
//    {
//        try
//        {
//            streamHandle.writeInt(FightProtocol.END_DAMAGE_DISCHARGE);
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//    }

    public static void instantKillEncode(DataOutputStream streamHandle,
         AbstractCreature creature)
    {
        //发送斩杀的数据标头信息
        try
        {
            streamHandle.writeInt(FightProtocol.INSTANT_KILL);
            //对creature信息做反射编码
            creatureEncode(streamHandle,creature,true);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //对斩杀信息做解码
    public static void instantKillDecode(DataInputStream streamHandle)
    {
        //对目标信息做解码
        AbstractCreature creature = creatureDecode(streamHandle,false);
        if(creature==null)
        {
            return;
        }
        //对目标进行斩杀
        creature.currentHealth = 0;
        creature.healthBarUpdatedEvent();
        DamageInfo tempInfo = new DamageInfo((AbstractCreature)null, 1, DamageInfo.DamageType.NORMAL);
        tempInfo.output=1;
        creature.damage(tempInfo);
    }



    //对斩杀信息的编码，主要是审判操作，当触发审判操作的时候告诉对面执行相应的操作
    @SpirePatch(clz = InstantKillAction.class,method = "update")
    public static class InstantKillSendPatch
    {
        @SpirePrefixPatch
        public static void fix(InstantKillAction __instance)
        {
            //判断是不是使用网络的状态，如果不使用网络就不用处理了
            if(!SocketServer.USE_NETWORK)
            {
                return;
            }
            AutomaticSocketServer server = AutomaticSocketServer.getServer();
            //对斩杀信息做编码
            instantKillEncode(server.streamHandle,__instance.target);
            server.send();
        }
    }

    //发送移除power的消息
    public static void removePowerEncode(DataOutputStream streamHandle,
         AbstractCreature target,
         AbstractCreature source,String powerToRemove)
    {
        try
        {
            //发送移除power的数据头
            streamHandle.writeInt(FightProtocol.REMOVE_POWER);
            //对目标和接收者做编码
            creatureEncode(streamHandle,target,true);
            creatureEncode(streamHandle,source,true);
            //发送要消除的power
            streamHandle.writeUTF(powerToRemove);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //对移除power信息的解码
    public static void removePowerDecode(DataInputStream streamHandle)
    {
        try
        {
            //读取发送者和接收者
            AbstractCreature target = creatureDecode(streamHandle,false);
            AbstractCreature source = creatureDecode(streamHandle,false);
            //读取要移除的power
            String powerToRemove = streamHandle.readUTF();
            //需要确保接收者和发送者是有效的
            if(target==null || source==null)
                return;
            //临时改成禁用发送
            RemovePowerInfoSend.stopTrigger = true;
            AbstractDungeon.actionManager.addToBottom(
                new RemoveSpecificPowerAction(target,source,powerToRemove)
            );
            RemovePowerInfoSend.stopTrigger = false;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //移除power的信息的处理
    @SpirePatch(clz = RemoveSpecificPowerAction.class,method = SpirePatch.CONSTRUCTOR,
        paramtypez = {AbstractCreature.class,AbstractCreature.class,String.class})
    public static class RemovePowerInfoSend
    {
        //禁用触发
        public static boolean stopTrigger = false;

        //在构造的时候告诉对面也构造一个一样的
        @SpirePostfixPatch
        public static void fix(RemoveSpecificPowerAction __instance,
           AbstractCreature target, AbstractCreature source, String powerToRemove)
        {
            //如果不使用网络就不用测试了
            if(!SocketServer.USE_NETWORK || stopTrigger)
            {
                return;
            }
            SocketServer server = AutomaticSocketServer.getServer();
            //对于要移除的power进行编码
            removePowerEncode(server.streamHandle,
                target,source,powerToRemove);
            server.send();
        }
    }

    //减少power的信息发送
    public static void reducePowerEncode(DataOutputStream streamHandle,
         AbstractCreature target,AbstractCreature source,int reduceAmount,
         String powerName)
    {

        try
        {
            //发送减少buff层数时的数据头
            streamHandle.writeInt(FightProtocol.REDUCE_POWER);
            //发送对buff操作者和接收者
            creatureEncode(streamHandle,target,true);
            creatureEncode(streamHandle,source,true);
            //发送power的名称
            streamHandle.writeUTF(powerName);
            //发送减少的层数
            streamHandle.writeInt(reduceAmount);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //对减少信息发送的解码操作
    public static void reducePowerDecode(DataInputStream streamHandle)
    {
        try
        {
            //读取操作层
            AbstractCreature target = creatureDecode(streamHandle,false);
            AbstractCreature source = creatureDecode(streamHandle,false);
            //读取power的名称
            String powerName = streamHandle.readUTF();
            //读取减少的层数
            int reduceAmount = streamHandle.readInt();
            //需要确保读取到的数据是有效的
            if(target==null || source==null)
                return;
            //调用实际减少的层数
            ReducePowerInfoSend.stopTrigger = true;
            AbstractDungeon.actionManager.addToBottom(
                new ReducePowerAction(target,source,powerName,reduceAmount)
            );
            ReducePowerInfoSend.stopTrigger = false;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //减少power的信息处理
    @SpirePatch(clz = ReducePowerAction.class,method = SpirePatch.CONSTRUCTOR,
        paramtypez = {AbstractCreature.class,AbstractCreature.class,String.class,int.class})
    public static class ReducePowerInfoSend
    {

        //禁用触发的标志
        public static boolean stopTrigger = false;

        //构造时的处理，构造的时候告诉对面也做相同的操作
        @SpirePostfixPatch
        public static void fix(ReducePowerAction __instance,
            AbstractCreature target, AbstractCreature source,
           String power, int amount){
            //如果是特殊传递的power,那不需要手动处理它的减少层数的问题
            if(!SocketServer.USE_NETWORK || stopTrigger ||
                PowerMapping.creatorMapper.containsKey(power))
            {
                return;
            }
            SocketServer server = AutomaticSocketServer.getServer();
            reducePowerEncode(server.streamHandle,target,source,
                    amount,power);
            server.send();
        }
    }


}
