package pvp_in_the_spire.actions.actionList;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;

public class AttackActList extends ActListGeneratorBase{
    public ArrayList<AbstractGameAction> getActList(AbstractCreature actSource, int baseValue)
    {
        //用于存储action信息的列表
        ArrayList<AbstractGameAction> actList = new ArrayList<AbstractGameAction>();
        //轻微移动的动画
        actList.add(new AnimateSlowAttackAction(actSource));
        //打击的信息
        DamageInfo tempInfo = new DamageInfo(actSource,baseValue);
        //给打击信息叠加力量值
        tempInfo.applyPowers(actSource, AbstractDungeon.player);
        //在动作列表里面添加伤害值
        actList.add(new DamageAction(AbstractDungeon.player,tempInfo,AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
        //返回伤害信息
        return actList;
    }
}
