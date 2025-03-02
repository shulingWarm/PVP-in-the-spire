package pvp_in_the_spire.ui.CardDesign;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen;
import com.megacrit.cardcrawl.screens.mainMenu.TabBarListener;
import pvp_in_the_spire.ui.Events.PvpTabBarListener;

import java.util.ArrayList;

public class PvpColorTabBar {

    private static final float TAB_SPACING;
    private static final int BAR_W = 1334;
    private static final int BAR_H = 102;
    private static final int TAB_W = 274;
    private static final int TAB_H = 68;
    private static final int TICKBOX_W = 48;
    public Hitbox redHb;
    public Hitbox greenHb;
    public Hitbox blueHb;
    public Hitbox purpleHb;
    public Hitbox colorlessHb;
    public Hitbox curseHb;
    //6个选项卡的位置
    public TabBarItem[] tabBarItems;
    //不同的选项内容
    public ArrayList<TabNameItem> tabList;
    //随机颜色生成器
    public ColorRng colorRng = new ColorRng();
    public Hitbox viewUpgradeHb;
    public PvpColorTabBar.CurrentTab curTab;
    private PvpTabBarListener delegate;

    public PvpColorTabBar(PvpTabBarListener delegate) {
        this.curTab = PvpColorTabBar.CurrentTab.RED;
        float w = 200.0F * Settings.scale;
        float h = 50.0F * Settings.scale;
        this.redHb = new Hitbox(w, h);
        this.greenHb = new Hitbox(w, h);
        this.blueHb = new Hitbox(w, h);
        this.purpleHb = new Hitbox(w, h);
        this.colorlessHb = new Hitbox(w, h);
        this.curseHb = new Hitbox(w, h);
        //初始化tab内容
        this.tabList = new ArrayList<>();
        //添加基本的6个数据信息
        tabList.add(new TabNameItem(CardLibraryScreen.TEXT[1], Color.WHITE, ImageMaster.COLOR_TAB_RED));
        tabList.add(new TabNameItem(CardLibraryScreen.TEXT[2], Color.WHITE, ImageMaster.COLOR_TAB_GREEN));
        tabList.add(new TabNameItem(CardLibraryScreen.TEXT[3], Color.WHITE, ImageMaster.COLOR_TAB_BLUE));
        tabList.add(new TabNameItem(CardLibraryScreen.TEXT[8], Color.WHITE, ImageMaster.COLOR_TAB_PURPLE));
        tabList.add(new TabNameItem(CardLibraryScreen.TEXT[4], Color.WHITE, ImageMaster.COLOR_TAB_COLORLESS));
        tabList.add(new TabNameItem(CardLibraryScreen.TEXT[5], Color.WHITE, ImageMaster.COLOR_TAB_CURSE));
        //初始化6个位置的tab bar
        this.tabBarItems = new TabBarItem[6];
        tabBarItems[0] = new TabBarItem(this.redHb);
        tabBarItems[1] = new TabBarItem(this.greenHb);
        tabBarItems[2] = new TabBarItem(this.blueHb);
        tabBarItems[3] = new TabBarItem(this.purpleHb);
        tabBarItems[4] = new TabBarItem(this.colorlessHb);
        tabBarItems[5] = new TabBarItem(this.curseHb);
        //注册当前第一页情况下的每个位置的tab信息
        for(int idTab=0;idTab<6;++idTab)
        {
            tabBarItems[idTab].registerTabItem(tabList.get(idTab));
        }
        this.delegate = delegate;
        this.viewUpgradeHb = new Hitbox(360.0F * Settings.scale, 48.0F * Settings.scale);
    }

    //添加新的选项卡
    public void registerTab(String tabName)
    {
        //添加注册卡包的内容
        this.tabList.add(new TabNameItem(
            tabName, this.colorRng.getRandColor(), ImageMaster.COLOR_TAB_COLORLESS));
    }

