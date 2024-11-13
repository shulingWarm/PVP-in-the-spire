package pvp_in_the_spire.ui;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.helpers.ImageMaster;

//各种纹理的管理器，里面会涉及一些自制的纹理
public class TextureManager {

    public static boolean initFlag = false;

    //普通的按钮
    public static Texture NORMAL_BUTTON;
    public static Texture BACK_BUTTON;
    //准备标志的图片
    public static Texture READY_TEXTURE;
    //聊天界面里面用的发送按钮
    public static Texture SEND_BUTTON;
    //聊天消息的图标
    public static Texture MESSAGE_BUTTON;
    //关闭按钮
    public static Texture CLOSE_BUTTON;
    //角色的边框
    public static Texture SIDE_BOX;

    //以下是遗物里面会用到的
    public static Texture BLOCK_GAIN;
    public static Texture RED_PELLETS;

    public static Texture PELLETS_BUFF_128;
    public static Texture PELLETS_BUFF_48;

    public static void initTexture()
    {
        if(initFlag)
            return;
        NORMAL_BUTTON = ImageMaster.loadImage("images/ui/normal.png");
        BACK_BUTTON = ImageMaster.loadImage("images/ui/return.png");
        READY_TEXTURE = ImageMaster.loadImage("images/ui/ready.png");
        SEND_BUTTON = ImageMaster.loadImage("images/ui/sendButton.png");
        MESSAGE_BUTTON = ImageMaster.loadImage("images/ui/message.png");
        CLOSE_BUTTON = ImageMaster.loadImage("images/ui/close.png");
        SIDE_BOX = ImageMaster.loadImage("images/ui/SideBox.png");

        BLOCK_GAIN = new Texture("images/relics/blockGainer.png");
        RED_PELLETS = new Texture("images/relics/OrangePelletsChange.png");
        //药丸的buff图片
        PELLETS_BUFF_48 = new Texture("images/powers/Pellets48.png");
        PELLETS_BUFF_128 = new Texture("images/powers/Pellets128.png");

        initFlag = true;
    }

}
