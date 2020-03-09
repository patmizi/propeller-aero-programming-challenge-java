package com.propelleraero.gnsscompiler.compiler.utils;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static com.propelleraero.gnsscompiler.compiler.utils.DateUtils.hourBlockChar;
import static com.propelleraero.gnsscompiler.compiler.utils.DateUtils.isLeapYear;


public class DataSourceUtils {

    public static ArrayList<String> generateDataSourceRange(
            String baseStationId,
            LocalDateTime fromTime,
            LocalDateTime toTime,
            String dataSourceLocation,
            String fileLocationFormat,
            String fileNameFormat
            ) {
        ArrayList<String> dataSources = new ArrayList<>();

        int year = fromTime.getYear();
        int toYear = toTime.getYear();
        int day = fromTime.getDayOfYear(); // 1-365 or 366
        int toDay = toTime.getDayOfYear();
        int hour = fromTime.getHour();     // 0-23
        int toHour = toTime.getHour();

        while (year <= toYear &&
                (year < toYear || day <= toDay) &&
                (year < toYear || day < toDay || hour <= toHour)) {
            String fileName = String.format(fileNameFormat,
                    baseStationId,
                    padLeftZeros(String.valueOf(day), 3),
                    hourBlockChar(hour),
                    year % 100);
            String dataSource = String.format(fileLocationFormat,
                    dataSourceLocation,
                    year,
                    padLeftZeros(String.valueOf(day), 3),
                    baseStationId,
                    fileName
                    );
            dataSources.add(dataSource);

            hour += 1;
            if (hour > 23) {
                hour = 0;
                day += 1;
                if (day > 365) {
                    if (isLeapYear(year) && day > 366) {
                        day = 1;
                        year += 1;
                    } else if(!isLeapYear(year)) {
                        day = 1;
                        year += 1;
                    }
                }
            }
        }

        return dataSources;
    }

    private static String padLeftZeros(String str, int len) {
        if (str.length() >= len) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < len - str.length()) {
            sb.append('0');
        }
        sb.append(str);

        return sb.toString();
    }

}
