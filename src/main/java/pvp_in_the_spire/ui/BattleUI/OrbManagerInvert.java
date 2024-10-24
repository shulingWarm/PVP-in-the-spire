package pvp_in_the_spire.ui.BattleUI;

import com.badlogic.gdx.math.MathUtils;

//反向的球位管理器，这个东西是和我方玩家一致的
//父类是敌方单位用的
public class OrbManagerInvert extends OrbManager {

    @Override
    float getOrbTx(float dist, float angle, float drawX) {
        return dist * MathUtils.cosDeg(angle) + drawX;
    }
}
