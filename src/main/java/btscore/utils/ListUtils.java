package btscore.utils;

import java.util.List;

/**
 *
 * @author joostmeulenkamp
 */
public class ListUtils {

    public static boolean isList(Object o) {
        if (o == null) {
            return false;
        } else {
            return List.class.isAssignableFrom(o.getClass());
        }
    }
}
