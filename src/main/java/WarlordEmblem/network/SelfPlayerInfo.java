package WarlordEmblem.network;

import WarlordEmblem.GlobalManager;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

//这是用来管理本地的PlayerInfo的
public class SelfPlayerInfo extends PlayerInfo {

    public SelfPlayerInfo()
    {
        super(GlobalManager.myPlayerTag);
        //设置玩家的信息
        setCharacterInfo(GlobalManager.myName,GlobalManager.VERSION,
                AbstractPlayer.PlayerClass.IRONCLAD);
    }

}
