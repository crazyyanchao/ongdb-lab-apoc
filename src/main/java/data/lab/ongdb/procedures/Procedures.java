package data.lab.ongdb.procedures;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import data.lab.ongdb.common.SamplingType;
import data.lab.ongdb.common.Sort;
import data.lab.ongdb.result.NodeResult;
import data.lab.ongdb.util.DateHandle;
import data.lab.ongdb.util.NodeHandle;
import data.lab.ongdb.util.StringVerify;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.graphdb.Node;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.procedures.ZdrProcedures
 * @Description: TODO(执行查询的存储过程 / 函数)
 * @date 2020/5/22 10:28
 */
public class Procedures {

    /**
     * @param world:函数参数
     * @return
     * @Description: TODO(@ Description的内容会在Neo4j浏览器中调用dbms.functions () 时显示)
     */
    @UserFunction(name = "olab.hello") // 自定义函数名
    @Description("hello(world) - Say hello!")   // 函数说明
    public String hello(@Name("world") String world) {
        return String.format("Hello, %s", world);
    }

    /**
     * @param
     * @return
     * @Description: TODO(自定义函数 - 降序排序集合的元素)
     */
    @UserFunction(name = "olab.sortDESC")
    public List<Object> sortDESC(@Name("coll") List<Object> coll) {
        List sorted = new ArrayList<>(coll);
        Collections.sort(sorted, (o1, o2) -> {
            Integer o1Int = Integer.valueOf(String.valueOf(o1));
            Integer o2Int = Integer.valueOf(String.valueOf(o2));
            return o2Int.compareTo(o1Int);
        });
        return sorted;
    }

    /**
     * @param
     * @return
     * @Description: TODO(获取命中关键词关系的属性ids的长度 ， ids的值是用逗号分隔的id)
     */
    @UserFunction(name = "olab.getEventIdsSize")
    public Number getEventIdsSize(@Name("ids") String ids) {
        String[] array = ids.split(",");
        int eventIdsSize = array.length;
        return eventIdsSize;
    }

    /**
     * @param
     * @return
     * @Description: TODO(截取时间的年份)
     */
    @UserFunction(name = "olab.initAnnualTime")
    public long convertInitAnnualTime(@Name("startTime") String startTime) {
        if (startTime != null) {
            String[] array = startTime.split("-");
            if (array.length == 3) {
                return Long.valueOf(array[0]);
            } else {
                return 0;
            }
        }
        return 0;
    }

    /**
     * @param
     * @return 1、符合时间区间 0、不符合时间区间
     * @Description: TODO(判断通联时间段是否匹配)
     */
    @UserFunction(name = "olab.matchTimeZone")
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
    @UserFunction(name = "olab.matchTimeListString")
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
    @UserFunction(name = "olab.scorePercentage")
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
    @UserFunction(name = "olab.moveDecimalPoint")
    @Description("Move six decimal points")
    public Number moveDecimalPoint(@Name("mapPara") Map<String, Object> mapPara) {
        double scoreObject = shiftDouble(mapPara.get("scoreObject"));
        double moveLength = shiftDouble(mapPara.get("moveLength"));
        BigDecimal score = BigDecimal.valueOf(scoreObject);
        score = score.multiply(BigDecimal.valueOf(moveLength));
        BigInteger scoreInt = score.toBigInteger();
        return scoreInt.intValue();
    }

    /**
     * @param
     * @return
     * @Description: TODO(Present字符转换获取当前系统时间)
     */
    @UserFunction(name = "olab.presentStringToDate")
    @Description("Present-Convert date to relevant format")
    public String presentStringToDate(@Name("present") String present) {
        if ("Present".equals(present)) {
            DateHandle dateHandle = new DateHandle();
            return dateHandle.millisecondToDate(System.currentTimeMillis());
        }
        return present;
    }

