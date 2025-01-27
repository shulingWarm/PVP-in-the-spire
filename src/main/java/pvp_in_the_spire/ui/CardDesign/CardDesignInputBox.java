package pvp_in_the_spire.ui.CardDesign;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.megacrit.cardcrawl.cards.AbstractCard;
import pvp_in_the_spire.ui.Events.CardDesignClickCallback;
import pvp_in_the_spire.ui.Events.CardModifyEvent;
import pvp_in_the_spire.ui.Events.InputBoxChange;
import pvp_in_the_spire.ui.InputBoxWithLabel;

//专门用于卡牌设计的回调函数
//当输入框里面的内容改变时会通知回调函数
public class CardDesignInputBox extends InputBoxWithLabel
    implements InputBoxChange {

    //负责管理的核心卡牌
    public AbstractCard mainCard;
    //卡牌被修改时产生的回调函数
    public CardModifyEvent modifyEvent;

    public CardDesignInputBox(float x,float y,float width,float height,
       String text, //显示在输入框旁边的文本
       BitmapFont font,
      AbstractCard card,
      CardModifyEvent cardModifyEvent
    )
    {
        super(x,y,width,height,text,font,true);
        this.mainCard = card;
        //记录卡牌修改时触发的回调
        this.modifyEvent = cardModifyEvent;
        //在输入框里面注册文本内容修改时的回调函数
        this.inputBox.registerInputBoxChange(this);
    }

    @Override
    public void onInputBoxChanged(String text) {
        //调用相应的卡牌修改回调
        if(this.modifyEvent != null)
        {
            this.modifyEvent.cardModifyTrigger(this.mainCard,text);
        }
    }
}
