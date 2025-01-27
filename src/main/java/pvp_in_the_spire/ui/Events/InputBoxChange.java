package pvp_in_the_spire.ui.Events;

//输入框里面的内容修改时的回调
//一般是不需要处理的,开发这个东西特地用于卡牌设计的回调
public interface InputBoxChange {

    void onInputBoxChanged(String text);

}
