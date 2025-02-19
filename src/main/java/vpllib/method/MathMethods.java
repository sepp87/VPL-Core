package vpllib.method;

import vplcore.graph.block.BlockMetadata;

/**
 *
 * @author joostmeulenkamp
 */
public class MathMethods {

    @BlockMetadata(
            name = "pi",
            description = "The double value that is closer than any other to pi (π), the ratio of the circumference of a circle to its diameter.",
            identifier = "Math.pi",
            category = "Core")
    public static double pi() {
        return Math.PI;
    }

    @BlockMetadata(
            name = "2*pi*r",
            description = "The circumference of a circle with radius r.",
            identifier = "Math.getCircleCircumference",
            category = "Core")
    public static Double getCircleCircumference(Number r) {
        return 2 * Math.PI * r.doubleValue();
    }

    @BlockMetadata(
            name = "pi*r^2",
            description = "The area of a circle with radius r.",
            identifier = "Math.getCircleArea",
            category = "Core")
    public static Double getCircleArea(Number r) {
        return Math.PI * Math.pow(r.doubleValue(), 2);
    }

    @BlockMetadata(
            name = "a+b",
            description = "The result of adding value b to a.",
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

    @BlockMetadata(
            name = "a-b",
            description = "The result of substracting value b from a.",
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

    @BlockMetadata(
            name = "a*b",
            description = "The result of multiplying value a with b.",
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

    @BlockMetadata(
            name = "a/b",
            description = "The result of dividing value a by b.",
            identifier = "Math.divide",
            category = "Core")
    public static Number divide(Number a, Number b) {
        return a.doubleValue() / b.doubleValue();
    }

    @BlockMetadata(
            name = "a%b",
            description = "The remainder of dividing value a by b.",
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

    @BlockMetadata(
            description = "Returns the greater of two double values.",
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

    @BlockMetadata(
            description = "Returns the smaller of two double values.",
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

    @BlockMetadata(
            name = "|a|",
            description = "Returns the absolute value of a double value. If the argument is not negative, the argument is returned. If the argument is negative, the negation of the argument is returned.",
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
