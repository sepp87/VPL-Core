package vpllib.method;

import vplcore.graph.model.BlockInfo;

/**
 *
 * @author joostmeulenkamp
 */
public class ObjectMethods {

    @BlockInfo(
            identifier = "Object.getClass",
            category = "Core",
            description = "Returns the runtime class of this Object.")
    public static Class<?> getClass(Object object) {
        return object.getClass();
    }

    @BlockInfo(
            identifier = "Object.toString",
            category = "Core",
            description = "Returns a string representation of the object.")
    public static String toString(Object object) {
        return object.toString();
    }

}
