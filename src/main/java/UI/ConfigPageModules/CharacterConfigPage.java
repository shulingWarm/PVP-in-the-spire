package UI.ConfigPageModules;

import UI.*;
import WarlordEmblem.character.CharacterInfo;
import WarlordEmblem.helpers.FontLibrary;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.Defect;
import com.megacrit.cardcrawl.core.Settings;

//配置界面里面用于显示人物角色的地方
public class CharacterConfigPage extends AbstractPage {

    public CharacterBox characterBox;

    //玩家名
    public TextLabel nameLabel;

    //用于显示角色框的背景，后续会把这个东西弄成一个像框
    public PlainBox plainBox;

    //角色的准备图标
    public PlainBox readyLabel;

    //玩家当前的准备状态
    public boolean isReady = false;

    //官方指定的宽和高
    public static final float WIDTH = Settings.WIDTH * 0.11f;
    public static final float HEIGHT = Settings.HEIGHT * 0.25f;

    //版本号
    VersionText versionText;

    //初始化测试用的画面
    //正常运行的时候是不走这个的
    public void initTest()
    {
        this.y = Settings.HEIGHT * 0.5f;
        this.width = WIDTH;
        this.x = (Settings.WIDTH - this.width)/2;
        this.height = HEIGHT;
        this.characterBox = new CharacterBox(
                this.x + this.width * 0.5f,
                this.y + this.height * 0.2f, new CharacterInfo(AbstractPlayer.PlayerClass.DEFECT)
        );
        //背景图
        this.plainBox = new PlainBox(
                this.width,this.height, Color.RED
        );
        this.plainBox.texture = TextureManager.SIDE_BOX;
        this.plainBox.x = this.x;
        this.plainBox.y = this.y;
        //初始化玩家名
        this.nameLabel = new TextLabel(this.x,this.y + this.height*0.9f,
                this.width,Settings.HEIGHT*0.01f,
                "user", FontLibrary.getBaseFont());
        //更新准备图标
        this.readyLabel = new PlainBox(this.width * 0.4f,
                this.height*0.15f,Color.WHITE);
        this.readyLabel.texture = TextureManager.READY_TEXTURE;
        this.readyLabel.x = this.x + this.width * 0.5f - readyLabel.width * 0.5f;
        this.readyLabel.y = this.y + this.readyLabel.height * 0.5f;
        //初始化版本信息
        this.versionText = new VersionText(this.nameLabel.x,this.readyLabel.y,FontLibrary.getBaseFont());
        this.versionText.width = this.width;
        versionText.text = "v0.4.12";
        this.isReady = true;
    }

    //仅仅用于实验测试的页面
    public CharacterConfigPage()
    {
        //页面的基本位置
        this.y = Settings.HEIGHT * 0.5f;
        this.width = WIDTH;
        this.x = (Settings.WIDTH - this.width)/2;
        this.height = HEIGHT;
        //背景图
        this.plainBox = new PlainBox(
                this.width,this.height, Color.RED
        );
        this.plainBox.texture = TextureManager.SIDE_BOX;
        this.plainBox.x = this.x;
        this.plainBox.y = this.y;
        //更新准备图标
        this.readyLabel = new PlainBox(this.width * 0.4f,
                this.height*0.15f,Color.WHITE);
        this.readyLabel.texture = TextureManager.READY_TEXTURE;
        this.readyLabel.x = this.x + this.width * 0.5f - readyLabel.width * 0.5f;
        this.readyLabel.y = this.y + this.readyLabel.height * 0.5f;
    }

    //设置玩家的信息
    public void setPlayerInfo(CharacterInfo characterInfo,
                              String name,
                              String version)
    {
        if(characterBox != null)
        {
            characterBox.updateCharacter(characterInfo);
            this.nameLabel = new TextLabel(this.x,this.y + this.height*0.9f,
                    this.width,Settings.HEIGHT*0.01f,
                    name, FontLibrary.getBaseFont());
            this.versionText = new VersionText(this.nameLabel.x,this.readyLabel.y,FontLibrary.getBaseFont());
            this.versionText.width = this.width;
        }
        else {
            characterBox = new CharacterBox(this.x + this.width * 0.5f,
                    this.y + this.height * 0.2f,characterInfo);
            this.nameLabel.text = name;
        }
        this.versionText.text = version;
    }

    //获取名字
    public String getName()
    {
        return nameLabel.text;
    }

    public String getVersion()
    {
        return versionText.text;
    }

    //把角色设置成横向翻转
    public void setHorizontalFlip(boolean newFlag)
    {
        this.characterBox.setFlipHorizontal(newFlag);
    }

    @Override
    public void move(float xChange, float yChange) {
        super.move(xChange,yChange);
        plainBox.move(xChange,yChange);
        readyLabel.move(xChange,yChange);
        //这个东西刚开始的时候可能没有被初始化
        if(characterBox != null)
        {
            characterBox.move(xChange,yChange);
            nameLabel.move(xChange,yChange);
            this.versionText.move(xChange,yChange);
        }
    }

    //更换角色
    public void updateCharacter(CharacterInfo characterInfo)
    {
        this.characterBox = new CharacterBox(
                this.x + this.width * 0.5f,
                this.y + this.height * 0.2f, characterInfo
        );
    }

    //设置准备状态
    public void setReady(boolean newStage)
    {
        this.isReady = newStage;
    }

    //获取准备状态
    public boolean getReadyFlag(){
        return isReady;
    }

    @Override
    public void update() {
        plainBox.update();
        if(characterBox != null)
        {
            characterBox.update();
            this.nameLabel.update();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        plainBox.render(sb);
        if(characterBox!=null)
        {
            characterBox.render(sb);
            this.nameLabel.render(sb);
            if(this.isReady)
                this.readyLabel.render(sb);
            else
                this.versionText.render(sb);
        }
    }
}
