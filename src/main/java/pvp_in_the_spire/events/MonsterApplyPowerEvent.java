package pvp_in_the_spire.events;

import pvp_in_the_spire.pvp_api.BaseEvent;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

//当敌方玩家受到buff施加时处理的操作
public class MonsterApplyPowerEvent extends BaseEvent {

    public MonsterApplyPowerEvent(AbstractMonster targetMonster,
      AbstractPower power,
      AbstractCreature source,
      int amount)
    {

    }

}
