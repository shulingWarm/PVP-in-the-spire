package pvp_in_the_spire.character;

import basemod.abstracts.CustomPlayer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.Zap;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.ui.panels.energyorb.EnergyOrbBlue;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import java.util.ArrayList;

//仅仅用于展示人物的抽象player
public class PlayerForShow extends CustomPlayer {

    public PlayerForShow()
    {
        super("test", PlayerClass.WATCHER, new EnergyOrbBlue(),  (String) null, null);

    }

    public void loadTexture(String atlasUrl, String skeletonUrl, float scale)
    {
        this.loadAnimation(atlasUrl,skeletonUrl,scale);
    }

    @Override
    public ArrayList<String> getStartingDeck() {
        ArrayList<String> retVal = new ArrayList();
        retVal.add("Strike_B");
        retVal.add("Strike_B");
        retVal.add("Strike_B");
        retVal.add("Strike_B");
        retVal.add("Defend_B");
        retVal.add("Defend_B");
        retVal.add("Defend_B");
        retVal.add("Defend_B");
        retVal.add("Zap");
        retVal.add("Dualcast");
        return retVal;
    }

    @Override
    public ArrayList<String> getStartingRelics() {
        ArrayList<String> retVal = new ArrayList();
        retVal.add("Cracked Core");
        UnlockTracker.markRelicAsSeen("Cracked Core");
        return retVal;
    }

    @Override
    public CharSelectInfo getLoadout() {
        return new CharSelectInfo("show", "show", 75, 75, 3, 99, 5, this, this.getStartingRelics(), this.getStartingDeck(), false);
    }

    @Override
    public String getTitle(PlayerClass playerClass) {
        return "show class";
    }

    @Override
    public AbstractCard.CardColor getCardColor() {
        return AbstractCard.CardColor.BLUE;
    }

    @Override
    public Color getCardRenderColor() {
        return Color.WHITE;
    }

    @Override
    public AbstractCard getStartCardForEvent() {
        return new Zap();
    }

    @Override
    public Color getCardTrailColor() {
        return Color.SKY.cpy();
    }

    @Override
    public int getAscensionMaxHPLoss() {
        return 4;
    }

    @Override
    public BitmapFont getEnergyNumFont() {
        return FontHelper.energyNumFontBlue;
    }

    @Override
    public void doCharSelectScreenSelectEffect() {

    }

    @Override
    public String getCustomModeCharacterButtonSoundKey() {
        return "ATTACK_MAGIC_BEAM_SHORT";
    }

    @Override
    public String getLocalizedCharacterName() {
        return "show";
    }

    @Override
    public AbstractPlayer newInstance() {
        return new PlayerForShow();
    }

    @Override
    public String getSpireHeartText() {
        return "show getSpireHeartText";
    }

    @Override
    public Color getSlashAttackColor() {
        return Color.SKY;
    }

    @Override
    public AbstractGameAction.AttackEffect[] getSpireHeartSlashEffect() {
        return new AbstractGameAction.AttackEffect[0];
    }

    @Override
    public String getVampireText() {
        return "show getVampireText";
    }
}
