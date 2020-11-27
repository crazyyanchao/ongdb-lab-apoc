package data.lab.ongdb.util;

/*
 *
 * Data Lab - graph database organization.
 *
 */

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: master.org.source.util.DateUtil
 * @Description: TODO(DATE UTIL)
 * @date 2020/5/18 16:17
 */
public class DateUtil {

    private static final long THRESHOLD_DATE_8 = 19000101L;
    private static final long THRESHOLD_DATE_14 = 19000101_01_01_01L;

    private static final Pattern PATTERN_MATCH_NUMBER = Pattern.compile("[0-9]");

    private static final Map<String, Integer> GET_DAY_BY_MONTH_MAP = new HashMap<>();

    static {
        GET_DAY_BY_MONTH_MAP.put("true1", 31);
        GET_DAY_BY_MONTH_MAP.put("true2", 29);
        GET_DAY_BY_MONTH_MAP.put("true3", 31);
        GET_DAY_BY_MONTH_MAP.put("true4", 30);
        GET_DAY_BY_MONTH_MAP.put("true5", 31);
        GET_DAY_BY_MONTH_MAP.put("true6", 30);
        GET_DAY_BY_MONTH_MAP.put("true7", 31);
        GET_DAY_BY_MONTH_MAP.put("true8", 31);
        GET_DAY_BY_MONTH_MAP.put("true9", 30);
        GET_DAY_BY_MONTH_MAP.put("true10", 31);
        GET_DAY_BY_MONTH_MAP.put("true11", 30);
        GET_DAY_BY_MONTH_MAP.put("true12", 31);
        GET_DAY_BY_MONTH_MAP.put("false1", 31);
        GET_DAY_BY_MONTH_MAP.put("false2", 28);
        GET_DAY_BY_MONTH_MAP.put("false3", 31);
        GET_DAY_BY_MONTH_MAP.put("false4", 30);
        GET_DAY_BY_MONTH_MAP.put("false5", 31);
        GET_DAY_BY_MONTH_MAP.put("false6", 30);
        GET_DAY_BY_MONTH_MAP.put("false7", 31);
        GET_DAY_BY_MONTH_MAP.put("false8", 31);
        GET_DAY_BY_MONTH_MAP.put("false9", 30);
        GET_DAY_BY_MONTH_MAP.put("false10", 31);
        GET_DAY_BY_MONTH_MAP.put("false11", 30);
        GET_DAY_BY_MONTH_MAP.put("false12", 31);
    }

    /**
     * @param timeMillis:毫秒时间
     * @return
     * @Description: TODO(毫秒转为时间字符串)
     */
    public static String millToTimeStr(long timeMillis) {
        Date d = new Date(timeMillis);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(d);
    }

