package WarlordEmblem.Screens.Buttons;

import WarlordEmblem.AutomaticSocketServer;
import WarlordEmblem.MultiSocketServer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import java.net.Socket;

//适用于多人连接的connect操作
public class MultiConnectButton extends ConnectButton {

    public MultiConnectButton(float x, float y, float width, float height,
                              BitmapFont font, StringBuilder inputResult)
    {
        super(x,y,width,height,font,inputResult);
    }

    @Override
    public void initGlobalServer(Socket socket) {
        AutomaticSocketServer.globalServer = new MultiSocketServer(socket);
    }
}
