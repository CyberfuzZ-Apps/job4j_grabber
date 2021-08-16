package ru.job4j.grabber.utils;

import java.time.LocalDateTime;
import java.util.Map;

public class SqlRuDateTimeParser implements DateTimeParser {

    private static final Map<String, Integer> MONTHS = Map.ofEntries(
            Map.entry("янв", 1),
            Map.entry("фев", 2),
            Map.entry("мар", 3),
            Map.entry("апр", 4),
            Map.entry("май", 5),
            Map.entry("июн", 6),
            Map.entry("июл", 7),
            Map.entry("авг", 8),
            Map.entry("сен", 9),
            Map.entry("окт", 10),
            Map.entry("ноя", 11),
            Map.entry("дек", 12)
    );

    @Override
    public LocalDateTime parse(String parse) {
        String[] dateTime = parse.split(",");
        if (dateTime.length != 2) {
            throw new IllegalArgumentException();
        }
        int day = 0;
        int month = 0;
        int year = 0;
        int hours = 0;
        int minutes = 0;
        String date = dateTime[0];
        String time = dateTime[1].substring(1);
        LocalDateTime today = LocalDateTime.now();
        if ("сегодня".equals(date)) {
            day = today.getDayOfMonth();
            month = today.getMonthValue();
            year = today.getYear();
        } else if ("вчера".equals(date)) {
            day = today.getDayOfMonth() - 1;
            month = today.getMonthValue();
            year = today.getYear();
        } else {
            String[] dayMonthYear = date.split(" ");
            day = Integer.parseInt(dayMonthYear[0]);
            month = MONTHS.get(dayMonthYear[1]);
            year = Integer.parseInt(dayMonthYear[2]);
            year = 2000 + year > today.getYear() ? 1900 + year : 2000 + year;
        }
        String[] times = time.split(":");
        hours = Integer.parseInt(times[0]);
        minutes = Integer.parseInt(times[1]);
        return LocalDateTime.of(year, month, day, hours, minutes);
    }
}
