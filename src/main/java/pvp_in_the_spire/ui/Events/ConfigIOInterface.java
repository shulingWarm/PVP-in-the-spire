package pvp_in_the_spire.ui.Events;

import java.io.DataInputStream;
import java.io.DataOutputStream;

//Config选项保存数据的接口
public interface ConfigIOInterface {

    //获取配置的名称
    public String getConfigName();

    public void saveConfig(DataOutputStream stream);

    public void loadConfig(DataInputStream stream);
}
