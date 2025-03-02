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
    //当前的tab id
    public int currIdTab = 0;
    private PvpTabBarListener delegate;

    public PvpColorTabBar(PvpTabBarListener delegate) {
        this.currIdTab = 0;
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

    //根据id获得枚举
    PvpColorTabBar.CurrentTab getTabEnumFromId(int idTab)
    {
        if(idTab == 0)
            return CurrentTab.RED;
        else if(idTab == 1)
            return CurrentTab.GREEN;
        else if(idTab == 2)
            return CurrentTab.BLUE;
        else if(idTab == 3)
            return  CurrentTab.PURPLE;
        else if(idTab == 4)
            return CurrentTab.COLORLESS;
        return CurrentTab.CURSE;
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
            int oldId = this.currIdTab;
            //遍历每个tab，检查是否有效
            for(int idTab=0;idTab<6;++idTab)
            {
                //判断当前的hit box是否被点击
                if(this.tabBarItems[idTab].refHitBox.hovered)
                {
                    this.currIdTab = idTab;
                    break;
                }
            }

            if (oldId != this.currIdTab) {
                this.delegate.changeColorTabBar(this.getTabEnumFromId(this.currIdTab));
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
        switch (this.currIdTab) {
            case 0:
                return new Color(0.5F, 0.1F, 0.1F, 1.0F);
            case 1:
                return new Color(0.25F, 0.55F, 0.0F, 1.0F);
            case 2:
                return new Color(0.01F, 0.34F, 0.52F, 1.0F);
            case 3:
                return new Color(0.37F, 0.22F, 0.49F, 1.0F);
            case 4:
                return new Color(0.4F, 0.4F, 0.4F, 1.0F);
            case 5:
                return new Color(0.18F, 0.18F, 0.16F, 1.0F);
            default:
                return Color.WHITE;
        }
    }

    public void render(SpriteBatch sb, float y) {
        sb.setColor(Color.GRAY);
        //渲染没有被选中的tab
        for(int idTab=0;idTab<6;++idTab)
        {
            if(idTab != currIdTab)
            {
                //调用tab内容的渲染过程
                this.tabBarItems[idTab].render(sb, y, true);
            }
        }

        sb.setColor(this.getBarColor());
        sb.draw(ImageMaster.COLOR_TAB_BAR, (float)Settings.WIDTH / 2.0F - 667.0F, y - 51.0F, 667.0F, 51.0F, 1334.0F, 102.0F, Settings.xScale, Settings.scale, 0.0F, 0, 0, 1334, 102, false, false);
        sb.setColor(Color.WHITE);
        //调用当前选中的tab的渲染过程
        this.tabBarItems[this.currIdTab].render(sb, y, false);

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
