package vpllib.method;

import java.time.LocalDate;

/**
 *
 * @author JoostMeulenkamp
 */
public class DateMethods {

    public static LocalDate now() {
        return LocalDate.now();
    }

    public static void getDayOfWeek(LocalDate date) {

    }

    public static void getDayOfYear(LocalDate date) {

    }

    public static void getEra(LocalDate date) {

    }

    public static void getMonth(LocalDate date) {

    }

    public static void getYear(LocalDate date) {

    }

    public static void lengthOfMonth(LocalDate date) {

    }

    public static void lengthOfYear(LocalDate date) {

    }

    public static void minus(LocalDate date) {
        
    }

    public static void minusDays(LocalDate date) {

    }

    public static void minusMonths(LocalDate date) {

    }

    public static void minusWeeks(LocalDate date) {

    }

    public static void minusYears(LocalDate date) {

    }

    public static boolean isLeapYear(LocalDate date) {
        return date.isLeapYear();
    }

    public static boolean isBefore(LocalDate date, LocalDate other) {
        return date.isBefore(other);
    }

    public static boolean isAfter(LocalDate date, LocalDate other) {
        return date.isAfter(other);
    }

    public static boolean isEqual(LocalDate date, LocalDate other) {
        return date.isEqual(other);
    }

}
