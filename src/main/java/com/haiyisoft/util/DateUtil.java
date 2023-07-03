package com.haiyisoft.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Chr.yl on 2023/7/3.
 *
 * @author Chr.yl
 */
public class DateUtil {
    //
    public static final String LOCAL_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    //
    public static final String LOCAL_DATE = "yyyy-MM-dd";

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

    public static void main(String[] args) {
        System.out.println(getLocalDate());
        System.out.println(getLocalDateTime());


    }

}
