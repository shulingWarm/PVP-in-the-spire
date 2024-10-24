package pvp_in_the_spire.patches.connection;

import com.badlogic.gdx.InputProcessor;

//ip的地址输入处理器，抄的是
//com.megacrit.cardcrawl.helpers.TypeHelper
public class IpInputProcessor implements InputProcessor {

    //实际输入得到的字符串
    public StringBuilder inputResult;

    public IpInputProcessor() {

        inputResult = new StringBuilder();

    }

    public boolean keyDown(int keycode) {
        return false;
    }

    public boolean keyUp(int keycode) {
        return false;
    }

    public boolean keyTyped(char character) {
        //如果是回退键，执行回退然后退出
        if((int)character == 8)
        {
            if(inputResult.length()>0)
            {
                inputResult.deleteCharAt(inputResult.length()-1);
            }
            return true;
        }
        //如果输入已经超过了限制，就不用处理了
        if(inputResult.length()>=20)
            return false;
        String charStr = String.valueOf(character);
        if (charStr.length() != 1) {
            return false;
        } else {

            if (Character.isDigit(character) || character=='.' ||
                character==':') {
                inputResult.append(charStr);
            }

            return true;
        }
    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    public boolean scrolled(int amount) {
        return false;
    }

}
