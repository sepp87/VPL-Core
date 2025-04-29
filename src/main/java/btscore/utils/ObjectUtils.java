package btscore.utils;

/**
 *
 * @author joostmeulenkamp
 */
public class ObjectUtils {

    public static boolean compare(Object obj1, Object obj2) {
        if (obj1 == obj2) {
            return true; // both are the same reference or both are null
        }
        if (obj1 == null || obj2 == null) {
            return false; // only one is null
        }
        return obj1.equals(obj2); // both are non-null, use equals
    }
}
