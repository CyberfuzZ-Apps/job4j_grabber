package ru.job4j.grabber.utils;

import java.time.LocalDateTime;

public class SqlRuDateTimeParser implements DateTimeParser {

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
            String stringMonth = dayMonthYear[1];
            month = switch (stringMonth) {
                case "янв" -> 1;
                case "фев" -> 2;
                case "мар" -> 3;
                case "апр" -> 4;
                case "май" -> 5;
                case "июн" -> 6;
                case "июл" -> 7;
                case "авг" -> 8;
                case "сен" -> 9;
                case "окт" -> 10;
                case "ноя" -> 11;
                case "дек" -> 12;
                default -> throw new IllegalStateException(
                        "Unexpected value: " + stringMonth);
            };
            year = Integer.parseInt(dayMonthYear[2]);
            year = 2000 + year > today.getYear() ? 1900 + year : 2000 + year;
        }
        String[] times = time.split(":");
        hours = Integer.parseInt(times[0]);
        minutes = Integer.parseInt(times[1]);
        return LocalDateTime.of(year, month, day, hours, minutes);
    }
}
