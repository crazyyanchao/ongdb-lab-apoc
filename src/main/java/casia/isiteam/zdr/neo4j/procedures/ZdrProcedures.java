package casia.isiteam.zdr.neo4j.procedures;
/**
 * 　　　　　　　 ┏┓       ┏┓+ +
 * 　　　　　　　┏┛┻━━━━━━━┛┻┓ + +
 * 　　　　　　　┃　　　　　　 ┃
 * 　　　　　　　┃　　　━　　　┃ ++ + + +
 * 　　　　　　 █████━█████  ┃+
 * 　　　　　　　┃　　　　　　 ┃ +
 * 　　　　　　　┃　　　┻　　　┃
 * 　　　　　　　┃　　　　　　 ┃ + +
 * 　　　　　　　┗━━┓　　　 ┏━┛
 * ┃　　  ┃
 * 　　　　　　　　　┃　　  ┃ + + + +
 * 　　　　　　　　　┃　　　┃　Code is far away from     bug with the animal protecting
 * 　　　　　　　　　┃　　　┃ +
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃　　+
 * 　　　　　　　　　┃　 　 ┗━━━┓ + +
 * 　　　　　　　　　┃ 　　　　　┣┓
 * 　　　　　　　　　┃ 　　　　　┏┛
 * 　　　　　　　　　┗┓┓┏━━━┳┓┏┛ + + + +
 * 　　　　　　　　　 ┃┫┫　 ┃┫┫
 * 　　　　　　　　　 ┗┻┛　 ┗┻┛+ + + +
 */

import casia.isiteam.zdr.neo4j.util.DateHandle;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.logging.Log;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * @author YanchaoMa yanchaoma@foxmail.com
 * @PACKAGE_NAME: casia.isiteam.zdr.neo4j.procedures
 * @Description: TODO(执行查询的存储过程 / 函数)
 * @date 2018/8/3 17:54
 */
public class ZdrProcedures {

    /**
     * @param
     * @return
     * @Description: TODO(自定义函数 - 降序排序集合的元素)
     */
    @UserFunction(name = "zdr.apoc.sortDESC")
    public List<Object> sortDESC(@Name("coll") List<Object> coll) {
        List sorted = new ArrayList<>(coll);
        Collections.sort(sorted, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                Integer o1Int = Integer.valueOf(String.valueOf(o1));
                Integer o2Int = Integer.valueOf(String.valueOf(o2));
                return o2Int.compareTo(o1Int);
            }
        });
        return sorted;
    }

    /**
     * @param
     * @return
     * @Description: TODO(获取命中关键词关系的属性ids的长度 ， ids的值是用逗号分隔的id)
     */
    @UserFunction(name = "zdr.apoc.getEventIdsSize")
    public Number getEventIdsSize(@Name("ids") String ids) {
        String[] array = ids.split(",");
        int eventIdsSize = array.length;
        return eventIdsSize;
    }

    /**
     * @param
     * @return 1、符合时间区间 0、不符合时间区间
     * @Description: TODO(判断通联时间段是否匹配)
     */
    @UserFunction(name = "zdr.apoc.matchTimeZone")
    public Number matchTimeZone(@Name("mapPara") Map<String, String> mapPara) {

        String startTime = mapPara.get("startTime");
        String stopTime = mapPara.get("stopTime");

        DateHandle dateHandle = new DateHandle();
        long startTimeLong = dateHandle.dateToMillisecond(startTime);
        long stopTimeLong = dateHandle.dateToMillisecond(stopTime);

        String timeListString = mapPara.get("timeList");
        String[] timeArray = timeListString.split(",");
        String time;
        long timeLong;
        int size = timeArray.length;
        for (int i = 0; i < size; i++) {
            time = timeArray[i];
            timeLong = dateHandle.dateToMillisecond(time);
            if (startTimeLong <= timeLong && timeLong <= stopTimeLong) {
                return 1;   // 符合时间区间返回1
            }
        }
        return 0;   // 不符合时间区间返回0
    }

    /**
     * @param
     * @return
     * @Description: TODO(找出匹配的时间段)
     */
    @UserFunction(name = "zdr.apoc.matchTimeListString")
    public String matchTimeListString(@Name("mapPara") Map<String, String> mapPara) {
        String startTime = mapPara.get("startTime");
        String stopTime = mapPara.get("stopTime");

        DateHandle dateHandle = new DateHandle();
        long startTimeLong = dateHandle.dateToMillisecond(startTime);
        long stopTimeLong = dateHandle.dateToMillisecond(stopTime);

        String timeListString = mapPara.get("timeList");
        String[] timeArray = timeListString.split(",");
        StringBuilder builder = new StringBuilder();
        String time;
        long timeLong;
        int size = timeArray.length;
        for (int i = 0; i < size; i++) {
            time = timeArray[i];
            timeLong = dateHandle.dateToMillisecond(time);
            if (startTimeLong <= timeLong && timeLong <= stopTimeLong) {
                builder.append(time + ",");
            }
        }
        String timeList = null;
        if (builder != null && !"".equals(builder.toString())) {
            timeList = builder.substring(0, builder.toString().length() - 1);
        }
        return timeList;
    }

    /**
     * @param
     * @return
     * @Description: TODO(百分比映射)
     */
    @UserFunction(name = "zdr.apoc.scorePercentage")
    @Description("Set node influence score percentage")
    public Number percentageInfluenceScore(@Name("mapPara") Map<String, Object> mapPara) {

        double max = shiftDouble(mapPara.get("maxScore"));
        double min = shiftDouble(mapPara.get("minScore"));
        double current = shiftDouble(mapPara.get("currentScore"));

        // min-max标准化(Min-MaxNormalization)也称为离差标准化，是对原始数据的线性变换，使结果值映射到 [0 - 1] 之间
        double initialThreshold = 0.015;
        if (min <= current && current <= max && min != 0) {
            double percentage = (current - min) / (max - min);
            double percentageFormat = Double.parseDouble(String.format("%.6f", percentage));
            if (percentageFormat == 0) {
                return initialThreshold;
            }
            return percentageFormat;
        } else {
            return initialThreshold;
        }
    }

    /**
     * @param
     * @return
     * @Description: TODO(统一数据类型)
     */
    private double shiftDouble(Object dataObject) {
        if (dataObject instanceof Long) {
            Long data = (Long) dataObject;
            return data.doubleValue();

        } else if (dataObject instanceof Double) {
            return (double) dataObject;

        } else if (dataObject instanceof Integer) {
            Integer data = (Integer) dataObject;
            return data.doubleValue();

        } else if (dataObject instanceof Float) {
            Float data = (Float) dataObject;
            return data.doubleValue();
        } else {
            return 0;
        }
    }

    /**
     * @param
     * @return
     * @Description: TODO(小数点向后移动)
     */
    @UserFunction(name = "zdr.apoc.moveDecimalPoint")
    @Description("Move six decimal points")
    public Number moveDecimalPoint(@Name("mapPara") Map<String, Object> mapPara) {
        double scoreObject = shiftDouble(mapPara.get("scoreObject"));
        double moveLength = shiftDouble(mapPara.get("moveLength"));
        BigDecimal score = BigDecimal.valueOf(scoreObject);
        score = score.multiply(BigDecimal.valueOf(moveLength));
        BigInteger scoreInt = score.toBigInteger();
        return scoreInt.intValue();
    }

}
