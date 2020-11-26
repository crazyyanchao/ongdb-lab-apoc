package data.lab.ongdb.util;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.util.DateHandle
 * @Description: TODO(Date handle class)
 * @date 2020/5/22 10:32
 */
public class DateHandle {

    private static final Map<String,Integer> GET_DAY_BY_MONTH_MAP = new HashMap<>();
    static {
        GET_DAY_BY_MONTH_MAP.put("true1",31);
        GET_DAY_BY_MONTH_MAP.put("true2",29);
        GET_DAY_BY_MONTH_MAP.put("true3",31);
        GET_DAY_BY_MONTH_MAP.put("true4",30);
        GET_DAY_BY_MONTH_MAP.put("true5",31);
        GET_DAY_BY_MONTH_MAP.put("true6",30);
        GET_DAY_BY_MONTH_MAP.put("true7",31);
        GET_DAY_BY_MONTH_MAP.put("true8",31);
        GET_DAY_BY_MONTH_MAP.put("true9",30);
        GET_DAY_BY_MONTH_MAP.put("true10",31);
        GET_DAY_BY_MONTH_MAP.put("true11",30);
        GET_DAY_BY_MONTH_MAP.put("true12",31);
        GET_DAY_BY_MONTH_MAP.put("false1",31);
        GET_DAY_BY_MONTH_MAP.put("false2",28);
        GET_DAY_BY_MONTH_MAP.put("false3",31);
        GET_DAY_BY_MONTH_MAP.put("false4",30);
        GET_DAY_BY_MONTH_MAP.put("false5",31);
        GET_DAY_BY_MONTH_MAP.put("false6",30);
        GET_DAY_BY_MONTH_MAP.put("false7",31);
        GET_DAY_BY_MONTH_MAP.put("false8",31);
        GET_DAY_BY_MONTH_MAP.put("false9",30);
        GET_DAY_BY_MONTH_MAP.put("false10",31);
        GET_DAY_BY_MONTH_MAP.put("false11",30);
        GET_DAY_BY_MONTH_MAP.put("false12",31);
    }

    /**
     * @param millisecond:日期的毫秒值
     * @return
     * @Description: TODO(毫秒转为日期)
     */
    public String millisecondToDate(long millisecond) {
        long time = Long.valueOf(millisecond);    //	list拿出的元素是毫秒
        Date d = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String start = sdf.format(d);    //	拿到最早的那天时间，然后每隔一天进行一次统计。传入参数时间/pnum/时间间隔+1
        return start;
    }

