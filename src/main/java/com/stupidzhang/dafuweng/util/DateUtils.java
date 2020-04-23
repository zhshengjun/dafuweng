package com.stupidzhang.dafuweng.util;

import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author hzchensiguang
 */
public class DateUtils {

    private static final String YYYY_MM_DD = "yyyy-MM-dd";
    private static final String YYYYMMDD = "yyyyMMdd";
    private static final String YYYY_MM = "yyyyMM";
    private static final String YYYY = "yyyy";
    private static final String YYYY_MM_DD_HH_MM_SS = "yyyyMMddHHmmss";
    private static final String HH_MM_SS = "HH:mm:ss";
    private static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";

    /**
     * java.util.Date类型转String，格式为 yyyy-MM-dd HH:mm
     */
    public static String formatYmdHmTime(Date date) {
        return formatDate(new SimpleDateFormat("yyyy-MM-dd HH:mm"), date);
    }

    /**
     * java.util.Date类型转String，格式为 yyyy-MM-dd HH:mm
     */
    public static String formatYmdHmsTime(Date date) {
        return formatDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), date);
    }
    public static String formatYmdDate(Date date) {
        return formatDate(new SimpleDateFormat(YYYYMMDD), date);
    }
    public static String formatYmdTime(Date date) {
        return formatDate(new SimpleDateFormat(YYYY_MM_DD), date);
    }

    public static String formatChineseDate(String date) {
        String[] ss = date.split("-");
        return ss[0] + "年" + ss[1] + "月" + ss[2] + "日";
    }

    private static String formatDate(SimpleDateFormat sdf, Date date) {
        if (date == null) {
            return null;
        }
        return sdf.format(date);
    }

    public static Date parseDateYmd(String date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 得到当前日期 YYYY-MM-DD
     */
    public static String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        Date date = c.getTime();
        SimpleDateFormat simple = new SimpleDateFormat(YYYY_MM_DD);
        return simple.format(date);
    }

    /**
     * 得到当前日期 YYYYMMDD
     */
    public static String getCurrentYmd() {
        Calendar c = Calendar.getInstance();
        Date date = c.getTime();
        SimpleDateFormat simple = new SimpleDateFormat(YYYYMMDD);
        return simple.format(date);
    }

    /**
     * 得到当前年
     */
    public static String getCurrentYear() {
        Calendar c = Calendar.getInstance();
        Date date = c.getTime();
        SimpleDateFormat simple = new SimpleDateFormat(YYYY);
        return simple.format(date);
    }

    public static String getCurrentYearIfInputYearIsNull(String year) {
        if (StringUtils.isEmpty(year)) {
            Calendar cal = Calendar.getInstance();
            return String.valueOf(cal.get(Calendar.YEAR));
        }
        return year;
    }

    /**
     * 得到当前年月
     */
    public static String getCurrentMonth() {
        Calendar c = Calendar.getInstance();
        Date date = c.getTime();
        SimpleDateFormat simple = new SimpleDateFormat(YYYY_MM);
        return simple.format(date);
    }

    /**
     * 得到当前时间
     */
    public static String getCurrentHms() {
        Calendar c = Calendar.getInstance();
        Date date = c.getTime();
        SimpleDateFormat simple = new SimpleDateFormat(HH_MM_SS);
        return simple.format(date);
    }

    /**
     * 获取当前日期
     */
    public static String getCurrentYmdHms() {
        Calendar c = Calendar.getInstance();
        Date date = c.getTime();
        SimpleDateFormat simple = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        return simple.format(date);
    }

    /**
     * 获取当前日期
     */
    public static String getCurrentYmdHmsTimes() {
        Calendar c = Calendar.getInstance();
        Date date = c.getTime();
        SimpleDateFormat simple = new SimpleDateFormat(YYYY_MM_DD_HH_MM);
        return simple.format(date);
    }

    /**
     * 获取当前时间
     */
    public static Date getNow() {
        return new Date();
    }

    /**
     * 获取指定日期前/后几天对应的日期
     * 往前 -num , 往后 num
     */
    public static Date getNextDay(Date date, Integer day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

    /**
     * 获取当前年份的上一年（当前2018年就返回2017年）
     */
    public static String getPreviousYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        SimpleDateFormat simple = new SimpleDateFormat(YYYY);
        return simple.format(calendar.getTime());
    }

    /**
     * 获取当前年份的下一年（当前2018年就返回2019年）
     */
    public static String getNextYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 1);
        SimpleDateFormat simple = new SimpleDateFormat(YYYY);
        return simple.format(calendar.getTime());
    }

    /**
     * 获取当前月第一天
     */
    public static String getCurrentMonthFirstDay() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        //设置为1号,当前日期既为本月第一天
        c.set(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat simple = new SimpleDateFormat(YYYY_MM_DD);
        return simple.format(c.getTime());
    }

    /**
     * 获取当前月最后一天
     */
    public static String getCurrentMonthLastDay() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        SimpleDateFormat simple = new SimpleDateFormat(YYYY_MM_DD);
        return simple.format(c.getTime());
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param startDate 较小的时间
     * @param endDate   较大的时间
     * @return 相差天数
     */
    public static Long daysBetween(Date startDate, Date endDate) {
        long startDateTime = startDate.getTime();
        long endDateTime = endDate.getTime();
        long dayTime = 24 * 60 * 60 * 1000;
        return (endDateTime - startDateTime) / dayTime;
    }

    /**
     * 字符串类型的日期取年月日
     * @param date 日期
     * @return yyyy-MM-dd
     */
    public static String getDateByString(String date) {
        if (StringUtils.isEmpty(date)) {
            return null;
        }
        return date.substring(0, 10);
    }

    /**
     * 计算两个日期之间相差的年数
     *
     * @param startDate 较小的时间
     * @param endDate   较大的时间
     * @return 相差年数
     */
    public static Integer yearBetween(Date startDate, Date endDate) {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);
        return endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
    }


    public static void main(String[] args) {
        String start = "2019-12-01";
        System.out.println(yearBetween(DateUtils.parseDateYmd(start),getNow()));
    }
}
