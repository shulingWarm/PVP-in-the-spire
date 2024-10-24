package pvp_in_the_spire.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.LocalizedStrings;

import java.util.HashMap;

//用来管理各种字体资源的东西
public class FontLibrary {

    public static BitmapFont generateFont(int size)
    {
        //供系统使用的字体，但仅限于中文的情况
        FileHandle fontFile = Gdx.files.internal("font/zhs/NotoSansMonoCJKsc-Regular.otf");
        //字体生成器
        FreeTypeFontGenerator g = new FreeTypeFontGenerator(fontFile);

        if (Settings.BIG_TEXT_MODE) {
            size *= 1.2F;
        }
        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
        p.characters = "";
        p.incremental = true;
        p.size = Math.round(size * Settings.scale);
        p.gamma = 0.9F;
        p.spaceX = 0;
        p.spaceY = 0;
        p.borderColor = new Color(0.35F, 0.35F, 0.35F, 1.0F);
        p.borderStraight = false;
        p.borderWidth = 2.0F * Settings.scale;
        p.borderGamma = 0.9F;
        p.shadowColor = new Color(0.0F, 0.0F, 0.0F, 0.25F);
        p.shadowOffsetX = Math.round(3.0F * Settings.scale);
        p.shadowOffsetY = Math.round(3.0F * Settings.scale);
        p.minFilter = Texture.TextureFilter.Linear;
        p.magFilter = Texture.TextureFilter.Linear;

        g.scaleForPixelHeight(p.size);
        BitmapFont font = g.generateFont(p);
        font.setUseIntegerPositions(false);
        font.getData().markupEnabled = true;
        if (LocalizedStrings.break_chars != null) {
            font.getData().breakChars = LocalizedStrings.break_chars.toCharArray();
        }

        font.getData().fontFile = fontFile;
        return font;
    }

    //基本的字体显示
    public static BitmapFont baseFont = null;

    //不同大小的字体
    public static HashMap<Integer,BitmapFont> fontSizeMap = new HashMap<>();

    //获得最基本的字体
    public static BitmapFont getBaseFont()
    {
        //判断是否已经初始化过基本字体
        if(baseFont == null)
        {
            baseFont = generateFont(26);
        }
        return baseFont;
    }

    //获取带size的字体
    public static BitmapFont getFontWithSize(int targetSize)
    {
        //判断map里面有没有这个号的字体
        if(!fontSizeMap.containsKey(targetSize))
        {
            fontSizeMap.put(targetSize,generateFont(targetSize));
        }
        return fontSizeMap.get(targetSize);
    }

    //获取文本的宽度
    public static float getTextWidth(String str,BitmapFont font)
    {
        FontHelper.layout.setText(font,str);
        return FontHelper.layout.width;
    }


}
