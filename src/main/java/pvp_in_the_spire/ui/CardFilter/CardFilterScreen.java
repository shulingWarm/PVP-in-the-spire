package pvp_in_the_spire.ui.CardFilter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.GameCursor;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.controller.CInputHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import com.megacrit.cardcrawl.screens.mainMenu.*;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pvp_in_the_spire.events.BanCardStageChangeEvent;
import pvp_in_the_spire.patches.CardShowPatch.CardShowChange;
import pvp_in_the_spire.pvp_api.Communication;
import pvp_in_the_spire.ui.AbstractPage;
import pvp_in_the_spire.ui.Events.ClosePageEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class CardFilterScreen extends AbstractPage
    implements TabBarListener, ScrollBarListener {

    public static CardFilterScreen instance;

    private static final Logger logger = LogManager.getLogger(CardFilterScreen.class.getName());
    private static float drawStartX;
    private static float drawStartY;
    private static float padX;
    private static float padY;
    private static final int CARDS_PER_LINE;
    private boolean grabbedScreen = false;
    private float grabStartY = 0.0F;
    private float currentDiffY = 0.0F;
    private float scrollLowerBound;
    private float scrollUpperBound;
    private AbstractCard hoveredCard;
    private AbstractCard clickStartedCard;
    private ColorTabBar colorBar;
    public MenuCancelButton button;
    private CardGroup redCards;
    private CardGroup greenCards;
    private CardGroup blueCards;
    private CardGroup purpleCards;
    private CardGroup colorlessCards;
    private CardGroup curseCards;
    private CardLibSortHeader sortHeader;
    private CardGroup visibleCards;
    private ScrollBar scrollBar;
    private CardFilterScreen.CardLibSelectionType type;
    private Texture filterSelectionImg;
    private int selectionIndex;
    private AbstractCard controllerCard;
    private Color highlightBoxColor;
    public HashMap<String, AbstractCard> cardMap;

    public CardFilter cardFilter;

    //关闭页面时的回调函数
    public ClosePageEvent closeCallback = null;

    public CardFilterScreen() {
        this.scrollLowerBound = -Settings.DEFAULT_SCROLL_LIMIT;
        this.scrollUpperBound = Settings.DEFAULT_SCROLL_LIMIT;
        this.hoveredCard = null;
        this.clickStartedCard = null;
        this.button = new MenuCancelButton();
        this.redCards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        this.greenCards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        this.blueCards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        this.purpleCards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        this.colorlessCards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        this.curseCards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        this.type = CardFilterScreen.CardLibSelectionType.NONE;
        this.filterSelectionImg = null;
        this.selectionIndex = 0;
        this.controllerCard = null;
        this.highlightBoxColor = new Color(1.0F, 0.95F, 0.5F, 0.0F);
        drawStartX = (float)Settings.WIDTH;
        drawStartX -= (float)CARDS_PER_LINE * AbstractCard.IMG_WIDTH * 0.75F;
        drawStartX -= (float)(CARDS_PER_LINE - 1) * Settings.CARD_VIEW_PAD_X;
        drawStartX /= 2.0F;
        drawStartX += AbstractCard.IMG_WIDTH * 0.75F / 2.0F;
        padX = AbstractCard.IMG_WIDTH * 0.75F + Settings.CARD_VIEW_PAD_X;
        padY = AbstractCard.IMG_HEIGHT * 0.75F + Settings.CARD_VIEW_PAD_Y;
        this.colorBar = new ColorTabBar(this);
        this.sortHeader = new CardLibSortHeader((CardGroup)null);
        this.scrollBar = new ScrollBar(this);
        this.initialize();
        //初始化卡牌过滤器
        this.cardFilter = new CardFilter();
    }

    //把所有的卡牌记录到哈希表里面
    public void recordCardsInHash(ArrayList<AbstractCard> cards)
    {
        for(AbstractCard eachCard : cards)
        {
            this.cardMap.put(eachCard.cardID,eachCard);
        }
    }

    public void initialize() {
        logger.info("Initializing card library screen.");
        this.redCards.group = CardLibrary.getCardList(CardLibrary.LibraryType.RED);
        this.greenCards.group = CardLibrary.getCardList(CardLibrary.LibraryType.GREEN);
        this.blueCards.group = CardLibrary.getCardList(CardLibrary.LibraryType.BLUE);
        this.purpleCards.group = CardLibrary.getCardList(CardLibrary.LibraryType.PURPLE);
        this.colorlessCards.group = CardLibrary.getCardList(CardLibrary.LibraryType.COLORLESS);
        this.curseCards.group = CardLibrary.getCardList(CardLibrary.LibraryType.CURSE);
        this.cardMap = new HashMap<>();
        recordCardsInHash(redCards.group);
        recordCardsInHash(greenCards.group);
        recordCardsInHash(blueCards.group);
        recordCardsInHash(purpleCards.group);
        recordCardsInHash(colorlessCards.group);
        recordCardsInHash(curseCards.group);
        this.visibleCards = this.redCards;
        this.sortHeader.setGroup(this.visibleCards);
        this.calculateScrollBounds();
    }

    //设置这个页面的关闭回调
    public void setCloseCallback(ClosePageEvent closeCallback)
    {
        this.closeCallback = closeCallback;
    }


    private void setLockStatus() {
        this.lockStatusHelper(this.redCards);
        this.lockStatusHelper(this.greenCards);
        this.lockStatusHelper(this.blueCards);
        this.lockStatusHelper(this.purpleCards);
        this.lockStatusHelper(this.colorlessCards);
        this.lockStatusHelper(this.curseCards);
    }

    private void lockStatusHelper(CardGroup group) {
        ArrayList<AbstractCard> toAdd = new ArrayList();
        Iterator<AbstractCard> i = group.group.iterator();

        while(i.hasNext()) {
            AbstractCard c = (AbstractCard)i.next();
            if (UnlockTracker.isCardLocked(c.cardID)) {
                AbstractCard tmp = CardLibrary.getCopy(c.cardID);
                tmp.setLocked();
                toAdd.add(tmp);
                i.remove();
            }
        }

        group.group.addAll(toAdd);
    }

    public void open() {
        this.controllerCard = null;
        if (Settings.isInfo) {
            CardLibrary.unlockAndSeeAllCards();
        }

        if (this.filterSelectionImg == null) {
            this.filterSelectionImg = ImageMaster.loadImage("images/ui/cardlibrary/selectBox.png");
        }

        this.setLockStatus();
        this.sortOnOpen();
        this.currentDiffY = this.scrollLowerBound;
        SingleCardViewPopup.isViewingUpgrade = false;
    }

    private void sortOnOpen() {
        this.sortHeader.justSorted = true;
        this.visibleCards.sortAlphabetically(true);
        this.visibleCards.sortByRarity(true);
        this.visibleCards.sortByStatus(true);

        for(AbstractCard c : this.visibleCards.group) {
            c.drawScale = MathUtils.random(0.6F, 0.65F);
            c.targetDrawScale = 0.75F;
        }

    }

    public void banCard(AbstractCard card)
    {
        CardShowChange.changeCardAlpha(card,0.5f);
        this.cardFilter.banCard(card.cardID);
    }

    public void restoreCard(AbstractCard card)
    {
        CardShowChange.changeCardAlpha(card,1);
        this.cardFilter.restoreCard(card.cardID);
    }

    //重置卡牌禁用的状态
    public void resetBanCardStage()
    {
        HashSet<String> bannedCards = this.cardFilter.bannedCards;
        for(String eachCard : bannedCards)
        {
            if(this.cardMap.containsKey(eachCard))
            {
                CardShowChange.changeCardAlpha(
                    this.cardMap.get(eachCard),1
                );
            }
        }
        bannedCards.clear();
    }

    //更改卡牌的禁用状态
    public void changeCardBanStage(String cardId,boolean banStage)
    {
        //寻找目标卡牌
        if(this.cardMap.containsKey(cardId))
        {
            AbstractCard tempCard = this.cardMap.get(cardId);
            if(banStage)
                banCard(tempCard);
            else
                restoreCard(tempCard);
        }
    }

    public void update() {
        this.colorBar.update(this.visibleCards.getBottomCard().current_y + 230.0F * Settings.yScale);
        this.sortHeader.update();
        if (this.hoveredCard != null) {
            CardCrawlGame.cursor.changeType(GameCursor.CursorType.INSPECT);
            if (InputHelper.justClickedLeft) {
                this.clickStartedCard = this.hoveredCard;
            }

            if (InputHelper.justReleasedClickLeft && this.clickStartedCard != null && this.hoveredCard != null || this.hoveredCard != null && CInputActionSet.select.isJustPressed()) {
                InputHelper.justReleasedClickLeft = false;
                //判断目标卡牌是否已经被禁用
                if(this.cardFilter.isCardAvailable(this.clickStartedCard.cardID))
                {
                    banCard(this.clickStartedCard);
                    Communication.sendEvent(new BanCardStageChangeEvent(this.clickStartedCard.cardID,true));
                }
                else
                {
                    restoreCard(this.clickStartedCard);
                    Communication.sendEvent(new BanCardStageChangeEvent(this.clickStartedCard.cardID,false));
                }
                // CardCrawlGame.cardPopup.open(this.clickStartedCard, this.visibleCards);
                this.clickStartedCard = null;
            }
        } else {
            this.clickStartedCard = null;
        }

        boolean isScrollBarScrolling = this.scrollBar.update();
        if (!CardCrawlGame.cardPopup.isOpen && !isScrollBarScrolling) {
            this.updateScrolling();
        }

        this.updateCards();
        this.button.update();
        if (this.button.hb.clicked || InputHelper.pressedEscape) {
            InputHelper.pressedEscape = false;
            this.button.hb.clicked = false;
            //通知上层页面，执行退出逻辑
            if(this.closeCallback != null)
            {
                this.closeCallback.closePageEvent(this);
            }
        }

    }

    private void updateCards() {
        this.hoveredCard = null;
        int lineNum = 0;
        ArrayList<AbstractCard> cards = this.visibleCards.group;

        for(int i = 0; i < cards.size(); ++i) {
            int mod = i % CARDS_PER_LINE;
            if (mod == 0 && i != 0) {
                ++lineNum;
            }

            ((AbstractCard)cards.get(i)).target_x = drawStartX + (float)mod * padX;
            ((AbstractCard)cards.get(i)).target_y = drawStartY + this.currentDiffY - (float)lineNum * padY;
            ((AbstractCard)cards.get(i)).update();
            ((AbstractCard)cards.get(i)).updateHoverLogic();
            if (((AbstractCard)cards.get(i)).hb.hovered) {
                this.hoveredCard = (AbstractCard)cards.get(i);
            }
        }

        if (this.sortHeader.justSorted) {
            for(AbstractCard c : cards) {
                c.current_x = c.target_x;
                c.current_y = c.target_y;
            }

            this.sortHeader.justSorted = false;
        }

    }

    private void updateScrolling() {
        int y = InputHelper.mY;
        if (!this.grabbedScreen) {
            if (InputHelper.scrolledDown) {
                this.currentDiffY += Settings.SCROLL_SPEED;
            } else if (InputHelper.scrolledUp) {
                this.currentDiffY -= Settings.SCROLL_SPEED;
            }

            if (InputHelper.justClickedLeft) {
                this.grabbedScreen = true;
                this.grabStartY = (float)y - this.currentDiffY;
            }
        } else if (InputHelper.isMouseDown) {
            this.currentDiffY = (float)y - this.grabStartY;
        } else {
            this.grabbedScreen = false;
        }

        this.resetScrolling();
        this.updateBarPosition();
    }

    private void calculateScrollBounds() {
        int size = this.visibleCards.size();
        int scrollTmp = 0;
        if (size > CARDS_PER_LINE * 2) {
            scrollTmp = size / CARDS_PER_LINE - 2;
            if (size % CARDS_PER_LINE != 0) {
                ++scrollTmp;
            }

            this.scrollUpperBound = Settings.DEFAULT_SCROLL_LIMIT + (float)scrollTmp * padY;
        } else {
            this.scrollUpperBound = Settings.DEFAULT_SCROLL_LIMIT;
        }

    }

    private void resetScrolling() {
        if (this.currentDiffY < this.scrollLowerBound) {
            this.currentDiffY = MathHelper.scrollSnapLerpSpeed(this.currentDiffY, this.scrollLowerBound);
        } else if (this.currentDiffY > this.scrollUpperBound) {
            this.currentDiffY = MathHelper.scrollSnapLerpSpeed(this.currentDiffY, this.scrollUpperBound);
        }

    }

    public void render(SpriteBatch sb) {
        this.scrollBar.render(sb);
        this.colorBar.render(sb, this.visibleCards.getBottomCard().current_y + 230.0F * Settings.yScale);
        this.sortHeader.render(sb);
        this.renderGroup(sb, this.visibleCards);
        if (this.hoveredCard != null) {
            this.hoveredCard.renderHoverShadow(sb);
            this.hoveredCard.renderInLibrary(sb);
        }

        this.button.render(sb);
        if (Settings.isControllerMode) {
            this.renderControllerUi(sb);
        }

    }

    private void renderControllerUi(SpriteBatch sb) {
        sb.draw(CInputActionSet.pageLeftViewDeck.getKeyImg(), 280.0F * Settings.xScale - 32.0F, this.sortHeader.group.getBottomCard().current_y + 280.0F * Settings.yScale - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
        sb.draw(CInputActionSet.pageRightViewExhaust.getKeyImg(), 1640.0F * Settings.xScale - 32.0F, this.sortHeader.group.getBottomCard().current_y + 280.0F * Settings.yScale - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
        if (this.type == CardFilterScreen.CardLibSelectionType.FILTERS && (this.selectionIndex == 4 || this.selectionIndex == 3 && Settings.removeAtoZSort)) {
            this.highlightBoxColor.a = 0.7F + MathUtils.cosDeg((float)(System.currentTimeMillis() / 2L % 360L)) / 5.0F;
            sb.setColor(this.highlightBoxColor);
            float doop = 1.0F + (1.0F + MathUtils.cosDeg((float)(System.currentTimeMillis() / 2L % 360L))) / 50.0F;
            sb.draw(this.filterSelectionImg, this.colorBar.viewUpgradeHb.cX - 100.0F, this.colorBar.viewUpgradeHb.cY - 43.0F, 100.0F, 43.0F, 200.0F, 86.0F, Settings.scale * doop * (this.colorBar.viewUpgradeHb.width / 150.0F / Settings.scale), Settings.scale * doop, 0.0F, 0, 0, 200, 86, false, false);
        }

    }

    private void renderGroup(SpriteBatch sb, CardGroup group) {
        group.renderInLibrary(sb);
        group.renderTip(sb);
    }

    public void didChangeTab(ColorTabBar tabBar, ColorTabBar.CurrentTab newSelection) {
        CardGroup oldSelection = this.visibleCards;
        switch (newSelection) {
            case RED:
                this.visibleCards = this.redCards;
                break;
            case GREEN:
                this.visibleCards = this.greenCards;
                break;
            case BLUE:
                this.visibleCards = this.blueCards;
                break;
            case PURPLE:
                this.visibleCards = this.purpleCards;
                break;
            case COLORLESS:
                this.visibleCards = this.colorlessCards;
                break;
            case CURSE:
                this.visibleCards = this.curseCards;
        }

        if (oldSelection != this.visibleCards) {
            this.sortHeader.setGroup(this.visibleCards);
            this.calculateScrollBounds();
        }

        this.sortHeader.justSorted = true;

        for(AbstractCard c : this.visibleCards.group) {
            c.drawScale = MathUtils.random(0.6F, 0.65F);
            c.targetDrawScale = 0.75F;
        }

    }

    public void scrolledUsingBar(float newPercent) {
        this.currentDiffY = MathHelper.valueFromPercentBetween(this.scrollLowerBound, this.scrollUpperBound, newPercent);
        this.updateBarPosition();
    }

    private void updateBarPosition() {
        float percent = MathHelper.percentFromValueBetween(this.scrollLowerBound, this.scrollUpperBound, this.currentDiffY);
        this.scrollBar.parentScrolledToPercent(percent);
    }

    static {
        drawStartY = (float)Settings.HEIGHT * 0.66F;
        CARDS_PER_LINE = (int)((float)Settings.WIDTH / (AbstractCard.IMG_WIDTH * 0.75F + Settings.CARD_VIEW_PAD_X * 3.0F));
    }

    private static enum CardLibSelectionType {
        NONE,
        FILTERS,
        CARDS;

        private CardLibSelectionType() {
        }
    }

}
