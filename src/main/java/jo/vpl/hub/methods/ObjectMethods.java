package jo.vpl.hub.methods;

import jo.vpl.core.HubInfo;

/**
 *
 * @author joostmeulenkamp
 */
public class ObjectMethods {

    @HubInfo(
            identifier = "Object.getClass",
            category = "Core",
            description = "Returns the runtime class of this Object.")
    public static Class<?> getClass(Object object) {
        return object.getClass();
    }

    @HubInfo(
            identifier = "Object.toString",
            category = "Core",
            description = "Returns a string representation of the object.")
    public static String toString(Object object) {
        return object.toString();
    }

}
