package pvp_in_the_spire.ui.Events;

import com.megacrit.cardcrawl.characters.AbstractPlayer;

//对方更新角色信息时的消息回调
public interface UpdateCharacter {

    public void updateCharacter(AbstractPlayer.PlayerClass playerClass, String versionInfo);

}