    public void update(float y) {
        float x = 470.0F * Settings.xScale;
        this.redHb.move(x, y + 50.0F * Settings.scale);
        float var4;
        this.greenHb.move(var4 = x + TAB_SPACING, y + 50.0F * Settings.scale);
        this.blueHb.move(x = var4 + TAB_SPACING, y + 50.0F * Settings.scale);
        float var6;
        this.purpleHb.move(var6 = x + TAB_SPACING, y + 50.0F * Settings.scale);
        this.colorlessHb.move(x = var6 + TAB_SPACING, y + 50.0F * Settings.scale);
        this.curseHb.move(x + TAB_SPACING, y + 50.0F * Settings.scale);
        this.viewUpgradeHb.move(1410.0F * Settings.xScale, y);
        this.redHb.update();
        this.greenHb.update();
        this.blueHb.update();
        this.purpleHb.update();
        this.colorlessHb.update();
        this.curseHb.update();
        this.viewUpgradeHb.update();
        if (this.redHb.justHovered || this.greenHb.justHovered || this.blueHb.justHovered || this.colorlessHb.justHovered || this.curseHb.justHovered || this.purpleHb.justHovered) {
            CardCrawlGame.sound.playA("UI_HOVER", -0.4F);
        }

        if (InputHelper.justClickedLeft) {
            PvpColorTabBar.CurrentTab oldTab = this.curTab;
            if (this.redHb.hovered) {
                this.curTab = PvpColorTabBar.CurrentTab.RED;
            } else if (this.greenHb.hovered) {
                this.curTab = PvpColorTabBar.CurrentTab.GREEN;
            } else if (this.blueHb.hovered) {
                this.curTab = PvpColorTabBar.CurrentTab.BLUE;
            } else if (this.purpleHb.hovered) {
                this.curTab = PvpColorTabBar.CurrentTab.PURPLE;
            } else if (this.colorlessHb.hovered) {
                this.curTab = PvpColorTabBar.CurrentTab.COLORLESS;
            } else if (this.curseHb.hovered) {
                this.curTab = PvpColorTabBar.CurrentTab.CURSE;
            }

            if (oldTab != this.curTab) {
                this.delegate.changeColorTabBar(this.curTab);
            }
        }

        if (this.viewUpgradeHb.justHovered) {
            CardCrawlGame.sound.playA("UI_HOVER", -0.3F);
        }

        if (this.viewUpgradeHb.hovered && InputHelper.justClickedLeft) {
            this.viewUpgradeHb.clickStarted = true;
        }

        if (this.viewUpgradeHb.clicked || this.viewUpgradeHb.hovered && CInputActionSet.select.isJustPressed()) {
            this.viewUpgradeHb.clicked = false;
            CardCrawlGame.sound.playA("UI_CLICK_1", -0.2F);
            SingleCardViewPopup.isViewingUpgrade = !SingleCardViewPopup.isViewingUpgrade;
        }

    }

    public Color getBarColor() {
        switch (this.curTab) {
            case RED:
                return new Color(0.5F, 0.1F, 0.1F, 1.0F);
            case GREEN:
                return new Color(0.25F, 0.55F, 0.0F, 1.0F);
            case BLUE:
                return new Color(0.01F, 0.34F, 0.52F, 1.0F);
            case PURPLE:
                return new Color(0.37F, 0.22F, 0.49F, 1.0F);
            case COLORLESS:
                return new Color(0.4F, 0.4F, 0.4F, 1.0F);
            case CURSE:
                return new Color(0.18F, 0.18F, 0.16F, 1.0F);
            default:
                return Color.WHITE;
        }
    }

