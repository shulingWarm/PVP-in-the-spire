package pvp_in_the_spire.effect_transport;

//通用的特效转换器
public abstract class GeneralTransporter extends AbstractTransporter {

    public abstract boolean judgeFit(Class<?> clazz);
}
