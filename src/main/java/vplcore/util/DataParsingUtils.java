package vplcore.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joostmeulenkamp
 */
public class DataParsingUtils {

    

    /**
     * @param rawValue the string to check
     * @return a double when the string is a valid number, otherwise null.
     */
    public static Double getDoubleValue(String rawValue) {
        Double newValue = null;
        //http://stackoverflow.com/questions/3133770/how-to-find-out-if-the-value-contained-in-a-string-is-double-or-not
        String regExp = "[\\x00-\\x20]*[+-]?(((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*";
        boolean isDouble = rawValue.matches(regExp);

        if (isDouble) {
            newValue = Double.parseDouble(rawValue);
        }
        return newValue;
    }

    /**
     * @param rawValue the string to check
     * @return an integer when the string is a valid number, otherwise null.
     */
    public static Integer getIntegerValue(String rawValue) {
        Integer newValue = null;
        //http://stackoverflow.com/questions/16331423/whats-the-java-regular-expression-for-an-only-integer-numbers-string
//        String regExp = "^\\d+$";
        String regExp = "-?[0-9]{1,10}";
        boolean isInteger = rawValue.matches(regExp);

        if (isInteger) {
            newValue = Integer.parseInt(rawValue);
        }
        return newValue;
    }

    /**
     * @param rawValue the string to check
     * @return a long when the string is a valid number, otherwise null.
     */
    public static Long getLongValue(String rawValue) {
        Long newValue = null;

        //http://stackoverflow.com/questions/16331423/whats-the-java-regular-expression-for-an-only-integer-numbers-string
        String regExp = "-?[0-9]{1,19}";
        boolean isLong = rawValue.matches(regExp);

        if (isLong) {
            newValue = Long.parseLong(rawValue);
        }
        return newValue;
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
