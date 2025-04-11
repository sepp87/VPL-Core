package vplcore.util;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 * @author joostmeulenkamp
 */
public class ParsingUtils {

    public static Object castToBestNumericType(Number value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Integer || value instanceof Short || value instanceof Byte) {
            return value.intValue();
        }

        if (value instanceof Long) {
            long l = value.longValue();
            if (l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE) {
                return (int) l;
            }
            return l;
        }

        if (value instanceof BigInteger) {
            BigInteger bigInt = (BigInteger) value;
            if (bigInt.bitLength() <= 31) {
                return bigInt.intValue();
            } else if (bigInt.bitLength() <= 63) {
                return bigInt.longValue();
            }
            return bigInt;
        }

        if (value instanceof Float || value instanceof Double) {
            double d = value.doubleValue();
            if (d % 1 == 0) {
                if (d >= Integer.MIN_VALUE && d <= Integer.MAX_VALUE) {
                    return (int) d;
                } else if (d >= Long.MIN_VALUE && d <= Long.MAX_VALUE) {
                    return (long) d;
                }
            }
            return d;
        }
        if (value instanceof BigDecimal) {
            BigDecimal bigDecimal = (BigDecimal) value;
            try {
                BigInteger unscaled = bigDecimal.toBigIntegerExact();
                // Check if it's actually an integer
                return castToBestNumericType(unscaled);
            } catch (ArithmeticException e) {
                // Not an exact integer, keep as BigDecimal
                return bigDecimal;
            }
        }

        // Fallback
        return value;
    }

    public static Object castToBestNumericTypeOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String integerRegex = "-?\\d+";
        if (value.matches(integerRegex)) {
            BigInteger bigInteger = new BigInteger(value);
            return castToBestNumericType(bigInteger);
        }

        // http://stackoverflow.com/questions/3133770/how-to-find-out-if-the-value-contained-in-a-string-is-double-or-not
        String doubleRegex = "[\\x00-\\x20]*[+-]?(((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*";
        if (value.matches(doubleRegex)) {
            try {
                Double doubleValue = Double.valueOf(value);
                if (!doubleValue.isInfinite()) {
                    return Double.valueOf(value);
                }
            } catch (NumberFormatException ignored) {
            }

            try {
                return new BigDecimal(value);
            } catch (NumberFormatException ignored) {
            }
        }

        return null;
    }

    /**
     * @param rawValue the string to check
     * @return a boolean when the string is a valid boolean, otherwise null.
     */
    public static Boolean getBooleanValue(String rawValue) {
        Boolean newValue = null;

        if (rawValue.toLowerCase().equals("true")) {
            newValue = true;
        } else if (rawValue.toLowerCase().equals("false")) {
            newValue = false;
        }

        return newValue;
    }

}
