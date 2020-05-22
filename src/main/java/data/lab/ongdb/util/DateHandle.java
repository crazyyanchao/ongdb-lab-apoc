package data.lab.ongdb.util;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.util.DateHandle
 * @Description: TODO(Date handle class)
 * @date 2020/5/22 10:32
 */
public class DateHandle {
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