    public void render(SpriteBatch sb, float y) {
        sb.setColor(Color.GRAY);
        if (this.curTab != PvpColorTabBar.CurrentTab.CURSE) {
            this.renderTab(sb, ImageMaster.COLOR_TAB_CURSE, this.curseHb.cX, y, CardLibraryScreen.TEXT[5], true);
        }

        if (this.curTab != PvpColorTabBar.CurrentTab.COLORLESS) {
            this.renderTab(sb, ImageMaster.COLOR_TAB_COLORLESS, this.colorlessHb.cX, y, CardLibraryScreen.TEXT[4], true);
        }

        if (this.curTab != PvpColorTabBar.CurrentTab.BLUE) {
            this.renderTab(sb, ImageMaster.COLOR_TAB_BLUE, this.blueHb.cX, y, CardLibraryScreen.TEXT[3], true);
        }

        if (this.curTab != PvpColorTabBar.CurrentTab.PURPLE) {
            this.renderTab(sb, ImageMaster.COLOR_TAB_PURPLE, this.purpleHb.cX, y, CardLibraryScreen.TEXT[8], true);
        }

        if (this.curTab != PvpColorTabBar.CurrentTab.GREEN) {
            this.renderTab(sb, ImageMaster.COLOR_TAB_GREEN, this.greenHb.cX, y, CardLibraryScreen.TEXT[2], true);
        }

        if (this.curTab != PvpColorTabBar.CurrentTab.RED) {
            this.renderTab(sb, ImageMaster.COLOR_TAB_RED, this.redHb.cX, y, CardLibraryScreen.TEXT[1], true);
        }

        sb.setColor(this.getBarColor());
        sb.draw(ImageMaster.COLOR_TAB_BAR, (float)Settings.WIDTH / 2.0F - 667.0F, y - 51.0F, 667.0F, 51.0F, 1334.0F, 102.0F, Settings.xScale, Settings.scale, 0.0F, 0, 0, 1334, 102, false, false);
        sb.setColor(Color.WHITE);
        switch (this.curTab) {
            case RED:
                this.renderTab(sb, ImageMaster.COLOR_TAB_RED, this.redHb.cX, y, CardLibraryScreen.TEXT[1], false);
                break;
            case GREEN:
                this.renderTab(sb, ImageMaster.COLOR_TAB_GREEN, this.greenHb.cX, y, CardLibraryScreen.TEXT[2], false);
                break;
            case BLUE:
                this.renderTab(sb, ImageMaster.COLOR_TAB_BLUE, this.blueHb.cX, y, CardLibraryScreen.TEXT[3], false);
                break;
            case PURPLE:
                this.renderTab(sb, ImageMaster.COLOR_TAB_PURPLE, this.purpleHb.cX, y, CardLibraryScreen.TEXT[8], false);
                break;
            case COLORLESS:
                this.renderTab(sb, ImageMaster.COLOR_TAB_COLORLESS, this.colorlessHb.cX, y, CardLibraryScreen.TEXT[4], false);
                break;
            case CURSE:
                this.renderTab(sb, ImageMaster.COLOR_TAB_CURSE, this.curseHb.cX, y, CardLibraryScreen.TEXT[5], false);
        }

        this.renderViewUpgrade(sb, y);
        this.redHb.render(sb);
        this.greenHb.render(sb);
        this.blueHb.render(sb);
        this.purpleHb.render(sb);
        this.colorlessHb.render(sb);
        this.curseHb.render(sb);
        this.viewUpgradeHb.render(sb);
    }

    private void renderTab(SpriteBatch sb, Texture img, float x, float y, String label, boolean selected) {
        sb.draw(img, x - 137.0F, y - 34.0F + 53.0F * Settings.scale, 137.0F, 34.0F, 274.0F, 68.0F, Settings.xScale, Settings.scale, 0.0F, 0, 0, 274, 68, false, false);
        Color c = Settings.GOLD_COLOR;
        if (selected) {
            c = Color.GRAY;
        }

        FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, label, x, y + 50.0F * Settings.scale, c, 0.9F);
    }

    private void renderViewUpgrade(SpriteBatch sb, float y) {
        Color c = Settings.CREAM_COLOR;
        if (this.viewUpgradeHb.hovered) {
            c = Settings.GOLD_COLOR;
        }

        FontHelper.renderFontRightAligned(sb, FontHelper.topPanelInfoFont, CardLibraryScreen.TEXT[7], 1546.0F * Settings.xScale, y, c);
        Texture img = SingleCardViewPopup.isViewingUpgrade ? ImageMaster.COLOR_TAB_BOX_TICKED : ImageMaster.COLOR_TAB_BOX_UNTICKED;
        sb.setColor(c);
        sb.draw(img, 1532.0F * Settings.xScale - FontHelper.getSmartWidth(FontHelper.topPanelInfoFont, CardLibraryScreen.TEXT[7], 9999.0F, 0.0F) - 24.0F, y - 24.0F, 24.0F, 24.0F, 48.0F, 48.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 48, 48, false, false);
    }

    static {
        TAB_SPACING = 198.0F * Settings.xScale;
    }

    public static enum CurrentTab {
        RED,
        GREEN,
        BLUE,
        PURPLE,
        COLORLESS,
        CURSE;

        private CurrentTab() {
        }
    }

}
