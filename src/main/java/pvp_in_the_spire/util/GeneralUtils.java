package pvp_in_the_spire.util;

public class GeneralUtils {

    public static String removePrefix(String ID) {
        return ID.substring(ID.indexOf(":") + 1);
    }

}
