package vpllib.method;

import java.util.List;
import vplcore.graph.block.BlockMetadata;

/**
 *
 * @author joostmeulenkamp
 */
public class StringMethods {

    @BlockMetadata(
            identifier = "String.length",
            category = "Core",
            description = "Returns the length of this string. The length is equal to the number of Unicode code units in the string.",
            tags = {"size"})
    public static Integer length(String string) {
        return string.length();
    }

    @BlockMetadata(
            identifier = "String.toUpperCase",
            category = "Core",
            description = "Converts all of the characters in this String to upper case using the rules of the default locale.",
            name = "a > A")
    public static String toUpperCase(String string) {
        return string.toUpperCase();
    }

    @BlockMetadata(
            identifier = "String.toLowerCase",
            category = "Core",
            description = "Converts all of the characters in this String to lower case using the rules of the default locale.",
            name = "A > a")
    public static String toLowerCase(String string) {
        return string.toLowerCase();
    }

    @BlockMetadata(
            identifier = "String.stripLeading",
            category = "Core",
            description = "Returns a string whose value is this string, with all leading white space removed.",
            name = "_ strip")
    public static String stripLeading(String string) {
        return string.stripLeading();
    }

    @BlockMetadata(
            identifier = "String.stripTrailing",
            category = "Core",
            description = "Returns a string whose value is this string, with all trailing white space removed.",
            name = "strip _")
    public static String stripTrailing(String string) {
        return string.stripTrailing();
    }

    @BlockMetadata(
            identifier = "String.strip",
            category = "Core",
            description = "Returns a string whose value is this string, with all leading and trailing white space removed.",
            name = "_ strip _")
    public static String strip(String string) {
        return string.strip();
    }

    @BlockMetadata(
            identifier = "String.substring",
            category = "Core",
            description = "Returns a string that is a substring of this string. The substring begins at the specified beginIndex and extends to the character at index endIndex - 1. Thus the length of the substring is endIndex-beginIndex.",
            name = "a > A")
    public static String substring(String string, Integer beginIndex, Integer endIndex) throws StringIndexOutOfBoundsException {
        beginIndex = beginIndex == null ? 0 : beginIndex;
        if (endIndex == null) {
            return string.substring(beginIndex);
        }
        return string.substring(beginIndex, endIndex);
    }

    @BlockMetadata(
            identifier = "String.contains",
            category = "Core",
            description = "Returns true if and only if this string contains the specified sequence of char values.",
            tags = {})
    public static Boolean contains(String string, String sequence) {
        return string.contains(sequence);
    }

    @BlockMetadata(
            identifier = "String.endsWith",
            category = "Core",
            description = "Tests if this string ends with the specified suffix.",
            tags = {})
    public static Boolean endsWith(String string, String suffix) {
        return string.endsWith(suffix);
    }

    @BlockMetadata(
            identifier = "String.startsWith",
            category = "Core",
            description = "Tests if the substring of this string beginning at the specified index starts with the specified prefix. By default the offset is set to zero.",
            tags = {})
    public static Boolean startWith(String string, String prefix, Integer offset) {
        offset = offset == null ? 0 : offset;
        return string.startsWith(prefix, offset);
    }

    @BlockMetadata(
            identifier = "String.equals",
            category = "Core",
            description = "Compares this string to the specified object. The result is true if and only if the argument is not null and is a String object that represents the same sequence of characters as this object.")
    public static Boolean equals(String a, String another) {
        return a.equals(another);
    }

    @BlockMetadata(
            identifier = "String.equalsIgnoreCase",
            category = "Core",
            description = "Compares this String to another String, ignoring case considerations. Two strings are considered equal ignoring case if they are of the same length and corresponding Unicode code points in the two strings are equal ignoring case.")
    public static boolean equalsIgnoreCase(String a, String another) {
        return a.equalsIgnoreCase(another);
    }

    @BlockMetadata(
            identifier = "String.indexOf",
            category = "Core",
            description = "Returns the index within this string of the first occurrence of the specified substring.")
    public static Integer indexOf(String string, String str) {
        return string.indexOf(str);
    }

    @BlockMetadata(
            identifier = "String.lastIndexOf",
            category = "Core",
            description = "Returns the index within this string of the last occurrence of the specified substring.")
    public static Integer lastIndexOf(String string, String str) {
        return string.lastIndexOf(str);
    }

    @BlockMetadata(
            identifier = "String.replace",
            category = "Core",
            description = "Replaces each substring of this string that matches the literal target sequence with the specified literal replacement sequence. The replacement proceeds from the beginning of the string to the end, for example, replacing \"aa\" with \"b\" in the string \"aaa\" will result in \"ba\" rather than \"ab\".")
    public static String replace(String string, String target, String replacement) {
        return string.replace(target, replacement);
    }

    @BlockMetadata(
            identifier = "String.replaceAll",
            category = "Core",
            description = "Replaces each substring of this string that matches the given regular expression with the given replacement.")
    public static String replaceAll(String string, String regex, String replacement) {
        return string.replaceAll(regex, replacement);
    }

    @BlockMetadata(
            identifier = "String.replaceFirst",
            category = "Core",
            description = "Replaces the first substring of this string that matches the given regular expression with the given replacement.")
    public static String replaceFirst(String string, String regex, String replacement) {
        return string.replaceFirst(regex, replacement);
    }

    @BlockMetadata(
            identifier = "String.concat",
            category = "Core",
            description = "Concatenates string b to the end of string a.")
    public static String concat(String a, String b) {
        return a.concat(b);
    }

    @BlockMetadata(
            identifier = "String.concatList",
            category = "Core",
            description = "Concatenates the list of string value into a single string")
    public static String concat(String[] list) {
        if (list.length == 0) {
            return null;
        }
        String result = "";
        for (String value : list) {
            result += value;
        }
        return result;
    }

    @BlockMetadata(
            identifier = "String.matches",
            category = "Core",
            description = "Tells whether or not this string matches the given regular expression.")
    public static Boolean matches(String value, String regex) {
        return value.matches(regex);
    }

    @BlockMetadata(
            identifier = "String.split",
            category = "Core",
            description = "Splits this string around matches of the given regular expression.")
    public static List<String> split(String value, String regex) {
        return List.of(value.split(regex));
    }

    @BlockMetadata(
            identifier = "String.isBlank",
            category = "Core",
            description = "Returns true if the string is empty or contains only white space codepoints, otherwise false.")
    public static Boolean isBlank(String value) {
        return value.isBlank();
    }

    @BlockMetadata(
            identifier = "String.isEmpty",
            category = "Core",
            description = "Returns true if, and only if, length() is 0.")
    public static Boolean isEmpty(String value) {
        return value.isEmpty();
    }

}
