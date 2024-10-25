package pvp_in_the_spire.actions;

import pvp_in_the_spire.events.RemoveCardEvent;
import pvp_in_the_spire.pvp_api.Communication;
import pvp_in_the_spire.character.PlayerMonster;
import pvp_in_the_spire.patches.CardShowPatch.UseCardSend;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.CardRewardScreen;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

//心灵窥探的具体操作，弹出五张牌让玩家选
public class PsychicSnoopingAction extends AbstractGameAction {

    //是否已经选过了牌
    public boolean selectedFlag = false;

    //是否自动打出那张牌
    public boolean getCardFlag;
    public boolean noCostFlag;
    //准备偷的目标牌
    public PlayerMonster targetMonster;

    public PsychicSnoopingAction(PlayerMonster targetMonster, boolean getCard, boolean noCostGet)
    {
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FAST;
        this.getCardFlag = getCard;
        this.noCostFlag = noCostGet;
        this.targetMonster = targetMonster;
    }

    //消耗某张牌的指令编码
    public static void exhaustCardEncode(DataOutputStream streamHandle,
         int idCard)
    {
        //发送这张牌的数据头
        try
        {
            streamHandle.writeInt(FightProtocol.FORCE_EXHAUST_CARD);
            streamHandle.writeInt(idCard);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //消耗某张牌的解码信息
    public static void exhaustCardDecode(DataInputStream streamHandle)
    {
        try
        {
            //读取要消耗的牌
            int idCard = streamHandle.readInt();
            //转换成实际的牌
            AbstractCard card = UseCardSend.getPlayerCardInstance(idCard);
            if(card!=null)
            {
                AbstractDungeon.actionManager.addToTop(new ExhaustDrawPileCard(card));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }


    @Override
    public void update() {
        if(AbstractDungeon.getMonsters().areMonstersBasicallyDead())
        {
            this.isDone = true;
            return;
        }
        ArrayList<AbstractCard> drawingCards = targetMonster.getDrawingCards();
        //如果抽牌堆中没有牌就直接结束
        if(drawingCards.isEmpty())
        {
            this.isDone=true;
            return;
        }
        //读取对方即将抽到的牌显示在屏幕上
        if (this.duration == Settings.ACTION_DUR_FAST)
        {
            //遍历每个牌，让它们正常显示
            for(AbstractCard eachCard : drawingCards)
            {
                eachCard.unfadeOut();
            }
            AbstractDungeon.cardRewardScreen.customCombatOpen(drawingCards, CardRewardScreen.TEXT[1], true);
            this.tickDuration();
            return;
        }
        if(!selectedFlag)
        {
            //判断是否选了某个牌
            if (AbstractDungeon.cardRewardScreen.discoveryCard != null) {
                //记录选到的牌
                AbstractCard selectCard = AbstractDungeon.cardRewardScreen.discoveryCard;
                //获得这个牌的id
                int idCard = targetMonster.playerCardManager.getCardId(selectCard);
                //如果是一个合法的id的话，就把它发给对方，告诉它消耗这张牌
                if(idCard>=0)
                {
                    Communication.sendEvent(new RemoveCardEvent(idCard,targetMonster.playerTag));
                }
                //取消选牌的记录
                AbstractDungeon.cardRewardScreen.discoveryCard=null;
                //判断是否获得这张牌
                if(this.getCardFlag)
                {
                    //复制一份牌
                    AbstractCard tempCard = selectCard.makeStatEquivalentCopy();
                    //如果耗能为0,让它下次打出为零
                    if(noCostFlag)
                        tempCard.setCostForTurn(0);
                    //加入到手中
                    this.addToBot(new MakeTempCardInHandAction(tempCard, true));
                }
            }
            selectedFlag=true;
            //重新整理手牌位置
            targetMonster.battleCardPanel.cardBox.shownCards.justUpdateFlag = true;
        }
        this.tickDuration();
    }
}