    /**
     * @param date:日期STRING格式:yyyy-MM-dd HH:mm:ss
     * @return
     * @Description: TODO(日期转为毫秒)
     */
    public static long dateToMillisecond(String date) {
        long millisecond = 0;
        try {
            if (date != null && !"".equals(date)) {
                millisecond = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date).getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return millisecond;
    }

    /**
     * @param millisecond:日期的毫秒值
     * @return
     * @Description: TODO(毫秒转为日期)
     */
    public static String millisecondToDate(long millisecond) {
        //	list拿出的元素是毫秒
        long time = Long.valueOf(millisecond);
        Date d = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //	拿到最早的那天时间，然后每隔一天进行一次统计。传入参数时间/pnum/时间间隔+1
        String start = sdf.format(d);
        return start;
    }

    /**
     * @param
     * @return
     * @Description: TODO(Object转为日期格式 ， 成功返回true ， 失败返回false)
     */
    public static boolean objectToDate(Object object) {
        if (object != null) {
            try {
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(String.valueOf(object)).getTime();
                return true;
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                return false;
            }
        } else {
            return false;
        }
    }

    public static String getCurrentIndexTime() {
        Date d = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(d);
    }

    public static String getCurrentUpdate() {
        String dateTime = getCurrentIndexTime();
        return dateTime.replace("-", "").replace(" ", "").replace(":", "");
    }

    /**
     * @param
     * @return
     * @Description: TODO 【‘-1’表示解析日期失败】
     */
    public static long getCurrentUpdateLong() {
        try {
            String dateTime = getCurrentIndexTime();
            dateTime = dateTime.replace("-", "").replace(" ", "").replace(":", "");
            return Long.parseLong(dateTime);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return -1;
    }


    public static String format(String date) {
        return date.replace("-", "").replace(" ", "").replace(":", "");
    }

    /**
     * @param date:20200826225937【日期格式LONG】
     * @return
     * @Description: TODO(解析为带符号的时间 - 默认带有时分秒)
     */
    public static String format(long date) {
        StringBuilder builder = new StringBuilder();
        if (date > THRESHOLD_DATE_14) {
            char[] chars = String.valueOf(date).toCharArray();
            int length = chars.length;
            if (length == 14) {
                for (int i = 0; i < length; i++) {
                    builder.append(chars[i]);
                    if (i == 3 || i == 5) {
                        builder.append("-");
                    }
                    if (i == 7) {
                        builder.append(" ");
                    }
                    if (i == 9) {
                        builder.append(":");
                    }
                    if (i == 11) {
                        builder.append(":");
                    }
                }
            }
        }
        String dateStr = builder.toString();
        if ("".equals(dateStr)) {
            return "1900-01-01 12:30:00";
        }
        return dateStr;
    }

    /**
     * @param date:20200826225937【日期格式LONG】
     * @return
     * @Description: TODO(解析为带符号的时间 - 不带有时分秒)
     */
    public static String formatNotHMS(long date) {
        StringBuilder builder = new StringBuilder();
        if (date > THRESHOLD_DATE_8) {
            char[] chars = String.valueOf(date).toCharArray();
            int length = chars.length;
            int threshold = 8;
            if (length == threshold) {
                for (int i = 0; i < threshold; i++) {
                    builder.append(chars[i]);
                    if (i == 3 || i == 5) {
                        builder.append("-");
                    }
                }
            }
        }
        String dateStr = builder.toString();
        if ("".equals(dateStr)) {
            return "1900-01-01 01:01:00";
        }
        return dateStr;
    }

    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static String timestampToString(Timestamp timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(timestamp);
    }

    public static String datePlus(String completeDate, long interval) {
        String reDate = null;
        long currentDateMillisecond = 0L;

        try {
            currentDateMillisecond = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(completeDate).getTime();
            long reMillisecond = currentDateMillisecond + interval;
            reDate = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date(reMillisecond));
        } catch (ParseException var8) {
            var8.printStackTrace();
        }

        return reDate;
    }

    public static String dateSub(String completeDate, long interval) {
        String reDate = null;
        long currentDateMillisecond = 0L;

        try {
            currentDateMillisecond = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(completeDate).getTime();
            long reMillisecond = currentDateMillisecond - interval;
            reDate = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date(reMillisecond));
        } catch (ParseException var8) {
            var8.printStackTrace();
        }

        return reDate;
    }

    public static String handle(String establishmentDateName) {
        String format = establishmentDateName.split("\\.")[0];
        if (format.length() > 8) {
            return format.substring(0, 8);
        }
        return format;
    }

    /**
     * @param object:时间相关的对象
     * @param isStdDate:无效OBJECT是否默认补充系统时间
     * @return
     * @Description: TODO
     */
    public static long standardizeDate(Object object, boolean isStdDate) {
        long aLong = new Long(-1);
        if (object instanceof String) {
            Matcher m = PATTERN_MATCH_NUMBER.matcher((String) object);
            StringBuilder result = new StringBuilder();
            while (m.find()) {
                String r = m.group(0);
                result.append(r);
            }
            if (!"".equals(result.toString())) {
                aLong = Long.valueOf(result.toString());
            }
        } else if (object instanceof Long) {
            aLong = ((Long) object).longValue();
        } else if (object instanceof Integer) {
            aLong = ((Integer) object).intValue();
        } else if (object instanceof Date) {
            aLong = Long.valueOf(new SimpleDateFormat("YYYYMMddHHmmss").format((Date) object));
        }
        if (aLong > 0) {
            return processingInvalidTime(aLong);
        }
        return isStdDate(isStdDate);
    }

    private static long isStdDate(boolean isStdDate) {
        if (isStdDate) {
            return getCurrentDate();
        }
        return -1;
    }

