package jo.vpl.hub.methods;

import jo.vpl.core.HubInfo;

/**
 *
 * @author joostmeulenkamp
 */
public class MathMethods {

    @HubInfo(
            identifier = "Math.add",
            category = "Core",
            description = "")
    public static Number add(Number a, Number b) {
        Boolean areIntegers = checkTypes(a, b);
        if (areIntegers == null) {
            throw new IllegalArgumentException("Unsupported types for operation. Both inputs must be integers or doubles.");
        }
        if (areIntegers) {
            return (int) a + (int) b;
        }
        return (double) a + (double) b;
    }

    @HubInfo(
            identifier = "Math.substract",
            category = "Core",
            description = "")
    public static Number substract(Number a, Number b) {
        Boolean areIntegers = checkTypes(a, b);
        if (areIntegers == null) {
            throw new IllegalArgumentException("Unsupported types for operation. Both inputs must be integers or doubles.");
        }
        if (areIntegers) {
            return (int) a - (int) b;
        }
        return (double) a - (double) b;
    }

    @HubInfo(
            identifier = "Math.multiply",
            category = "Core",
            description = "")
    public static Number multiply(Number a, Number b) {
        Boolean areIntegers = checkTypes(a, b);
        if (areIntegers == null) {
            throw new IllegalArgumentException("Unsupported types for operation. Both inputs must be integers or doubles.");
        }
        if (areIntegers) {
            return (int) a * (int) b;
        }
        return (double) a * (double) b;
    }

    @HubInfo(
            identifier = "Math.divide",
            category = "Core",
            description = "")
    public static Number divide(Number a, Number b) {
        Boolean areIntegers = checkTypes(a, b);
        if (areIntegers == null) {
            throw new IllegalArgumentException("Unsupported types for operation. Both inputs must be integers or doubles.");
        }
        if (areIntegers) {
            return (int) a / (int) b;
        }
        return (double) a / (double) b;
    }

    @HubInfo(
            identifier = "Math.remainder",
            category = "Core",
            description = "")
    public static Number remainder(Number a, Number b) {
        Boolean areIntegers = checkTypes(a, b);
        if (areIntegers == null) {
            throw new IllegalArgumentException("Unsupported types for operation. Both inputs must be integers or doubles.");
        }
        if (areIntegers) {
            return (int) a % (int) b;
        }
        return (double) a % (double) b;
    }

    @HubInfo(
            identifier = "Math.max",
            category = "Core",
            description = "")
    public static Number max(Number a, Number b) {
        Boolean areIntegers = checkTypes(a, b);
        if (areIntegers == null) {
            throw new IllegalArgumentException("Unsupported types for operation. Both inputs must be integers or doubles.");
        }
        if (areIntegers) {
            return Math.max((int) a, (int) b);
        }
        return Math.max((double) a, (double) b);
    }

    @HubInfo(
            identifier = "Math.min",
            category = "Core",
            description = "")
    public static Number min(Number a, Number b) {
        Boolean areIntegers = checkTypes(a, b);
        if (areIntegers == null) {
            throw new IllegalArgumentException("Unsupported types for operation. Both inputs must be integers or doubles.");
        }
        if (areIntegers) {
            return Math.min((int) a, (int) b);
        }
        return Math.min((double) a, (double) b);
    }

    @HubInfo(
            identifier = "Math.abs",
            category = "Core",
            description = "")
    public static Number abs(Number a) {
        Boolean isInteger = checkType(a);
        if (isInteger == null) {
            throw new IllegalArgumentException("Unsupported types for operation. Both inputs must be integers or doubles.");
        }
        if (isInteger) {
            return Math.abs((int) a);
        }
        return Math.abs((double) a);
    }

    /**
     *
     * @param a
     * @return returns true when numbers is integer, false when double and null
     * when none of both
     */
    public static Boolean checkType(Number a) {
        return a instanceof Integer ? true : (a instanceof Double ? false : null);
    }

    /**
     *
     * @param a
     * @param b
     * @return returns true when numbers are integer, false when numbers are
     * integer or double and returns null when they are none of both
     */
    public static Boolean checkTypes(Number a, Number b) {
        Boolean isIntA = a instanceof Integer;
        Boolean isIntB = b instanceof Integer;
        Boolean isDoubleA = a instanceof Double;
        Boolean isDoubleB = b instanceof Double;
        return (isIntA && isIntB) ? true : ((isIntA || isDoubleA) && (isIntB || isDoubleB)) ? false : null;
    }
}
