package casia.isiteam.zdr.neo4j.procedures;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.harness.junit.Neo4jRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

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

/**
 * @author YanchaoMa yanchaoma@foxmail.com
 * @PACKAGE_NAME: casia.isiteam.zdr.neo4j.procedures
 * @Description: TODO(自定义函数测试)
 * @date 2018/8/8 17:23
 */
public class ZdrProceduresTest {

    @Rule
    public Neo4jRule neo4j = new Neo4jRule().withFunction(ZdrProcedures.class);

    @Test
    public void sortDESC() throws Exception {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        try (Transaction tx = db.beginTx()) {

            Map<String, Object> params = new HashMap<>();
            List<Integer> list = new ArrayList<>();
            list.add(4);
            list.add(1);
            list.add(2);
            list.add(3);
            params.put("list", list);
            Result result = db.execute("RETURN zdr.apoc.sortDESC({list}) as descList", params);
            List<Integer> descList = (List<Integer>) result.next().get("descList");
            System.out.println(descList);
        }
    }

    @Test
    public void getEventIdsSize() throws Exception {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        try (Transaction tx = db.beginTx()) {
            String eventIds = "123213,234324,4354353,1231231,2132131";
            Map<String, Object> params = new HashMap<>();
            params.put("eventIds", eventIds);
            Result result = db.execute("RETURN zdr.apoc.getEventIdsSize({eventIds}) as value", params);
            int eventIdsSize = (int) result.next().get("value");
            System.out.println(eventIdsSize);

// 首先自定义SIZE函数，然后通过关系的属性进行排序：
// match p=(n:事)<-[r:命中关键词]-(m:虚拟账号ID) where n.name='新闻_1432' and r.eventTargetIds IS NOT NULL return p ORDER BY zdr.apoc.getEventIdsSize(r.eventTargetIds) DESC limit 10

        }
    }

    @Test
    public void getInitAnnualTime() throws Exception {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        try (Transaction tx = db.beginTx()) {
            String startTime = "2006-05-01 00:00:00";
            Map<String, Object> params = new HashMap<>();
            params.put("startTime", startTime);
            Result result = db.execute("RETURN zdr.apoc.initAnnualTime({startTime}) as value", params);
            long initStartTime = (long) result.next().get("value");
            System.out.println(initStartTime);
        }
    }

    @Test
    public void presentStringToDate() throws Exception {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        try (Transaction tx = db.beginTx()) {
            String present = "Present";
            Map<String, Object> params = new HashMap<>();
            params.put("present", present);
            Result result = db.execute("RETURN zdr.apoc.presentStringToDate({present}) as value", params);
            String initStartTime = (String) result.next().get("value");
            System.out.println(initStartTime);
        }
    }

    @Test
    public void getStringSize() throws Exception {
        String string = "213123,123123,123123,123123,12312";

    }

    @Test
    public void matchTimeZone() throws Exception {
        // 测试查询：
        // MATCH p=(n:手机号)-[r:通联]-(m:手机号) WHERE n.name='13910317532' AND m.name='13910272362' AND zdr.apoc.matchTimeZone({timeList:r.dateHistory,startTime:'2018-03-05 15:37:42',stopTime:'2018-03-05 15:37:43'})=0 RETURN p
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        try (Transaction tx = db.beginTx()) {

            String dateHistory = "2018-03-25 17:49:39,2018-03-22 23:13:56,2018-03-05 21:13:05,2018-03-01 17:01:25,2018-03-01 10:48:08,2018-03-01 10:48:03,2018-03-01 10:48:02";
            Map<String, String> map = new HashMap<>();

            map.put("timeList", dateHistory);

            map.put("startTime", "2018-03-25 10:49:39");
            map.put("stopTime", "2018-03-25 20:49:39");

            Map<String, Object> params = new HashMap<>();
            params.put("paras", map);

            Result result = db.execute("RETURN zdr.apoc.matchTimeZone({paras}) as value", params);

            int eventIdsSize = (int) result.next().get("value");
            System.out.println(eventIdsSize);

        }
    }

    @Test
    public void matchTimeListString() throws Exception {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        try (Transaction tx = db.beginTx()) {

            String dateHistory = "2018-03-25 17:49:39,2018-03-22 23:13:56,2018-03-05 21:13:05,2018-03-01 17:01:25,2018-03-01 10:48:08,2018-03-01 10:48:03,2018-03-01 10:48:02";
            Map<String, String> map = new HashMap<>();

            map.put("timeList", dateHistory);

            map.put("startTime", "2018-03-25 10:49:39");
            map.put("stopTime", "2018-03-25 20:49:39");

            Map<String, Object> params = new HashMap<>();
            params.put("paras", map);

            Result result = db.execute("RETURN zdr.apoc.matchTimeListString({paras}) as value", params);

            String eventIdsSize = (String) result.next().get("value");
            System.out.println(eventIdsSize);

        }
    }

    @Test
    public void percentageInfluenceScore() throws Exception {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        try (Transaction tx = db.beginTx()) {

            Map<String, Object> map = new HashMap<>();

            map.put("minScore", 15);
            map.put("maxScore", 79);
            map.put("currentScore", 77);

            Map<String, Object> params = new HashMap<>();
            params.put("paras", map);

            Result result = db.execute("RETURN zdr.apoc.scorePercentage({paras}) as value", params);

            double eventIdsSize = (double) result.next().get("value");
            System.out.println(eventIdsSize);

        }
    }

    //return zdr.apoc.moveDecimalPoint({scoreObject:21313777.48543,moveLength:100.0})
    @Test
    public void moveDecimalPoint() throws Exception {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        try (Transaction tx = db.beginTx()) {

//            Map<String, Object> map = new HashMap<>();
//            map.put("scoreObject", 0.15213123);
//            map.put("moveLength", 100);

            Map<String, Object> map = new HashMap<>();
            map.put("scoreObject", 21313777.48543);
            map.put("moveLength", 100.0);

//            Map<String, Object> map = new HashMap<>();
//            map.put("scoreObject",  0.98364);
//            map.put("moveLength", 1000000);

            Map<String, Object> params = new HashMap<>();
            params.put("paras", map);

            Result result = db.execute("RETURN zdr.apoc.moveDecimalPoint({paras}) as value", params);

            Object eventIdsSize = result.next().get("value");
            System.out.println(eventIdsSize);

        }
    }

    @Test
    public void timeCrossOrNot() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        Map<String, Object> map = new HashMap<>();
        map.put("r1Start", "2012-01-01 00:00:00");
        map.put("r1Stop", "2015-01-01 00:00:00");
        map.put("r2Start", "2012-02-01 00:00:00");
        map.put("r2Stop", "");

        Map<String, Object> params = new HashMap<>();
        params.put("paras", map);

        Result result = db.execute("RETURN zdr.apoc.timeCrossOrNot({paras}) AS value", params);
        boolean bool = (boolean) result.next().get("value");
        System.out.println(bool);
    }

    @Test
    public void dataType() {
        long data = 1231231l;
//        double data = 234324.3432;
//        int data = 21312;
//        float data = 21312;

        Object dataObject = data;
        if (dataObject instanceof Long) {
            System.out.println("long");
        } else if (dataObject instanceof Double) {
            System.out.println("double");
        } else if (dataObject instanceof Integer) {
            System.out.println("int");
        } else if (dataObject instanceof Float) {
            System.out.println("float");
        }
    }

}

