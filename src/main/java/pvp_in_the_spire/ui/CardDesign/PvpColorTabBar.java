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
import pvp_in_the_spire.helpers.FontLibrary;
import pvp_in_the_spire.ui.Button.WithUpdate.BaseUpdateButton;
import pvp_in_the_spire.ui.Events.ClickCallback;
import pvp_in_the_spire.ui.Events.PvpTabBarListener;

import java.util.ArrayList;

public class PvpColorTabBar implements ClickCallback {

    private static final float TAB_SPACING;
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
    //左右翻页的按钮
    public BaseUpdateButton leftButton;
    public BaseUpdateButton rightButton;
    //每一页里面tab的个数
    public static final int PAGE_TAB_NUM = 6;
    //当前的tab id
    public int currIdTab = 0;
    //选项卡的总页数
    public int tabPageNum = 1;
    //当前的页数
    public int currIdPage = 0;
    private PvpTabBarListener delegate;
    //左右按钮的起始位置
    public static final float LEFT_BUTTON_X = 0.13f * Settings.WIDTH;
    public static final float RIGHT_BUTTON_X = 0.82f * Settings.WIDTH;
    //翻页按钮的宽度
    public static final float BUTTON_WIDTH = 0.05f * Settings.WIDTH;
    //按钮显示的Y位置
    public static final float BUTTON_Y = 0.83f * Settings.HEIGHT;

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
        this.tabBarItems = new TabBarItem[PAGE_TAB_NUM];
        tabBarItems[0] = new TabBarItem(this.redHb);
        tabBarItems[1] = new TabBarItem(this.greenHb);
        tabBarItems[2] = new TabBarItem(this.blueHb);
        tabBarItems[3] = new TabBarItem(this.purpleHb);
        tabBarItems[4] = new TabBarItem(this.colorlessHb);
        tabBarItems[5] = new TabBarItem(this.curseHb);
        //注册当前第一页情况下的每个位置的tab信息
        for(int idTab=0;idTab<PAGE_TAB_NUM;++idTab)
        {
            tabBarItems[idTab].registerTabItem(tabList.get(idTab));
        }
        this.delegate = delegate;
        this.viewUpgradeHb = new Hitbox(360.0F * Settings.scale, 48.0F * Settings.scale);
        //初始化左右按钮
        this.leftButton = new BaseUpdateButton(
                LEFT_BUTTON_X,BUTTON_Y,
                BUTTON_WIDTH,BUTTON_WIDTH,
                "", FontLibrary.getBaseFont(), ImageMaster.CF_LEFT_ARROW,this
        );
        this.rightButton = new BaseUpdateButton(
                RIGHT_BUTTON_X,
                leftButton.y,
                leftButton.width, leftButton.height, leftButton.text,
                FontLibrary.getBaseFont(),ImageMaster.CF_RIGHT_ARROW,this
        );
        //翻页按钮默认不可点击，因为默认只有一页
        this.updatePageChangeButton();
    }

    //根据page id更新按钮的可点击状态
    public void updatePageChangeButton()
    {
        this.leftButton.setEnableFlag(this.currIdPage > 0);
        this.rightButton.setEnableFlag(this.currIdTab + 1 < this.tabPageNum);
    }

    //添加新的选项卡
    public void registerTab(String tabName)
    {
        //添加注册卡包的内容
        this.tabList.add(new TabNameItem(
            tabName, this.colorRng.getRandColor(), ImageMaster.COLOR_TAB_COLORLESS));
        //更改总的page个数
        this.tabPageNum = (this.tabList.size() + PAGE_TAB_NUM - 1) / PAGE_TAB_NUM;
        //更新按钮的可点击状态
        this.updatePageChangeButton();
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

    //执行tab id的切换
    public void recallTabIdChange()
    {
        //如果是第零页的话，就是正常的recall逻辑
        if(this.currIdPage == 0)
        {
            this.delegate.changeColorTabBar(this.getTabEnumFromId(this.currIdTab));
        }
        else
        {
            this.delegate.changeShowCardPackage(this.tabBarItems[this.currIdTab].getTabName());
        }
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
                this.recallTabIdChange();
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
        leftButton.update();
        rightButton.update();

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
        //左右翻页按钮的渲染
        this.leftButton.render(sb);
        this.rightButton.render(sb);
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

    //加载某一个page的tab信息
    public void loadIdPage(int idPage)
    {
        if(idPage < 0 || idPage >= this.tabPageNum)
            return;
        this.currIdPage = idPage;
        //tab id也弄成0
        this.currIdTab = 0;
        //当前页的起始id
        int beginId = idPage*PAGE_TAB_NUM;
        //结束位置的id
        int endId = (idPage+1)*PAGE_TAB_NUM;
        if(endId > this.tabList.size())
            endId = this.tabList.size();
        //遍历每个page内容
        for(int id=beginId;id<endId;++id)
        {
            this.tabBarItems[id-beginId].registerTabItem(this.tabList.get(id));
        }
        //剩下的内容记记录成空指针
        int nullId = (idPage+1)*PAGE_TAB_NUM;
        for(int id=endId;id<nullId;++id)
        {
            this.tabBarItems[id-beginId].registerTabItem(null);
        }
        //更新按钮的可点击状态
        this.updatePageChangeButton();
        recallTabIdChange();
    }

    static {
        TAB_SPACING = 198.0F * Settings.xScale;
    }

    @Override
    public void clickEvent(BaseUpdateButton button) {
        //判断是不是左边翻页
        if(button == this.leftButton)
        {
            this.loadIdPage(this.currIdPage - 1);
        }
        else if(button == this.rightButton)
        {
            this.loadIdPage(this.currIdPage + 1);
        }
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
