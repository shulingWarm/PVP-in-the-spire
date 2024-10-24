package pvp_in_the_spire.ui;

import pvp_in_the_spire.character.CharacterInfo;
import pvp_in_the_spire.helpers.FieldHelper;
import pvp_in_the_spire.patches.AnimationRecorder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.esotericsoftware.spine.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;

//用于显示人物立绘的页面
public class CharacterBox extends CreatureBox {

    public TextureAtlas atlas;
    public Skeleton skeleton;
    public AnimationStateData stateData;
    public AnimationState state;

    public boolean flipHorizontal = false;
    public boolean flipVertical = false;
    //当前处理的角色信息
    //仅仅是用于渲染的
    public AbstractPlayer player;

    //判断是否需要每次加载人物时都重置大小
    public boolean resetScaleFlag = true;
    //需要重置情况下的人物大小
    public final float RESET_SCALE = 1.3f;

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

    //初始化player的动画状态，必须调用这个它才能正常动
    public static void initPlayerAnimation(AbstractPlayer player, CharacterInfo characterInfo)
    {
        if(player.state!=null)
        {
            try
            {
                player.state.setAnimation(0, "Idle", true);
                //读取骨架信息
                Skeleton tempSkeleton = characterInfo.getSkeleton();
                FieldHelper.setPrivateFieldValue(player,"eyeBone",
                        tempSkeleton.findBone("eye_anchor"));
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

    //更新当前显示的角色
    public void updateCharacter(CharacterInfo newCharacter)
    {
        //记录角色的player
        this.player = newCharacter.player;
        this.player.drawX = this.x;
        this.player.drawY = this.y;
        if(this.resetScaleFlag)
            AnimationRecorder.resetCreatureScale(this.player,RESET_SCALE);
        initPlayerAnimation(this.player,newCharacter);
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

    public CharacterBox(float x, float y, CharacterInfo characterInfo)
    {
        this(x,y,characterInfo,true);
    }


    //通过角色信息做的初始化
    public CharacterBox(float x, float y, CharacterInfo characterInfo,boolean resetScale)
    {
        this.x = x;
        this.y = y;
        //从角色信息里面载入动画
        //loadAnimationFromCharacter(characterInfo);
        this.player = characterInfo.player;
        this.resetScaleFlag = resetScale;
        //重置player的大小
        if(resetScale)
            AnimationRecorder.resetCreatureScale(this.player,1.3f);
        this.player.drawX = this.x;
        this.player.drawY = this.y;
        initPlayerAnimation(this.player,characterInfo);

    }

    public void setFlipHorizontal(boolean flipHorizontal) {
        this.flipHorizontal = flipHorizontal;
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

    @Override
    public void move(float xChange, float yChange) {
        super.move(xChange,yChange);
        if(player != null)
        {
            player.drawX += xChange;
            player.drawY += yChange;
        }

    }

    //获取要渲染的角色的位置
    @Override
    public float[] getLocation() {
        return new float[]{this.player.drawX,this.player.drawY};
    }

    @Override
    public AbstractCreature getCreature() {
        return player;
    }
}
