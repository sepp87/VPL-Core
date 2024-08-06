package jo.vpl.hub.methods;

/**
 *
 * @author joostmeulenkamp
 */
public class MathMethods {

    public static Number add(Number a, Number b) {

        boolean isIntA = Integer.class.isAssignableFrom(a.getClass());
        boolean isIntB = Integer.class.isAssignableFrom(b.getClass());

        boolean isDoubleA = Double.class.isAssignableFrom(a.getClass());
        boolean isDoubleB = Double.class.isAssignableFrom(b.getClass());

        if (isIntA && isIntB) {
            return (int) a + (int) b;
        }

        if ((isIntA || isDoubleA) && (isIntB || isDoubleB)) {
            return (double) a + (double) b;
        }

        // If none of the above, throw an exception
        throw new IllegalArgumentException("Unsupported types for Add operation. Both inputs must be integers or doubles.");
    }

}
