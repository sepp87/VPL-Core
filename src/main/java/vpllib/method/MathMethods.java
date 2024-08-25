package vpllib.method;

import vplcore.graph.model.BlockInfo;

/**
 *
 * @author joostmeulenkamp
 */
public class MathMethods {

    @BlockInfo(
            identifier = "Math.pi",
            category = "Core",
            name = "Pi")
    public static double pi() {
        return Math.PI;
    }

    @BlockInfo(
            identifier = "Math.getCircleCircumference",
            category = "Core",
            name = "2*Pi*R")
    public static Double getCircleCircumference(Number r) {
        return 2 * Math.PI * r.doubleValue();
    }

    @BlockInfo(
            identifier = "Math.getCircleArea",
            category = "Core",
            name = "Pi*R^2")
    public static Double getCircleArea(Number r) {
        return Math.PI * Math.pow(r.doubleValue(), 2);
    }

    @BlockInfo(
            identifier = "Math.add",
            category = "Core")
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
            category = "Core")
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
            category = "Core")
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
            category = "Core")
    public static Number divide(Number a, Number b) {
        return a.doubleValue() / b.doubleValue();
    }

    @BlockInfo(
            identifier = "Math.remainder",
            category = "Core")
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
            category = "Core")
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
            category = "Core")
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
            category = "Core")
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
