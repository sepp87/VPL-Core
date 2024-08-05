package jo.vpl.hub.methods;

import jo.vpl.core.HubInfo;

/**
 *
 * @author joostmeulenkamp
 */
public class StringMethods {

    @HubInfo(
            name = "String.fromObject",
            category = "Core",
            description = "Returns a string representation of the object.",
            tags = {})
    public static String fromObject(Object object) {
        return object.toString();
    }

    @HubInfo(
            name = "String.length",
            category = "Core",
            description = "Returns the length of this string. The length is equal to the number of Unicode code units in the string.",
            tags = {"size"})
    public static Integer getLength(String string) {
        return string.length();
    }

    @HubInfo(
            name = "String.toUpperCase",
            category = "Core",
            description = "Converts all of the characters in this String to upper case using the rules of the default locale.",
            tags = {})
    public static String toUpperCase(String string) {
        return string.toUpperCase();
    }

    @HubInfo(
            name = "String.toLowerCase",
            category = "Core",
            description = "Converts all of the characters in this String to lower case using the rules of the default locale.",
            tags = {})
    public static String toLowerCase(String string) {
        return string.toLowerCase();
    }

    @HubInfo(
            name = "String.stripLeading",
            category = "Core",
            description = "Returns a string whose value is this string, with all leading white space removed.",
            tags = {})
    public static String stripLeading(String string) {
        return string.stripLeading();
    }

    @HubInfo(
            name = "String.stripTrailing",
            category = "Core",
            description = "Returns a string whose value is this string, with all trailing white space removed.",
            tags = {})
    public static String stripTrailing(String string) {
        return string.stripTrailing();
    }

    @HubInfo(
            name = "String.strip",
            category = "Core",
            description = "Returns a string whose value is this string, with all leading and trailing white space removed.",
            tags = {})
    public static String strip(String string) {
        return string.strip();
    }

    @HubInfo(
            name = "String.substring",
            category = "Core",
            description = "Returns a string that is a substring of this string. The substring begins at the specified beginIndex and extends to the character at index endIndex - 1. Thus the length of the substring is endIndex-beginIndex.",
            tags = {})
    public static String substring(String string, Integer beginIndex, Integer endIndex) throws StringIndexOutOfBoundsException {
        beginIndex = beginIndex == null ? 0 : beginIndex;
        if (endIndex == null) {
            return string.substring(beginIndex);
        }
        return string.substring(beginIndex, endIndex);
    }

    @HubInfo(
            name = "String.contains",
            category = "Core",
            description = "Returns true if and only if this string contains the specified sequence of char values.",
            tags = {})
    public static Boolean contains(String string, String sequence) {
        return string.contains(sequence);
    }

    @HubInfo(
            name = "String.endsWith",
            category = "Core",
            description = "Tests if this string ends with the specified suffix.",
            tags = {})
    public static Boolean endsWith(String string, String suffix) {
        return string.endsWith(suffix);
    }

    @HubInfo(
            name = "String.startsWith",
            category = "Core",
            description = "Tests if the substring of this string beginning at the specified index starts with the specified prefix. By default the offset is set to zero.",
            tags = {})
    public static Boolean startWith(String string, String prefix, Integer offset) {
        offset = offset == null ? 0 : offset;
        return string.startsWith(prefix, offset);
    }

    @HubInfo(
            name = "String.equals",
            category = "Core",
            description = "Compares this string to the specified object. The result is true if and only if the argument is not null and is a String object that represents the same sequence of characters as this object.",
            tags = {})
    public static Boolean equals(String string, String anotherString) {
        return string.equals(anotherString);
    }

    @HubInfo(
            name = "String.indexOf",
            category = "Core",
            description = "Returns the index within this string of the first occurrence of the specified substring.",
            tags = {})
    public static Integer indexOf(String string, String str) {
        return string.indexOf(str);
    }

    @HubInfo(
            name = "String.lastIndexOf",
            category = "Core",
            description = "Returns the index within this string of the last occurrence of the specified substring.",
            tags = {})
    public static Integer lastIndexOf(String string, String str) {
        return string.lastIndexOf(str);
    }

    @HubInfo(
            name = "String.replace",
            category = "Core",
            description = "Replaces each substring of this string that matches the literal target sequence with the specified literal replacement sequence. The replacement proceeds from the beginning of the string to the end, for example, replacing \"aa\" with \"b\" in the string \"aaa\" will result in \"ba\" rather than \"ab\".",
            tags = {})
    public static String replace(String string, String target, String replacement) {
        return string.replace(target, replacement);
    }

    @HubInfo(
            name = "String.replaceAll",
            category = "Core",
            description = "Replaces each substring of this string that matches the given regular expression with the given replacement.",
            tags = {})
    public static String replaceAll(String string, String regex, String replacement) {
        return string.replaceAll(regex, replacement);
    }

    @HubInfo(
            name = "String.replaceFirst",
            category = "Core",
            description = "Replaces the first substring of this string that matches the given regular expression with the given replacement.",
            tags = {})
    public static String replaceFirst(String string, String regex, String replacement) {
        return string.replaceFirst(regex, replacement);
    }
}
