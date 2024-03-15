package UI;

import WarlordEmblem.character.CharacterInfo;
import WarlordEmblem.character.ControlMoster;
import WarlordEmblem.character.PlayerForShow;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.esotericsoftware.spine.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;

//用于显示人物立绘的页面
public class CharacterBox extends AbstractPage {

    public TextureAtlas atlas;
    public Skeleton skeleton;
    public AnimationStateData stateData;
    public AnimationState state;

    public boolean flipHorizontal = false;
    public boolean flipVertical = false;
    //当前处理的角色信息
    //仅仅是用于渲染的
    public AbstractPlayer player;

    //载入人物的原画
    public void loadAnimation(String atlasUrl, String skeletonUrl, float scale) {
        this.atlas = new TextureAtlas(Gdx.files.internal(atlasUrl));
        SkeletonJson json = new SkeletonJson(this.atlas);

        json.setScale(Settings.renderScale / scale);
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal(skeletonUrl));
        this.skeleton = new Skeleton(skeletonData);
        this.skeleton.setColor(Color.WHITE);
        this.stateData = new AnimationStateData(skeletonData);
        this.state = new AnimationState(this.stateData);
        this.state.setAnimation(0, "Idle", true);
    }

    //从角色信息里面载入动画
    public void loadAnimationFromCharacter(CharacterInfo characterInfo)
    {
        this.atlas = characterInfo.getAtlas();
        //初始化骨架
        this.skeleton = characterInfo.getSkeleton();
        this.stateData = characterInfo.getStateData();
        //初始化动画状态
        this.state = characterInfo.getState();
    }

    //通过角色种类载入贴图
    public void loadAnimation(AbstractPlayer.PlayerClass playerClass)
    {
        switch (playerClass)
        {
            case THE_SILENT:
                this.loadAnimation("images/characters/theSilent/idle/skeleton.atlas", "images/characters/theSilent/idle/skeleton.json", 1.0F);
                break;
            case WATCHER:
                this.loadAnimation("images/characters/watcher/idle/skeleton.atlas", "images/characters/watcher/idle/skeleton.json", 1.0F);
                break;
            case DEFECT:
                this.loadAnimation("images/characters/defect/idle/skeleton.atlas", "images/characters/defect/idle/skeleton.json", 1.0F);
                break;
            default:
                this.loadAnimation("images/characters/ironclad/idle/skeleton.atlas", "images/characters/ironclad/idle/skeleton.json", 1.0F);
        }
    }

    public CharacterBox(float x, float y, AbstractPlayer.PlayerClass selectedCharacter)
    {
        this.x = x;
        this.y = y;
        //默认载入观者的原画，后面再改成传入的形式
        loadAnimation(selectedCharacter);
        //确认一下creature那里有没有初始化过sr
        if(AbstractCreature.sr==null)
        {
            AbstractCreature.initialize();
        }
    }

    //通过角色信息做的初始化
    public CharacterBox(float x, float y, CharacterInfo characterInfo)
    {
        this.x = x;
        this.y = y;
        //从角色信息里面载入动画
        //loadAnimationFromCharacter(characterInfo);
        this.player = characterInfo.player;
        this.player.drawX = this.x;
        this.player.drawY = this.y;

        if(this.player.state!=null)
        {
            try
            {
                this.player.state.setAnimation(0, "Idle", true);
            }
            catch (IllegalArgumentException e)
            {
                e.printStackTrace();
            }
        }
        if(AbstractCreature.sr==null)
        {
            AbstractCreature.initialize();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        if(this.player!=null)
        {
            this.player.flipHorizontal = this.flipHorizontal;
            this.player.flipVertical = this.flipVertical;
            this.player.renderPlayerImage(sb);
            return;
        }
        float deltaTime = Gdx.graphics.getDeltaTime();
        this.state.update(deltaTime);
        this.state.apply(this.skeleton);
        this.skeleton.updateWorldTransform();
        this.skeleton.setPosition(x, y);
        this.skeleton.setColor(Color.WHITE);
        this.skeleton.setFlip(this.flipHorizontal, this.flipVertical);
        sb.end();
        CardCrawlGame.psb.begin();
        AbstractCreature.sr.draw(CardCrawlGame.psb, this.skeleton);
        CardCrawlGame.psb.end();
        sb.begin();
    }
}
