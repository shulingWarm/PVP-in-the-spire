package pvp_in_the_spire.ui.Text;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.megacrit.cardcrawl.helpers.FontHelper;

import java.util.ArrayList;

//多行文本的管理器，不负责显示UI，只负责将文本切分为多行
public class AdvTextManager {

    //数据显示的宽度
    public float width;

    //每一行的文本
    ArrayList<String> lines = new ArrayList<>();

    StringBuilder stringBuilder = new StringBuilder();

    boolean freezeFlag = false;

    BitmapFont font;

    public AdvTextManager(float width,BitmapFont font)
    {
        this.width = width;
        this.font = font;
    }

    public float getTextHeight(String str)
    {
        FontHelper.layout.setText(font,str);
        return FontHelper.layout.height;
    }


    //根据文本获取文本的长度
    public float getTextWidth(String str)
    {
        FontHelper.layout.setText(font,str);
        return FontHelper.layout.width;
    }

    //固定当前内容，后续不再更新
    public void freeze()
    {
        this.freezeFlag = true;
        if(stringBuilder.length() > 0)
        {
            lines.add(stringBuilder.toString());
            stringBuilder = new StringBuilder();
        }
    }

    //获取文本的行数
    public int getLineNum()
    {
        if(freezeFlag)
            return lines.size();
        return lines.size() + 1;
    }

    //获取最后一行
    public String getLastLine()
    {
        if(freezeFlag)
        {
            if(lines.isEmpty())
                return "";
            return lines.get(lines.size()-1);
        }
        return stringBuilder.toString();
    }

    //获取某一行的文本
    public String getStr(int index)
    {
        if(index == lines.size())
            return this.stringBuilder.toString();
        return lines.get(index);
    }

    public int findMaxIdx(String str) {
        int left = 0;
        int right = str.length();

        while (left < right) {
            int mid = (left + right) / 2;
            String subStr = str.substring(0, mid + 1); // +1 因为 substring 的右边界是排他的
            float subStrWidth = getTextWidth(subStr);

            if (subStrWidth < width) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }


    //向最后一行追加文本
    public void appendStr(String str)
    {
        stringBuilder.append(str);
        String totalStr = stringBuilder.toString();
        boolean changedFlag = false;
        //维护文本行
        while(getTextWidth(totalStr) > this.width)
        {
            //获取可切割文本的最大值
            int maxId = findMaxIdx(totalStr);
            //记录子串
            this.lines.add(totalStr.substring(0,maxId));
            //更新当前串
            totalStr = totalStr.substring(maxId);
            changedFlag = true;
        }
        if(changedFlag)
        {
            stringBuilder = new StringBuilder(totalStr);
        }
    }

    //删除最后一行的文本
    public void backspace()
    {
        if(stringBuilder.length() > 0)
            stringBuilder.deleteCharAt(stringBuilder.length()-1);
        //如果已经删除完了，看一下还有没有剩下的行
        if(stringBuilder.length() == 0 && !lines.isEmpty())
        {
            stringBuilder.append(lines.get(lines.size()-1));
            lines.remove(lines.size()-1);
        }
    }

    //获取完整的string
    public String getTotalString()
    {
        StringBuilder tempBuilder = new StringBuilder();
        for(int i=0;i<getLineNum();++i)
        {
            tempBuilder.append(this.getStr(i));
        }
        return tempBuilder.toString();
    }


}