    /**
     * @param date:日期STRING格式:yyyy-MM-dd HH:mm:ss
     * @return
     * @Description: TODO(日期转为毫秒)
     */
    public long dateToMillisecond(String date) {
        long millisecond = 0;
        if (date != null && !"".equals(date)) {
            try {
                millisecond = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date).getTime();
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return millisecond;
    }

    /**
     * @param date1:日期一
     * @param date2:日期二
     * @return
     * @Description: TODO(日期做差取绝对值)
     */
    public long dateSubtract(String date1, String date2) {
        long date1Mill = dateToMillisecond(date1);
        long date2Mill = dateToMillisecond(date2);
        long sub = Math.abs(date1Mill - date2Mill);
        return sub;
    }

//    舍掉小数取整:Math.floor(3.5)=3
//    四舍五入取整:Math.rint(3.5)=4
//    进位取整:Math.ceil(3.1)=4
//    取绝对值：Math.abs(-3.5)=3.5
//    取余数：A%B = 余数

    /**
     * @param completeDate:完整日期格式，yyyy-MM-dd HH:mm:ss
     * @param interval:需要加的时间，比如当前时间加一小时就是   3600000
     * @return
     * @Description: TODO(当前年月日时分秒日期格式加 - 时间扩展)
     */
    public String datePlus(String completeDate, long interval) {
        String reDate = null;
        long currentDateMillisecond = 0;
        try {
            currentDateMillisecond = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(completeDate).getTime();
            long reMillisecond = currentDateMillisecond + interval;
            reDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(reMillisecond));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return reDate;
    }

    /**
     * @param completeDate:完整日期格式，yyyy-MM-dd HH:mm:ss
     * @param interval:需要减的时间，比如当前时间减一小时就是   3600000
     * @return
     * @Description: TODO(当前年月日时分秒日期格式加 - 时间扩展)
     */
    public String dateSub(String completeDate, long interval) {
        String reDate = null;
        long currentDateMillisecond = 0;
        try {
            currentDateMillisecond = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(completeDate).getTime();
            long reMillisecond = currentDateMillisecond - interval;
            reDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(reMillisecond));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return reDate;
    }

    /**
     * @param timeList:日期LIST 格式:yyyy-MM-dd HH:mm:ss
     * @return
     * @Description: TODO(列表中最小时间)
     */
    public String minListTime(List<String> timeList) {
        long min = Long.MAX_VALUE;
        long mill;
        String minTime;
        for (String time : timeList) {
            if (time != null) {
                mill = dateToMillisecond(time);
                if (mill < min) {
                    min = mill;
                }
            }
        }
        minTime = millisecondToDate(min);
        return minTime;
    }

    /**
     * @param timeList:日期LIST 格式:yyyy-MM-dd HH:mm:ss
     * @return
     * @Description: TODO(列表中最大时间)
     */
    public String maxListTime(List<String> timeList) {
        long max = Long.MIN_VALUE;
        long mill;
        String minTime;
        for (String time : timeList) {
            if (time != null) {
                mill = dateToMillisecond(time);
                if (mill > max) {
                    max = mill;
                }
            }
        }
        minTime = millisecondToDate(max);
        return minTime;
    }

    /**
     * @param
     * @return
     * @Description: TODO(Object转为日期格式 ， 成功返回true ， 失败返回false)
     */
    public boolean objectToDate(Object object) {
        if (object != null) {
            long millisecond = 0;
            try {
                millisecond = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(String.valueOf(object)).getTime();
                return true;
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                return false;
            }
        } else {
            return false;
        }
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
    private static void processingInvalidTime(long invalidTime, long currentDate,List<Long> dateList) {
        //标准时间
        StringBuffer standardTime = new StringBuffer("");
        //当前时间
        String currentDateStr = String.valueOf(currentDate);

        //-1和未来时间不处理
        if (invalidTime < 0){
            return;
        }
        //标准时间位数为14位，不足则补齐，多则截取
        //时间转字符
        String invalidTimeStr = String.valueOf(invalidTime);
        //相差长度  例：负数为需截取长度   正数为需拼接长度
        int differenceLength = 14-invalidTimeStr.length();
        //截取
        if(differenceLength < 0){
            invalidTimeStr = invalidTimeStr.substring(0, 14);
        }else if (differenceLength > 0){
            //拼接
            StringBuffer stringBuffer = new StringBuffer("");
            for (int i = 0; i < differenceLength; i++) {
                stringBuffer.append("0");
            }
            invalidTimeStr = invalidTimeStr+stringBuffer.toString();
        }
        //位数补齐后依次判断年月日时分秒是否有效
        //补全年
        String yearStr = invalidTimeStr.substring(0, 4);
        long year = Long.valueOf(yearStr);
        String currentYearStr = currentDateStr.substring(0, 4);
        long currentYear = Long.valueOf(currentYearStr);
        if(year >=1990 && year <= currentYear){
            standardTime.append(year);
        }else {
            standardTime.append(currentYearStr);
        }
        //补全月
        String monthStr = invalidTimeStr.substring(4, 6);
        long month = Long.valueOf(monthStr);
        if(month >= 1 && month <= 12){
            standardTime.append(month);
        }else {
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
        if(day >= 1 && day <= maxDay){
            standardTime.append(day);
        }else {
            standardTime.append(maxDay);
        }
        //补全时
        String timeStr = invalidTimeStr.substring(8, 10);
        long time = Long.valueOf(timeStr);
        if(month >= 0 && month <= 23){
            standardTime.append(time);
        }else {
            standardTime.append("00");
        }
        //补全分
        String minuteStr = invalidTimeStr.substring(10, 12);
        long minute = Long.valueOf(minuteStr);
        if(month >= 0 && month <= 59){
            standardTime.append(minute);
        }else {
            standardTime.append("00");
        }
        //补全秒
        String secondStr = invalidTimeStr.substring(12);
        long second = Long.valueOf(secondStr);
        if(month >= 0 && month <= 59){
            standardTime.append(second);
        }else {
            standardTime.append("00");
        }
        dateList.add(Long.valueOf(standardTime.toString()));
    }


    /**
     * @param
     * @return
     * @Description: TODO(Test main entrance)
     */
    public static void main(String[] args) {
        DateHandle dateHandle = new DateHandle();
        System.out.println(dateHandle.objectToDate("2016-07-04 17:21:00"));
    }
}


