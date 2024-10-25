package pvp_in_the_spire.screens.Buttons;

import pvp_in_the_spire.AutomaticSocketServer;
import pvp_in_the_spire.MultiSocketServer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

//多人通信情况下的button,这是新时代的通信形式
public class MultiServerButton extends AsServerButton {

    public MultiServerButton(
        float x, float y, float width, float height,
        BitmapFont font,
        StringBuilder ipText, //当发生点击事件的时候，会把这个字符串改成本机的ip
        ConnectButton connectButton //和这个按钮链接的表示连接对方的按钮
    )
    {
        super(x,y,width,height,font,ipText,connectButton);
    }

    //用于连接对方的服务器
    @Override
    public void prepareConnectTool(int idPort) {
        //直接初始化多用户的连接器
        AutomaticSocketServer.globalServer = new MultiSocketServer(idPort);
        //直接设置当前状态为连接成功
        callbackEvent.connectOk(true);
    }
}
