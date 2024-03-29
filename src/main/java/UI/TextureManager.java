package UI;

import UI.Button.BackButton;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.helpers.ImageMaster;

//各种纹理的管理器，里面会涉及一些自制的纹理
public class TextureManager {

    public static boolean initFlag = false;

    //普通的按钮
    public static Texture NORMAL_BUTTON;
    public static Texture BACK_BUTTON;
    //准备标志的图片
    public static Texture READY_TEXTURE;

    public static void initTexture()
    {
        if(initFlag)
            return;
        NORMAL_BUTTON = ImageMaster.loadImage("pvp/ui/normal.png");
        BACK_BUTTON = ImageMaster.loadImage("pvp/ui/return.png");
        READY_TEXTURE = ImageMaster.loadImage("pvp/ui/ready.png");
        initFlag = true;
    }

}