    public static void main(String[] args) {
        long l = standardizeDate("2020-11-26T08:47:38", false);
        System.out.println(l);
    }

    //<6>、defineDate标准化处理【多字段选举的基础上再增加这个逻辑】【位数不一致的补齐为14位】
    //时间变量中部分有效的就保留，无效的做处理
    //暂定1900-当前年份算有效时间
    //对时间进行补全处理
    private static long processingInvalidTime(long invalidTime) {
        //标准时间
        StringBuffer standardTime = new StringBuffer("");
        //当前时间
        String currentDateStr = String.valueOf(getCurrentDate());

        //标准时间位数为14位，不足则补齐，多则截取
        //时间转字符
        String invalidTimeStr = String.valueOf(invalidTime);
        //相差长度  例：负数为需截取长度   正数为需拼接长度
        int differenceLength = 14 - invalidTimeStr.length();
        //截取
        if (differenceLength < 0) {
            invalidTimeStr = invalidTimeStr.substring(0, 14);
        } else if (differenceLength > 0) {
            //拼接
            StringBuffer stringBuffer = new StringBuffer("");
            for (int i = 0; i < differenceLength; i++) {
                stringBuffer.append("0");
            }
            invalidTimeStr = invalidTimeStr + stringBuffer.toString();
        }
        //位数补齐后依次判断年月日时分秒是否有效
        //补全年
        String yearStr = invalidTimeStr.substring(0, 4);
        long year = Long.valueOf(yearStr);
        String currentYearStr = currentDateStr.substring(0, 4);
        long currentYear = Long.valueOf(currentYearStr);
        if (year >= 1990 && year <= currentYear) {
            standardTime.append(yearStr);
        } else {
            standardTime.append(currentYearStr);
        }
        //补全月
        String monthStr = invalidTimeStr.substring(4, 6);
        long month = Long.valueOf(monthStr);
        if (month >= 1 && month <= 12) {
            standardTime.append(monthStr);
        } else {
            standardTime.append("12");
        }
        //补全日
        String dayStr = invalidTimeStr.substring(6, 8);
        long day = Long.valueOf(dayStr);
        //当前已补全的年
        String standardYearStr = standardTime.substring(0, 4);
        Long standardYear = Long.valueOf(standardYearStr);
        //当前已补全的月
        String standardMonthStr = standardTime.substring(4);
        Long standardMonth = Long.valueOf(standardMonthStr);
        boolean isLeapYear = standardYear % 4 == 0 && standardYear % 100 != 0;
        //当前已补全的年月所对应的日范围
        Integer maxDay = GET_DAY_BY_MONTH_MAP.get(isLeapYear + standardMonthStr);
        if (day >= 1 && day <= maxDay) {
            standardTime.append(dayStr);
        } else {
            standardTime.append(maxDay);
        }
        //补全时
        String timeStr = invalidTimeStr.substring(8, 10);
        long time = Long.valueOf(timeStr);
        if (time >= 0 && time <= 23) {
            standardTime.append(timeStr);
        } else {
            standardTime.append("00");
        }
        //补全分
        String minuteStr = invalidTimeStr.substring(10, 12);
        long minute = Long.valueOf(minuteStr);
        if (minute >= 0 && minute <= 59) {
            standardTime.append(minuteStr);
        } else {
            standardTime.append("00");
        }
        //补全秒
        String secondStr = invalidTimeStr.substring(12);
        long second = Long.valueOf(secondStr);
        if (second >= 0 && second <= 59) {
            standardTime.append(secondStr);
        } else {
            standardTime.append("00");
        }
        return Long.valueOf(standardTime.toString());
    }

    public static long getDefineDate(long releaseDate, long startDate, long endDate, long updateDate, long currentDate) {
        //<6>、defineDate标准化处理【多字段选举的基础上再增加这个逻辑】【位数不一致的补齐为14位】
        //时间变量中部分有效的就保留，无效的做处理
        //暂定1900-当前年份算有效时间
        List<Long> dateList = new ArrayList<>();
        //对时间进行补全处理
        processingInvalidTime(releaseDate, currentDate, dateList);
        processingInvalidTime(startDate, currentDate, dateList);
        processingInvalidTime(endDate, currentDate, dateList);
        processingInvalidTime(updateDate, currentDate, dateList);
        dateList.add(currentDate);
        return Collections.min(dateList);
    }

