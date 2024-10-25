package pvp_in_the_spire.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;

import java.util.ArrayList;
import java.util.HashMap;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.powers.watcher.EnergyDownPower;
import com.megacrit.cardcrawl.powers.watcher.MantraPower;
import pvp_in_the_spire.pvp_api.PowerCreate;

//所有power的映射
public class PowerMapping {

    public static class Vulnerable extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new VulnerablePower(owner,amount,true);
        }
    }

    public static class Weakened extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new WeakPower(owner,amount,true);
        }
    }

    public static class Strength extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new StrengthPower(owner,amount);
        }
    }

    //对毒效果的映射
    public static class Poison extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            //只有承受方是本体的情况下才使用真毒
            if(owner.isPlayer)
                return new BlockablePoisonPower(owner,owner,amount);
            return new FakePoisonPower(owner,owner,amount);
        }
    }

    //残影
    public static class Blur extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new BlurPower(owner,amount);
        }
    }

    //荆棘 但这里返回的是假的荆棘，只是显示个画面
    public static class Thorns extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new FakeThornsPower(owner,amount);
        }
    }


    //集中
    public static class Focus extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new FocusPower(owner,amount);
        }
    }

    //缓冲
    public static class Buffer extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new BufferPower(owner,amount);
        }
    }

    //机器人球的易伤，锁定
    public static class Lockon extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new LockOnPower(owner,amount);
        }
    }

    //人工制品
    public static class Artifact extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new ArtifactPower(owner,amount);
        }
    }

    //无实体
    public static class IntangiblePlayer extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            //给敌人的情况下使用敌人版本的
            return new IntangiblePlayerPower(owner,amount);
        }
    }

    //火焰屏障
    public static class FlameBarrier extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new FakeFlameBarrierPower(owner,amount);
        }
    }

    //无法出牌的buff,主要是为了让它正常抵消人工制品
    public static class NoDraw extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new NoDrawPower(owner);
        }
    }

    //幽魂power
    public static class WraithFormCreator extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new FakeWraithFormPower(owner,amount);
        }
    }

    //敏捷，最重要的敏捷
    public static class Dexterity extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new DexterityPower(owner,amount);
        }
    }

    //偏差认知的buff
    public static class BiasBlue extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new FakeBias(owner,amount);
        }
    }

    //斋戒的buff
    public static class FastingSend extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new EnergyDownPower(owner,amount,true);
        }
    }

    //无法获得格挡，应急按钮的debuff
    public static class NoBlock extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new NoBlockPower(owner,amount,isSourceMonster);
        }
    }

    //静电释放的buff
    //但解码的时候只生成假的power
    public static class StaticDischarge extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new FakeStaticDischarge(owner,amount);
        }
    }

    //壁垒的buff传输
    public static class BarricadeMapping extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new BarricadePower(owner);
        }
    }

    //炸弹信息的映射
    public static class BombMapping extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new FakeBombPower(owner,3,amount);
        }
    }

    //自燃buff的显示
    public static class CombustMapping extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new FakeCombust(owner,amount);
        }
    }

    //撕裂的buff
    public static class RuptureMapping extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new FakeRupture(owner,amount);
        }
    }

    //神格的buff修改
    public static class MantraMapping extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new FakeMantra(owner,amount);
        }
    }

    //时间吞噬信息的传递
    public static class TimeEatMapping extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new TimeEatPower(owner);
        }
    }

    //好奇的信息传递
    public static class CuriosPowerMapping extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new CuriosityPower(owner,amount);
        }
    }

    //邪咒信息的映射
    public static class HexPowerMapping extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new RealHexPower(owner,amount);
        }
    }

    //猛男
    public static class StrongManPowerMapping extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new AngerPower(owner,amount);
        }
    }

    //疼痛power的传输，自己显示假的，但对方那里显示的是真的
    public static class PainSwordPowerMapping extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new PainSwordPower(owner,amount);
        }
    }

    //失去能量的映射，自己这边显示假的，但对面附加的是真的
    public static class DropEnergyMapping extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new DropEnergy(owner,amount);
        }
    }

    //计算转移的power 显示计算转移
    public static class ComputeTransformMapping extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new ComputeTransformPower(owner);
        }
    }

    //命运转移
    public static class FateTransformPowerMapping extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new FateTransformPower(owner);
        }
    }

    //坚不可摧 这个直接上的是真buff
    public static class InvinciblePowerMapping extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new InvincibleAtStartPower(owner,amount);
        }
    }

    //天罚形态
    public static class GodPunishmentPowerMapping extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new FakeGodPunishment2(owner,amount);
        }
    }

    //机器学习的映射
    public static class DrawPowerMapping extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new FakeDrawPower(owner,amount);
        }
    }

    //对仪式的映射
    public static class RitualMapping extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new FakeRitual(owner,amount);
        }
    }

    //脆弱
    public static class FrailMapping extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new FrailPower(owner,amount,true);
        }
    }

    //对再生效果的映射
    public static class RegenMapping extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new FakeRegenPower(owner,amount);
        }
    }

    //缓存负面效果的buff
    public static class PelletsPowerMapping extends PowerCreate
    {
        @Override
        public AbstractPower make(AbstractCreature owner,int amount,boolean isSourceMonster) {
            return new PelletsPower(owner);
        }
    }

    //从ID到power生成方法的映射
    public static HashMap<String,PowerCreate> creatorMapper;

    //通信形式下的power mapping
    public static ArrayList<CommunicatePower> powerList;
    //通信相关的power从power到通信id的映射表
    public static HashMap<String, Integer> comPowerMap;

    public static void registerCommunicatePower(CommunicatePower power)
    {
        int idPower = powerList.size();
        comPowerMap.put(power.getMapId(),idPower);
        powerList.add(power);
    }

    public static CommunicatePower getComPower(int idPower)
    {
        initCreatorMapper();
        if(idPower >=0 && idPower<powerList.size())
            return powerList.get(idPower);
        return null;
    }

    public static int getPowerId(String powerId)
    {
        initCreatorMapper();
        if(comPowerMap.containsKey(powerId))
            return comPowerMap.get(powerId);
        return -1;
    }

    //初始化通信用的power
    public static void initCommunicatePowerMap()
    {
        //给两个power开辟空间
        powerList = new ArrayList<>();
        comPowerMap = new HashMap<>();
        //初始化从power到id到map
        registerCommunicatePower(new TimeWarpDebuff(null,0));
    }

    //生成power的mapper的初始化器
    public static void initCreatorMapper()
    {
        //如果已经处理过了就不用处理了
        if(creatorMapper != null)
        {
            return;
        }
        creatorMapper = new HashMap<String,PowerCreate>();
        //初始化通信用的map
        initCommunicatePowerMap();
        creatorMapper.put(VulnerablePower.POWER_ID,
                new Vulnerable());
        creatorMapper.put(WeakPower.POWER_ID,
                new Weakened());
        creatorMapper.put(StrengthPower.POWER_ID,
                new Strength());
        creatorMapper.put(PoisonPower.POWER_ID,
                new Poison());
        creatorMapper.put(BlurPower.POWER_ID,
                new Blur());
        creatorMapper.put(ThornsPower.POWER_ID,
                new Thorns());
        creatorMapper.put(IntangiblePlayerPower.POWER_ID,
                new IntangiblePlayer());
        creatorMapper.put(FocusPower.POWER_ID,
                new Focus());
        creatorMapper.put(BufferPower.POWER_ID,
                new Buffer());
        creatorMapper.put(LockOnPower.POWER_ID,
                new Lockon());
        creatorMapper.put(ArtifactPower.POWER_ID,
                new Artifact());
        creatorMapper.put(FlameBarrierPower.POWER_ID,
                new FlameBarrier());
        creatorMapper.put(NoDrawPower.POWER_ID,
                new NoDraw());
        creatorMapper.put(WraithFormPower.POWER_ID,
                new WraithFormCreator());
        creatorMapper.put(DexterityPower.POWER_ID,
                new Dexterity());
        creatorMapper.put(BiasPower.POWER_ID,
                new BiasBlue());
        creatorMapper.put(EnergyDownPower.POWER_ID,
                new FastingSend());
        creatorMapper.put(NoBlockPower.POWER_ID,
                new NoBlock());
        creatorMapper.put(StaticDischargePower.POWER_ID,
                new StaticDischarge());
        creatorMapper.put(BarricadePower.POWER_ID,
                new BarricadeMapping());
        creatorMapper.put(TheBombPower.POWER_ID,new BombMapping());
        //自燃
        creatorMapper.put(CombustPower.POWER_ID,new CombustMapping());
        //撕裂
        creatorMapper.put(RupturePower.POWER_ID,new RuptureMapping());
        //真言
        creatorMapper.put(MantraPower.POWER_ID,new MantraMapping());
        //时间吞噬
        creatorMapper.put(FakeTimePower.POWER_ID,new TimeEatMapping());
        //好奇
        creatorMapper.put(CuriosityPower.POWER_ID,new CuriosPowerMapping());
        //邪咒
        creatorMapper.put(HexPower.POWER_ID,new HexPowerMapping());
        //猛男buff
        creatorMapper.put(AngerPower.POWER_ID,new StrongManPowerMapping());
        //疼痛
        creatorMapper.put(PainSwordPower.POWER_ID,new PainSwordPowerMapping());
        //失去能量
        creatorMapper.put(DropEnergy.POWER_ID,new DropEnergyMapping());
        //计算转移
        creatorMapper.put(ComputeTransformPower.POWER_ID,new ComputeTransformMapping());
        //命运转移
        creatorMapper.put(FateTransformPower.POWER_ID,new FateTransformPowerMapping());
        //坚不可摧
        creatorMapper.put(InvinciblePower.POWER_ID,new InvinciblePowerMapping());
        //天罚形态
        creatorMapper.put(GodPunishmentPower.POWER_ID,new GodPunishmentPowerMapping());
        //机器学习
        creatorMapper.put(DrawPower.POWER_ID,new DrawPowerMapping());
        //仪式
        creatorMapper.put(RitualPower.POWER_ID,new RitualMapping());
        //脆弱
        creatorMapper.put(FrailPower.POWER_ID,new FrailMapping());
        //再生
        creatorMapper.put(RegenPower.POWER_ID,new RegenMapping());
        //药丸的buff
        creatorMapper.put(PelletsPower.POWER_ID,new PelletsPowerMapping());
    }

    //根据id生成具体的power对象
    public static AbstractPower getPowerById(String powerId,
         AbstractCreature owner,int amount,boolean isSourceMonster)
    {
        //确保映射表是初始化过的
        initCreatorMapper();
        //从id映射出对应的生成器
        PowerCreate creator = creatorMapper.get(powerId);
        if(creator==null)
            return null;
        return creator.make(owner,amount,isSourceMonster);
    }


}
