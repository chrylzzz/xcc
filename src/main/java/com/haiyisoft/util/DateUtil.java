package com.haiyisoft.util;

import cn.hutool.core.date.DatePattern;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Created by Chr.yl on 2023/7/3.
 *
 * @author Chr.yl
 */
public class DateUtil {
    //
    public static final String LOCAL_DATE_TIME_MILL = DatePattern.NORM_DATETIME_MS_PATTERN;
    //
    public static final String LOCAL_DATE_TIME = DatePattern.NORM_DATETIME_PATTERN;
    //
    public static final String LOCAL_DATE = DatePattern.NORM_DATE_PATTERN;

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getLocalDateTime() {
        LocalDateTime now = LocalDateTime.now(); // 当前时间
        String format = now.format(DateTimeFormatter.ofPattern(LOCAL_DATE_TIME));
        return format;
    }

    /**
     * 获取当前日期
     *
     * @return
     */
    public static String getLocalDate() {
        LocalDate now = LocalDate.now(); // 当前时间
        String format = now.format(DateTimeFormatter.ofPattern(LOCAL_DATE));
        return format;
    }

    public void show() {
        //1.具有转换功能的对象
        DateTimeFormatter df = DateTimeFormatter.ofPattern(LOCAL_DATE_TIME);
//        //2.要转换的对象    
//        LocalDateTime time = LocalDateTime.now();
//        //3.发动功能
//        String localTime = df.format(time);
//        System.out.println("LocalDateTime转成String类型的时间：" + localTime);
//        //3.LocalDate发动，将字符串转换成  df格式的LocalDateTime对象，的功能
//        LocalDateTime LocalTime = LocalDateTime.parse(strLocalTime, df);
//        System.out.println("String类型的时间转成LocalDateTime：" + LocalTime);

    }

    public static void main(String[] args) throws ParseException {
//        System.out.println(getLocalDate());
//        System.out.println(getLocalDateTime());

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

        //查看error
//        LocalDateTime parseLocalDateTime2 = LocalDateTime.parse(aaa, DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_MILL));
//        System.out.println(parseLocalDateTime2);

        String substring = aaa.substring(0, aaa.length() - 4);
        System.out.println(substring);
        String substring2 = aaa.substring(0, 19);
        System.out.println(substring2);
    }

}
