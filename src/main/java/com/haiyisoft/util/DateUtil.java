package com.haiyisoft.util;

import cn.hutool.core.date.DatePattern;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Chr.yl on 2023/7/3.
 *
 * @author Chr.yl
 */
public class DateUtil {
    //
    public static final String LOCAL_DATE_TIME_MILL = DatePattern.NORM_DATETIME_MS_PATTERN;
    //
    public static final String LOCAL_DATE_TIME_MILL_ = "yyyy-MM-dd HH:mm:ss:SSS";
    //
    public static final String LOCAL_DATE_TIME = DatePattern.NORM_DATETIME_PATTERN;
    //
    public static final String LOCAL_DATE = DatePattern.NORM_DATE_PATTERN;

    /**
     * 获取当前时间
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getLocalDateTime() {
        LocalDateTime now = LocalDateTime.now(); // 当前时间
        String format = now.format(DateTimeFormatter.ofPattern(LOCAL_DATE_TIME));
        return format;
    }

    /**
     * 获取当前日期
     *
     * @return yyyy-MM-dd
     */
    public static String getLocalDate() {
        LocalDate now = LocalDate.now(); // 当前时间
        String format = now.format(DateTimeFormatter.ofPattern(LOCAL_DATE));
        return format;
    }

    /**
     * 时间转换
     *
     * @param dateStr 2023-07-03 18:13:59:919
     * @return 2023-07-03 18:13:59
     */
    public static String parseLocalDateTime(String dateStr) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_MILL_));
        String format = localDateTime.format(DateTimeFormatter.ofPattern(LOCAL_DATE_TIME));
        return format;
    }

    /**
     * 时间转换
     *
     * @param dateStr 2023-07-03 18:13:59.919
     * @return
     */
//    public static String parseLocalDateTime(String dateStr) {
//        LocalDateTime now = LocalDateTime.now(); // 当前时间
//        String format = now.format(DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_MILL));
//        return format;
//    }
    public static void main(String[] args) throws ParseException {
        System.out.println(getLocalDate());
        System.out.println(getLocalDateTime());

        LocalTime now = LocalTime.now();
        System.out.println("LocalTime:" + now);
        String aaa = "2023-07-03 18:13:59:919";
        System.out.println(aaa.length());

        String timeStr = "2022-03-30 10:23:32";
        LocalDateTime parseLocalDateTime = LocalDateTime.parse(timeStr, DateTimeFormatter.ofPattern(LOCAL_DATE_TIME));
        System.out.println(parseLocalDateTime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        LocalDateTime parse = LocalDateTime.parse("2020-05-29T07:51:33.106", formatter);
        System.out.println(parse);


        String substring = aaa.substring(0, aaa.length() - 4);
        System.out.println(substring);
        String substring2 = aaa.substring(0, 19);
        System.out.println(substring2);

        //查看error
        LocalDateTime parseLocalDateTime2 = LocalDateTime.parse(aaa, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS"));
        System.out.println(parseLocalDateTime2);
        //
        String s = parseLocalDateTime(aaa);
        System.out.println(s);


    }

}
