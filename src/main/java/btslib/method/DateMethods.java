package btslib.method;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.chrono.IsoEra;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import btscore.graph.block.BlockMetadata;
import btscore.utils.DateTimeUtils;

/**
 *
 * @author JoostMeulenkamp
 */
public class DateMethods {

    @BlockMetadata(
            name = "Date",
            description = "Obtains an instance of LocalDate from a text string such as 2007-12-03.",
            identifier = "Date.fromString",
            category = "Core")
    public static LocalDate fromString(String value) {
        String pattern = DateTimeUtils.getDateFormat(value);
        if (pattern == null) {
            throw new DateTimeParseException("Process stopped, because the date format was unknown.", value, 0);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDate date = LocalDate.parse(value, formatter);
        return date;
    }

    @BlockMetadata(
            name = "now",
            description = "Obtains the current date from the system clock in the default time-zone.",
            identifier = "Date.now",
            category = "Core")
    public static LocalDate now() {
        return LocalDate.now();
    }

    @BlockMetadata(
            name = "DayOfWeek",
            description = "Gets the day-of-week field, which is an enum DayOfWeek.",
            identifier = "Date.getDayOfWeek",
            category = "Core")
    public static DayOfWeek getDayOfWeek(LocalDate date) {
        return date.getDayOfWeek();
    }

    @BlockMetadata(
            name = "DayOfYear",
            description = "Gets the day-of-year field.",
            identifier = "Date.getDayOfYear",
            category = "Core")
    public static int getDayOfYear(LocalDate date) {
        return date.getDayOfYear();
    }

    @BlockMetadata(
            name = "Era",
            description = "Gets the era applicable at this date.\nThe official ISO-8601 standard does not define eras, however IsoChronology does. It defines two eras, 'CE' from year one onwards and 'BCE' from year zero backwards. Since dates before the Julian-Gregorian cutover are not in line with history, the cutover between 'BCE' and 'CE' is also not aligned with the commonly used eras, often referred to using 'BC' and 'AD'.",
            identifier = "Date.getEra",
            category = "Core")
    public static IsoEra getEra(LocalDate date) {
        return date.getEra();
    }

    @BlockMetadata(
            name = "Month",
            description = "Gets the month-of-year field using the Month enum.",
            identifier = "Date.getMonth",
            category = "Core")
    public static Month getMonth(LocalDate date) {
        return date.getMonth();
    }

    @BlockMetadata(
            name = "MonthValue",
            description = "Gets the month-of-year field from 1 to 12.",
            identifier = "Date.getMonthValue",
            category = "Core")
    public static int getMonthValue(LocalDate date) {
        return date.getMonthValue();
    }

    @BlockMetadata(
            name = "Year",
            description = "Gets the year field.",
            identifier = "Date.getYear",
            category = "Core")
    public static int getYear(LocalDate date) {
        return date.getYear();
    }

    @BlockMetadata(
            name = "lengthOfMonth",
            description = "Returns the length of the month represented by this date.\nThis returns the length of the month in days. For example, a date in January would return 31.",
            identifier = "Date.lengthOfMonth",
            category = "Core")
    public static int lengthOfMonth(LocalDate date) {
        return date.lengthOfMonth();
    }

    @BlockMetadata(
            name = "lengthOfYear",
            description = "Returns the length of the year represented by this date.\nThis returns the length of the year in days, either 365 or 366.",
            identifier = "Date.lengthOfYear",
            category = "Core")
    public static int lengthOfYear(LocalDate date) {
        return date.lengthOfYear();
    }

    @BlockMetadata(
            name = "minus",
            description = "Returns a copy of this date with the specified amount subtracted.",
            identifier = "Date.minus",
            category = "Core")
    public static LocalDate minus(LocalDate date, long amountToSubstract, TemporalUnit unit) {
        return date.minus(amountToSubstract, unit);
    }

    @BlockMetadata(
            name = "minusDays",
            description = "Returns a copy of this date with the specified amount subtracted.",
            identifier = "Date.minusDays",
            category = "Core")
    public static LocalDate minusDays(LocalDate date, long daysToSubstract) {
        return date.minusDays(daysToSubstract);
    }

    @BlockMetadata(
            name = "minusMonths",
            description = "Returns a copy of this LocalDate with the specified number of months subtracted.",
            identifier = "Date.minusMonths",
            category = "Core")
    public static LocalDate minusMonths(LocalDate date, long monthsToSubstract) {
        return date.minusMonths(monthsToSubstract);
    }

    @BlockMetadata(
            name = "minusWeeks",
            description = "Returns a copy of this LocalDate with the specified number of weeks subtracted.",
            identifier = "Date.minusWeeks",
            category = "Core")
    public static LocalDate minusWeeks(LocalDate date, long weeksToSubstract) {
        return date.minusWeeks(weeksToSubstract);
    }

    @BlockMetadata(
            name = "minusYears",
            description = "Returns a copy of this LocalDate with the specified number of years subtracted.",
            identifier = "Date.minusYears",
            category = "Core")
    public static LocalDate minusYears(LocalDate date, long yearsToSubstract) {
        return date.minusYears(yearsToSubstract);
    }

    @BlockMetadata(
            name = "isLeapYear",
            description = "Checks if the year is a leap year, according to the ISO proleptic calendar system rules.",
            identifier = "Date.isLeapYear",
            category = "Core")
    public static boolean isLeapYear(LocalDate date) {
        return date.isLeapYear();
    }

    @BlockMetadata(
            name = "isBefore",
            description = "Checks if this date is before the specified date.",
            identifier = "Date.isBefore",
            category = "Core")
    public static boolean isBefore(LocalDate date, LocalDate other) {
        return date.isBefore(other);
    }

    @BlockMetadata(
            name = "isAfter",
            description = "Checks if this date is after the specified date.",
            identifier = "Date.isAfter",
            category = "Core")
    public static boolean isAfter(LocalDate date, LocalDate other) {
        return date.isAfter(other);
    }

    @BlockMetadata(
            name = "isEqual",
            description = "Checks if this date is equal to the specified date.",
            identifier = "Date.isEqual",
            category = "Core")
    public static boolean isEqual(LocalDate date, LocalDate other) {
        return date.isEqual(other);
    }

    @BlockMetadata(
            name = "daysBetween",
            description = "Calculates the amount of time between two dates.",
            identifier = "Date.daysBetween",
            category = "Core")
    public static long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }

}