    //<6>、defineDate标准化处理【多字段选举的基础上再增加这个逻辑】【位数不一致的补齐为14位】
    //时间变量中部分有效的就保留，无效的做处理
    //暂定1900-当前年份算有效时间
    //对时间进行补全处理
    private static void processingInvalidTime(long invalidTime, long currentDate, List<Long> dateList) {
        //标准时间
        StringBuffer standardTime = new StringBuffer("");
        //当前时间
        String currentDateStr = String.valueOf(currentDate);

        //-1和未来时间不处理
        if (invalidTime < 0) {
            return;
        }
        //标准时间位数为14位，不足则补齐，多则截取
        //时间转字符
        String invalidTimeStr = String.valueOf(invalidTime);
        //相差长度  例：负数为需截取长度   正数为需拼接长度
        int differenceLength = 14 - invalidTimeStr.length();
        //截取
        if (differenceLength < 0) {
            invalidTimeStr = invalidTimeStr.substring(0, 14);
        } else if (differenceLength > 0) {
            //拼接
            StringBuffer stringBuffer = new StringBuffer("");
            for (int i = 0; i < differenceLength; i++) {
                stringBuffer.append("0");
            }
            invalidTimeStr = invalidTimeStr + stringBuffer.toString();
        }
        //位数补齐后依次判断年月日时分秒是否有效
        //补全年
        String yearStr = invalidTimeStr.substring(0, 4);
        long year = Long.valueOf(yearStr);
        String currentYearStr = currentDateStr.substring(0, 4);
        long currentYear = Long.valueOf(currentYearStr);
        if (year >= 1990 && year <= currentYear) {
            standardTime.append(year);
        } else {
            standardTime.append(currentYearStr);
        }
        //补全月
        String monthStr = invalidTimeStr.substring(4, 6);
        long month = Long.valueOf(monthStr);
        if (month >= 1 && month <= 12) {
            standardTime.append(month);
        } else {
            standardTime.append("12");
        }
        //补全日
        String dayStr = invalidTimeStr.substring(6, 8);
        long day = Long.valueOf(dayStr);
        //当前已补全的年
        String standardYearStr = standardTime.substring(0, 4);
        Long standardYear = Long.valueOf(standardYearStr);
        //当前已补全的月
        String standardMonthStr = standardTime.substring(4);
        Long standardMonth = Long.valueOf(standardMonthStr);
        boolean isLeapYear = standardYear % 4 == 0 && standardYear % 100 != 0;
        //当前已补全的年月所对应的日范围
        Integer maxDay = GET_DAY_BY_MONTH_MAP.get(isLeapYear + standardMonthStr);
        if (day >= 1 && day <= maxDay) {
            standardTime.append(day);
        } else {
            standardTime.append(maxDay);
        }
        //补全时
        String timeStr = invalidTimeStr.substring(8, 10);
        long time = Long.valueOf(timeStr);
        if (time >= 0 && time <= 23) {
            standardTime.append(timeStr);
        } else {
            standardTime.append("00");
        }
        //补全分
        String minuteStr = invalidTimeStr.substring(10, 12);
        long minute = Long.valueOf(minuteStr);
        if (minute >= 0 && minute <= 59) {
            standardTime.append(minuteStr);
        } else {
            standardTime.append("00");
        }
        //补全秒
        String secondStr = invalidTimeStr.substring(12);
        long second = Long.valueOf(secondStr);
        if (second >= 0 && second <= 59) {
            standardTime.append(secondStr);
        } else {
            standardTime.append("00");
        }
        dateList.add(Long.valueOf(standardTime.toString()));
    }

    public static long getCurrentDate() {
        return Long.valueOf(new SimpleDateFormat("YYYYMMddHHmmss").format(new Date()));
    }

    /*public static void main(String[] args) {
     *//*String str = "202001011230121";
        String substring = str.substring(4, 6);
        Long aLong = Long.valueOf(substring);
        System.out.println(aLong);*//*
        Long a = Long.valueOf("1200023112121");
        Long b = getCurrentDate();
        List<Long> aa = new ArrayList<>();
        processingInvalidTime(a,b,aa);
        for (Long time:aa) {
            System.out.println(time);
        }
    }*/
}