    /**
     * @param
     * @return
     * @Description: TODO(判断两个时间区间是否有交叉)
     */
    @UserFunction(name = "olab.timeCrossOrNot")
    @Description("Time zone cross or not")
    public boolean timeCrossOrNot(@Name("mapPara") Map<String, Object> mapPara) {

        DateHandle dateHandle = new DateHandle();
        String r1Start = (String) mapPara.get("r1Start");
        String r1Stop = (String) mapPara.get("r1Stop");
        String r2Start = (String) mapPara.get("r2Start");
        String r2Stop = (String) mapPara.get("r2Stop");

        if ((dateHandle.objectToDate(r1Start) || dateHandle.objectToDate(r1Stop)) && (dateHandle.objectToDate(r2Start)
                || dateHandle.objectToDate(r2Stop))) {

            long r1StartMill = dateHandle.dateToMillisecond(r1Start);
            long r1StopMill = dateHandle.dateToMillisecond(r1Stop);
            long r2StartMill = dateHandle.dateToMillisecond(r2Start);
            long r2StopMill = dateHandle.dateToMillisecond(r2Stop);

            // 不可能交叉的情况：
            // 1、区间一的结束时间小于区间二的开始时间
            // 2、区间一的开始时间大于区间二的结束时间

            if ((r1StartMill > r2StopMill && r1StartMill != 0 && r2StopMill != 0)
                    || (r1StopMill < r2StartMill && r1StopMill != 0 && r2StartMill != 0)
                    || (r1StartMill > r2StartMill && r2StopMill == 0)
                    || (r1StartMill < r2StartMill && r1StopMill == 0)) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * @param
     * @return
     * @Description: TODO(是否包含某字符串 | | - 任意包含一个 & & - 全部包含)
     */
    @UserFunction(name = "olab.isContainsString")
    @Description("Is contains string? &&-All contains ||-Or contains (Chinese||English Chinese&&English)")
    public boolean isContainsString(@Name("mapPara") Map<String, Object> mapPara) {

        // 将输入拼接成一个STRING
        String original = removeNull(mapPara.get("original0")) + removeNull(mapPara.get("original1")) + removeNull(mapPara.get("original2")) +
                removeNull(mapPara.get("original3")) + removeNull(mapPara.get("original4")) + removeNull(mapPara.get("original5")) + removeNull(mapPara.get("original6")) +
                removeNull(mapPara.get("original7")) + removeNull(mapPara.get("original8")) + removeNull(mapPara.get("original9"));
        String input = (String) mapPara.get("input");

        if (original != null && !"".equals(original)) {
            String[] split;
            if (input.contains("||")) {
                split = input.split("\\|\\|");
                return Arrays.stream(split).parallel().anyMatch(v -> original.contains(v));
            } else if (input.contains("&&")) {
                split = input.split("&&");
                return Arrays.stream(split).parallel().allMatch(v -> original.contains(v));
            } else if (original.contains(input)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param
     * @return
     * @Description: TODO(清理NULL值设置为空字符串)
     */
    private String removeNull(Object object) {
        if (object == null) {
            return "";
        }
        return String.valueOf(object);
    }

    /**
     * @param
     * @return
     * @Description: TODO(统计字符串中包含某个字符的数量)
     */
    @UserFunction(name = "olab.stringCharCount")
    @Description("Count char in string")
    public long stringCharCount(@Name("mapPara") Map<String, Object> mapPara) {
        return StringUtils.countMatches((String) mapPara.get("original"), (String) mapPara.get("char"));
    }

    /**
     * @param nLabels:节点集合
     * @param mLabels:节点集合
     * @param strictLabels:标签 分隔符号（||）
     * @return 两个集合同时包含某一个标签 返回TRUE
     * @Description: TODO(两个集合同时包含某一个标签)
     */
    @UserFunction(name = "olab.relatCalculateRestrict")
    @Description("Graph relationships calculate restrict")
    public boolean relatCalculateRestrict(@Name("nLabels") List<String> nLabels, @Name("mLabels") List<String> mLabels, @Name("restrictLabels") String strictLabels) {

        // ||包含其中一个
        if (strictLabels.contains("||")) {
            String[] strict = strictLabels.split("\\|\\|");
            for (int i = 0; i < strict.length; i++) {
                String label = strict[i];
                if (nLabels.contains(label) && mLabels.contains(label)) {
                    return true;
                }
            }
        } else {
            if (nLabels.contains(strictLabels) && mLabels.contains(strictLabels)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断字符串中是否包含中文
     *
     * @param node:节点的所有属性中是否包含中文
     * @return 是否为中文
     * @warn 不能校验是否为中文标点符号
     */
    @UserFunction(name = "olab.isContainChinese")
    @Description("Node is contains chinese or not")
    public long isContainChinese(@Name("node") Node node) {

        Iterable<String> iterableKeys = node.getPropertyKeys();
        StringBuilder nodeValueBuilder = new StringBuilder();
        for (Iterator iterator = iterableKeys.iterator(); iterator.hasNext(); ) {
            Object next = iterator.next();
            Object nodeValue = node.getProperty((String) next);
            if (nodeValue instanceof String) {
                nodeValueBuilder.append(nodeValue);
            }
        }
        // 所有节点属性value
        char[] nodeValueChar = nodeValueBuilder.toString().toCharArray();

        int chineseCharCount = 0;
        StringVerify chineseVerify = new StringVerify();
        for (int i = 0; i < nodeValueChar.length; i++) {
            char c = nodeValueChar[i];
            if (chineseVerify.isContainChinese(String.valueOf(c))) {
                chineseCharCount++;
            }
        }
        return chineseCharCount;
    }

    /**
     * @param
     * @return
     * @Description: TODO(节点是否包含权限)
     */
    @UserFunction(name = "olab.isContainAuthority")
    @Description("Node is contains authority or not")
    public boolean isContainAuthority(@Name("node") Node node) {
        Iterable<String> iterableKeys = node.getPropertyKeys();

        String prefix = "sysuser_id_";
        for (Iterator iterator = iterableKeys.iterator(); iterator.hasNext(); ) {
            String key = (String) iterator.next();
            if (key.contains(prefix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param
     * @return
     * @Description: TODO(去重合并多个节点列表)
     */
    @UserFunction(name = "olab.mergeNodes")
    @Description("Merge node list")
    public List<Node> mergeNodes(@Name("nodePackArray") List<List<Node>> nodePackArray) {

        List<Node> nodes = new ArrayList<>();

        for (int i = 0; i < nodePackArray.size(); i++) {
            List<Node> nodeList = nodePackArray.get(i);
            nodes.addAll(nodeList);
        }
        NodeHandle nodeHandle = new NodeHandle();

        return nodeHandle.distinctNodes(nodes);
    }

    /**
     * @param
     * @return
     * @Description: TODO(地理位置名称多字段检索 -)
     */
    @UserFunction(name = "olab.locMultiFieldsFullTextSearchCondition")
    @Description("Location multi fields search- 找共同居住地的人 - EXAMPLE:location:`\"+location+\"`* OR location:`\"+location+\"`*")
    public String locMultiFieldsFullTextSearchCondition(@Name("node") Node node, @Name("locMultiFields") List<String> locMultiFields) {

        StringBuilder builder = new StringBuilder();
        Map<String, Object> mapProperties = node.getAllProperties();
        locMultiFields.forEach(field -> {
            if (!"".equals(field) && field != null) {
                if (mapProperties.containsKey(field)) {
                    Object value = node.getProperty(field);
                    if (value instanceof String) {
                        if (value != null && !"".equals(value)) {
                            builder.append(field + ":`" + value + "`* OR ");
                        }
                    }
                }
            }
        });
        if (builder != null && !"".equals(builder.toString())) {
            return builder.substring(0, builder.length() - 3);
        }
        return "`null`";
    }

    //    olab.nodeIsContainsKey

    /**
     * @param
     * @return
     * @Description: TODO(节点是否包含某个KEY - 多个中的任意一个)
     */
    @UserFunction(name = "olab.nodeIsContainsKey")
    @Description("Node is contain key or not")
    public boolean nodeIsContainsKey(@Name("node") Node node, @Name("locMultiFields") List<String> locMultiFields) {
        Map<String, Object> mapProperties = node.getAllProperties();
        for (int i = 0; i < locMultiFields.size(); i++) {
            String field = locMultiFields.get(i);
            if (mapProperties.containsKey(field)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param
     * @return
     * @Description: TODO(节点转换)
     */
    private List<NodeResult> transformNodes(List<Node> nodes) {
        return nodes.stream().map(node -> new NodeResult(node))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * @param string:原始字符串
     * @param regexp:正则串
     * @return
     * @Description: TODO(用正则串过滤字段值 ， 并返回过滤之后的VALUE ； 保留空格)
     */
    @UserFunction(name = "olab.replace.regexp")
    @Description("Replace string by regexp")
    public String replaceRegexp(@Name("string") String string, @Name("regexp") String regexp) {
        Pattern pattern = Pattern.compile(regexp);
        Matcher m = pattern.matcher(string);
        return m.replaceAll(" ").trim();
    }

    /**
     * @param
     * @return
     * @Description: TODO(节点ID集合移除某些节点)
     */
    @UserFunction(name = "olab.removeIdsFromRawList")
    @Description("Remove ids from raw node id list")
    public List<Long> removeIdsFromRawList(@Name("rawIDs") List<Long> rawIDs, @Name("ids") List<Long> ids) {
        if (rawIDs != null && ids != null) {
            rawIDs.removeAll(ids);
            return rawIDs;
        }
        return rawIDs;
    }

    /**
     * @param jsonString:JSON-STRING
     * @param keyFields:排重字段
     * @return
     * @Description: TODO(对存列表的属性字段进行排重 【 字段存储JSON列表对象 】 【 返回排重后的数据 】)
     */
    @UserFunction(name = "olab.remove.duplicate")
    @Description("RETURN olab.remove.duplicate({jsonString},{keyFields})")
    public String removeDuplicate(@Name("jsonString") String jsonString, @Name("keyFields") List<String> keyFields) {
        JSONArray objects = JSONArray.parseArray(jsonString)
                .stream()
                .filter(distinctByKey(v -> {
                    JSONObject object = (JSONObject) v;
                    return packObject(object, keyFields);
                }))
                .collect(Collectors.toCollection(JSONArray::new));
        return objects.toJSONString();
    }

    private Object packObject(JSONObject object, List<String> keyFields) {
        JSONObject jsonObject = new JSONObject();
        for (String key : keyFields) {
            jsonObject.put(key, object.get(key));
        }
        return jsonObject;
    }

    static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    /**
     * @param jsonString:JSON        STRING
     * @param sortField:排序字段
     * @param returnSize:返回的Object数量
     * @return
     * @Description: TODO(解析JSONArray ， 通过传入字段排序array ， 并返回前N个结果)
     */
    @UserFunction(name = "olab.sort.jsonArray")
    @Description("RETURN olab.sort.jsonArray(rawJson)")
    public String sortJsonArray(@Name("jsonString") String jsonString, @Name("sortField") String sortField, @Name("sort") String sort, @Name("returnSize") Number returnSize) {
        JSONArray array = JSONArray.parseArray(jsonString)
                .stream()
                .sorted((v1, v2) -> {
                    JSONObject object1 = (JSONObject) v1;
                    JSONObject object2 = (JSONObject) v2;
                    Long l1 = object1.getLongValue(sortField);
                    Long l2 = object2.getLongValue(sortField);
                    if (Sort.ASC.name().equals(sort)) {
                        return l1.compareTo(l2);
                    } else {
                        return l2.compareTo(l1);
                    }
                })
                .collect(Collectors.toCollection(JSONArray::new));
        List<Object> returnList = array.subList(0, Math.min(returnSize.intValue(), array.size()));
        JSONArray returnArray = JSONArray.parseArray(JSON.toJSONString(returnList));
        return returnArray.toJSONString();
    }

    /**
     * @param jsonString:JSON                   STRING
     * @param samplingType:采样类型:YRE-有放回，NRE-无放回
     * @param samplingSize:采样尺寸
     * @return
     * @Description: TODO(解析JSONArray, 进行采样)
     */
    @UserFunction(name = "olab.sampling.jsonArray")
    @Description("RETURN olab.sampling.jsonArray(rawJson)")
    public String samplingJsonArray(@Name("jsonString") String jsonString, @Name("samplingType") String samplingType, @Name("samplingSize") Number samplingSize) {
        JSONArray rawJsonArray = JSONArray.parseArray(jsonString);
        // 有放回
        if (SamplingType.YRE.name().equals(samplingType)) {
            return samplingJsonArrayYRE(rawJsonArray, samplingSize.intValue()).toJSONString();
            // 无放回
        } else {
            return samplingJsonArrayNRE(rawJsonArray, samplingSize.intValue()).toJSONString();
        }
    }

    /**
     * @param
     * @return
     * @Description: TODO(有放回采样)
     */
    private JSONArray samplingJsonArrayYRE(JSONArray rawJsonArray, int samplingSize) {
        JSONArray samplingArray = new JSONArray();
        Random random = new Random();
        // < 【不包含max】
        int max = rawJsonArray.size();
        // >=
        int min = 0;

        int size = samplingArray.size();
        while (size < samplingSize) {
            int index = random.nextInt(max - min) + min;
            Object object = rawJsonArray.get(index);

            if (object instanceof JSONObject) {
                /**
                 * 关闭循环引用检测
                 * **/
                String str = JSON.toJSONString(object, SerializerFeature.DisableCircularReferenceDetect);
                JSONObject jsonObject = JSON.parseObject(str);
                samplingArray.add(jsonObject);
            } else {
                samplingArray.add(object);
            }
            size++;
        }
        return samplingArray;
    }

    /**
     * @param
     * @return
     * @Description: TODO(无放回采样)
     */
    private JSONArray samplingJsonArrayNRE(JSONArray rawJsonArray, int samplingSize) {
        JSONArray samplingArray = new JSONArray();
        List<Integer> indexList = new ArrayList<>();
        Random random = new Random();
        // < 【不包含max】
        int max = rawJsonArray.size();
        // >=
        int min = 0;

        int size = samplingArray.size();
        while (size < samplingSize) {
            int index = random.nextInt(max - min) + min;
            if (!indexList.contains(index)) {
                indexList.add(index);
                samplingArray.add(rawJsonArray.get(index));
                size++;
            }
            if (max == samplingArray.size()) {
                break;
            }
        }
        return samplingArray;
    }

    /**
     * @param jsonString:JSON-STRING
     * @param dateValue:20201020235959
     * @param dateField:日期字段
     * @return
     * @Description: TODO(解析JSONArray， 从列表中选举距离当前时间最近的对象)
     */
    @UserFunction(name = "olab.samplingByDate.jsonArray")
    @Description("RETURN olab.samplingByDate.jsonArray(rawJson)")
    public String samplingByDateJsonArray(@Name("jsonString") String jsonString,  @Name("dateField") String dateField,@Name("dateValue") Long dateValue) {
        return JSONArray.parseArray(jsonString)
                .stream()
                // 过滤出包含dateField的OBJECT
                .filter(v -> {
                    JSONObject object = (JSONObject) v;
                    return object.containsKey(dateField);
                })
                // 过滤出时间小于等于dateValue的OBJECT
                .filter(v -> {
                    JSONObject object = (JSONObject) v;
                    Long dateFieldValue = object.getLong(dateField);
                    return dateFieldValue <= dateValue;
                })
                // 降序排序拿第一条
                .sorted((v1, v2) -> {
                    JSONObject object1 = (JSONObject) v1;
                    JSONObject object2 = (JSONObject) v2;
                    Long l1 = object1.getLong(dateField);
                    Long l2 = object2.getLong(dateField);
                    return l2.compareTo(l1);
                }).collect(Collectors.toCollection(JSONArray::new)).getJSONObject(0).toJSONString();
    }
}


