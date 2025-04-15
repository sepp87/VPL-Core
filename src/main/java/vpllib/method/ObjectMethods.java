package vpllib.method;

import vplcore.graph.block.BlockMetadata;

/**
 *
 * @author joostmeulenkamp
 */
public class ObjectMethods {

    @BlockMetadata(
            identifier = "Object.getClass",
            aliases = {"Object.getType"},
            category = "Core",
            description = "Returns the runtime class of this Object.")
    public static Class<?> getClass(Object object) {
        return object.getClass();
    }

    @BlockMetadata(
            identifier = "Object.toString",
            aliases = {"String.fromObject"},
            category = "Core",
            description = "Returns a string representation of the object.")
    public static String toString(Object object) {
        return object.toString();
    }

}
