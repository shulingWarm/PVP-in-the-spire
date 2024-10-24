package pvp_in_the_spire.ui.Button;

import pvp_in_the_spire.ui.Steam.SteamFriendInfo;
import pvp_in_the_spire.screens.UserButton;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;

//用于处理steam好友逻辑的按钮
public class SteamFriendButton extends UserButton {

    public static final UIStrings uiStrings =
            CardCrawlGame.languagePack.getUIString("SteamFriendButton");

    public SteamFriendInfo friendInfo;

    //选中好友时的回调接口
    SteamFriendInterface callbackInterface;

    //处于等待连接状态的标志
    public boolean waitingFlag = false;

    public SteamFriendButton(SteamFriendInfo friendInfo,
         float x, float y, float height, float width, BitmapFont font,
         SteamFriendInterface callbackInterface)
    {
        super(x,y,width,height,friendInfo.friendName,font);
        //记录这个链接的好友信息
        this.friendInfo = friendInfo;
        //记录选中好友时的回调接口
        this.callbackInterface = callbackInterface;
    }

    //按钮的点击事件
    public void clickEvent()
    {
        //修改自己目前的状态
        this.waitingFlag = !this.waitingFlag;
        //调用回调函数
        callbackInterface.friendSelectTrigger(this.friendInfo);
    }

    //在渲染之前确定自己要渲染的文本
    public void render(SpriteBatch sb)
    {
        //判断是不是正在等待对方玩家进入
        if(this.waitingFlag)
        {
            //判断是不是鼠标正在上面
            if(this.isHovering())
            {
                this.text = uiStrings.TEXT[2];
            }
            else {
                this.text = uiStrings.TEXT[0] +
                    this.friendInfo.friendName + uiStrings.TEXT[1];
            }
            //默认的渲染颜色
            this.defaultColor = Color.GOLD;
        }
        else {
            //没有点击的情况下就显示好友的名字
            this.text = friendInfo.friendName;
            //正常情况下不使用特殊颜色
            this.defaultColor = Color.WHITE;
        }
        super.render(sb);
    }

}
