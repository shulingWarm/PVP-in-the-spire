package pvp_in_the_spire.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

//多种input的处理器
//方便不同的控件订阅不同类型的处理操作
public class MultiInputProcessor implements InputProcessor {

    //鼠标相关的订阅
    InputProcessor mouseSubscriber;
    //滚动相关的订阅者
    InputProcessor scrollSubscriber;
    //订阅输入操作
    InputProcessor typeSubscriber;

    //订阅鼠标操作
    public void subscribeMouse(InputProcessor processor)
    {
        mouseSubscriber = processor;
    }

    //订阅滚动相关的操作
    public void subscribeScroll(InputProcessor processor)
    {
        scrollSubscriber = processor;
    }

    //订阅键盘输入的相关的操作
    public void subscribeKeyType(InputProcessor processor)
    {
        typeSubscriber = processor;
    }

    //订阅所有的信息
    public void subscribeAll(InputProcessor processor)
    {
        subscribeMouse(processor);
        subscribeScroll(processor);
        subscribeKeyType(processor);
    }

    @Override
    public boolean keyDown(int i) {
        if(typeSubscriber != null)
            return typeSubscriber.keyDown(i);
        return false;
    }

    @Override
    public boolean keyUp(int i) {
        if(typeSubscriber != null)
            return typeSubscriber.keyUp(i);
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        if(typeSubscriber != null)
            return typeSubscriber.keyTyped(c);
        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        if(mouseSubscriber != null)
            return touchDown(i,i1,i2,i3);
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        if(mouseSubscriber != null)
            return touchUp(i,i1,i2,i3);
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        if(mouseSubscriber != null)
            return touchDragged(i,i1,i2);
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        if(mouseSubscriber != null)
            return mouseMoved(i,i1);
        return false;
    }

    @Override
    public boolean scrolled(int i) {
        if(scrollSubscriber != null)
            return scrollSubscriber.scrolled(i);
        return false;
    }
}
