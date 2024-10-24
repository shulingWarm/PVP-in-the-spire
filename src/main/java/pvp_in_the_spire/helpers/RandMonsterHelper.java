package pvp_in_the_spire.helpers;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.city.BanditBear;
import com.megacrit.cardcrawl.monsters.city.Champ;
import com.megacrit.cardcrawl.monsters.city.ShelledParasite;
import com.megacrit.cardcrawl.monsters.city.SnakePlant;
import com.megacrit.cardcrawl.monsters.exordium.*;

import java.util.ArrayList;

//生成随机敌人的工具
public class RandMonsterHelper {

    //随机敌人的列表
    public static ArrayList<String> monsterList = new ArrayList<>();

    //初始化随机敌人的列表
    public static void initMonsterList()
    {
        if(!monsterList.isEmpty())
            return;
        monsterList.add(Cultist.class.getName());//kaka
        monsterList.add(JawWorm.class.getName());//大颚虫
        monsterList.add(Lagavulin.class.getName());//乐加
        monsterList.add(Sentry.class.getName());//三柱神的一个
        monsterList.add(ShelledParasite.class.getName());//壳爹
        monsterList.add(BanditBear.class.getName());//熊
        monsterList.add(GremlinNob.class.getName());//猛男
        monsterList.add(SnakePlant.class.getName());//蛇花
        monsterList.add(Champ.class.getName());//弟勇
    }

    //获取一个随机敌人
    public static AbstractMonster getRandMonster()
    {
        AbstractMonster monster;
        while (true)
        {
            //生成一个随机的id
            int idMonster = AbstractDungeon.cardRng.random(monsterList.size()-1);
            //根据monster的名字得到monster
            monster = ClassNameHelper.createMonster(monsterList.get(idMonster));
            //如果得到的是正常的结果，就直接返回
            if(monster != null)
                return monster;
        }
    }


}
