package com.propelleraero.gnsscompiler.compiler.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtils {

    public static String hourBlockChar(int hour) {
        hour += 1;
        return hour > 0 && hour <= 24 ? String.valueOf((char)(hour + 64)).toLowerCase() : null;
    }

    public static boolean isLeapYear(int year) {
        return ((year % 4 == 0) && (year % 100 != 0) || (year % 400 == 0));
    }

    public static LocalDateTime stringToDateTime(String dateTime) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
                .withZone(ZoneId.of("UTC"));

        return LocalDateTime.parse(dateTime, formatter);
    }
}
