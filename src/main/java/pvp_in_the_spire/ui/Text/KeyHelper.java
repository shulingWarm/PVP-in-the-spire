package pvp_in_the_spire.ui.Text;

import com.badlogic.gdx.Input;

import java.util.HashSet;

public class KeyHelper {

    public static HashSet<Integer> bannedKeys = null;

    public static void initKeys()
    {
        if(bannedKeys != null)
            return;
        bannedKeys = new HashSet<>();
        bannedKeys.add(Input.Keys.SHIFT_RIGHT);
        bannedKeys.add(Input.Keys.SHIFT_LEFT);
        bannedKeys.add(Input.Keys.CONTROL_LEFT);
        bannedKeys.add(Input.Keys.CONTROL_RIGHT);
        bannedKeys.add(Input.Keys.TAB);
        bannedKeys.add(Input.Keys.ENTER);
        bannedKeys.add(Input.Keys.ALT_LEFT);
        bannedKeys.add(Input.Keys.ALT_RIGHT);
        bannedKeys.add(Input.Keys.ESCAPE);
        bannedKeys.add(Input.Keys.BACKSPACE);
        bannedKeys.add(Input.Keys.LEFT);
        bannedKeys.add(Input.Keys.RIGHT);
        bannedKeys.add(Input.Keys.UP);
        bannedKeys.add(Input.Keys.DOWN);
    }

    //判断一个按键是否被允许



}
