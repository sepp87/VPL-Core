package jo.vpl.block.methods;

import jo.vpl.core.BlockInfo;

/**
 *
 * @author joostmeulenkamp
 */
public class MathMethods {

    @BlockInfo(
            identifier = "Math.add",
            category = "Core",
            description = "")
    public static Number add(Number a, Number b) {
        Boolean areIntegers = areIntegers(a, b);
        if (areIntegers == null) {
            throw new IllegalArgumentException("Unsupported types for operation. Both inputs must be integers or doubles.");
        }
        if (areIntegers) {
            return a.longValue() + b.longValue();
        }
        return a.doubleValue() + b.doubleValue();
    }

    @BlockInfo(
            identifier = "Math.substract",
            category = "Core",
            description = "")
    public static Number substract(Number a, Number b) {
        Boolean areIntegers = areIntegers(a, b);
        if (areIntegers == null) {
            throw new IllegalArgumentException("Unsupported types for operation. Both inputs must be integers or doubles.");
        }
        if (areIntegers) {
            return a.longValue() - b.longValue();
        }
        return a.doubleValue() - b.doubleValue();
    }

    @BlockInfo(
            identifier = "Math.multiply",
            category = "Core",
            description = "")
    public static Number multiply(Number a, Number b) {
        Boolean areIntegers = areIntegers(a, b);
        if (areIntegers == null) {
            throw new IllegalArgumentException("Unsupported types for operation. Both inputs must be integers or doubles.");
        }
        if (areIntegers) {
            return a.longValue() * b.longValue();
        }
        return a.doubleValue() * b.doubleValue();
    }

    @BlockInfo(
            identifier = "Math.divide",
            category = "Core",
            description = "")
    public static Number divide(Number a, Number b) {
        return a.doubleValue() / b.doubleValue();
    }

    @BlockInfo(
            identifier = "Math.remainder",
            category = "Core",
            description = "")
    public static Number remainder(Number a, Number b) {
        Boolean areIntegers = areIntegers(a, b);
        if (areIntegers == null) {
            throw new IllegalArgumentException("Unsupported types for operation. Both inputs must be integers or doubles.");
        }
        if (areIntegers) {
            return a.longValue() % b.longValue();
        }
        return a.doubleValue() % b.doubleValue();
    }

    @BlockInfo(
            identifier = "Math.max",
            category = "Core",
            description = "")
    public static Number max(Number a, Number b) {
        Boolean areIntegers = areIntegers(a, b);
        if (areIntegers == null) {
            throw new IllegalArgumentException("Unsupported types for operation. Both inputs must be integers or doubles.");
        }
        if (areIntegers) {
            return Math.max((int) a, (int) b);
        }
        return Math.max((double) a, (double) b);
    }

    @BlockInfo(
            identifier = "Math.min",
            category = "Core",
            description = "")
    public static Number min(Number a, Number b) {
        Boolean areIntegers = areIntegers(a, b);
        if (areIntegers == null) {
            throw new IllegalArgumentException("Unsupported types for operation. Both inputs must be integers or doubles.");
        }
        if (areIntegers) {
            return Math.min((int) a, (int) b);
        }
        return Math.min((double) a, (double) b);
    }

    @BlockInfo(
            identifier = "Math.abs",
            category = "Core",
            description = "")
    public static Number abs(Number a) {
        Boolean isInteger = isInteger(a);
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
    public static Boolean isInteger(Number a) {
        if (a == null) {
            return null;
        }
        return a.doubleValue() % 1 == 0;
    }

    /**
     *
     * @param a
     * @param b
     * @return returns true when numbers are integer, false when numbers are
     * integer or double and returns null when they are none of both
     */
    public static Boolean areIntegers(Number a, Number b) {
        if (a == null || b == null) {
            return null;
        }
        Boolean isIntA = a.doubleValue() % 1 == 0;
        Boolean isIntB = b.doubleValue() % 1 == 0;

        return isIntA && isIntB;
    }
}
