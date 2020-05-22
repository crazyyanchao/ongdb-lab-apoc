package data.lab.ongdb.procedures;

import org.junit.Rule;
import org.junit.Test;
import org.neo4j.graphdb.*;
import org.neo4j.harness.junit.Neo4jRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.neo4j.procedures
 * @Description: TODO(自定义函数测试)
 * @date 2018/8/8 17:23
 */
public class ZdrProceduresTest {

    @Rule
    public Neo4jRule neo4j = new Neo4jRule().withFunction(ZdrProcedures.class);

    @Test
    public void shouldGreetWorld() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();

        try (Transaction tx = db.beginTx()) {
            String name = "World";

            Map<String, Object> params = new HashMap<>();
            params.put("name", name);

            Result result = db.execute("RETURN zdr.apoc.hello({name}) as greeting", params);

            String greeting = (String) result.next().get("greeting");

            assertEquals("Hello, " + name, greeting);
        }
    }

    @Test
    public void test01() {
        System.out.println(String.format("Hello, %s", "neo4j"));
    }

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
        map.put("r2Start", "2013-02-01 00:00:00");
        map.put("r2Stop", "2016-01-01 00:00:00");

        Map<String, Object> params = new HashMap<>();
        params.put("paras", map);

        Result result = db.execute("RETURN zdr.apoc.timeCrossOrNot({paras}) AS value", params);
        boolean bool = (boolean) result.next().get("value");
        System.out.println(bool);
    }

    @Test
    public void isContainsString() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        Map<String, Object> map = new HashMap<>();
        map.put("original0", "0Chinese,English");
        map.put("original1", "1Chinese,English");
        map.put("original2", "2Chinese,English");
        map.put("original3", "3Chinese,English");
        map.put("original4", "4Chinese,English");
        map.put("original5", "5Chinese,English");
        map.put("original6", "6Chinese,English");
        map.put("original7", "7Chinese,English");
        map.put("original8", "8Chinese,English");
        map.put("original9", "9Chinese,English");
        map.put("input", "Chinese");
        Map<String, Object> params = new HashMap<>();
        params.put("paras", map);
        Result result = db.execute("RETURN zdr.apoc.isContainsString({paras}) AS value", params);
        boolean bool = (boolean) result.next().get("value");
        System.out.println(bool);
    }

    @Test
    public void stringCharCount() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        Map<String, Object> map = new HashMap<>();
        map.put("original", "0Chinese,English,Chinese,China,Hadoop,Spark");
        map.put("char", "English");
        Map<String, Object> params = new HashMap<>();
        params.put("paras", map);
        Result result = db.execute("RETURN zdr.apoc.stringCharCount({paras}) AS value", params);
        long num = (long) result.next().get("value");
        System.out.println(num);
    }

    @Test
    public void relatCalculateRestrict() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        List<String> nLabels = new ArrayList<>();
        nLabels.add("Facebook发帖");

        List<String> mLabels = new ArrayList<>();
        mLabels.add("Facebook发帖");

        String strict = "FacebookID||Facebook发帖";

        Map<String, Object> map = new HashMap<>();
        map.put("nLabels", nLabels);
        map.put("mLabels", mLabels);
        map.put("strictLabels", strict);

//        Map<String, Object> params = new HashMap<>();
//        params.put("paras",map);

        Result result = db.execute("RETURN zdr.apoc.relatCalculateRestrict({nLabels},{mLabels},{strictLabels}) AS value", map);
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


    @Test
    public void test10() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        try (Transaction tx = db.beginTx()) {
            Node node = db.createNode();
            node.setProperty("name", "Test");
            node.setProperty("key1", "Test，");
            node.setProperty("born", "的");
            node.setProperty("age", 12323);

            Map<String, Object> map = new HashMap<>();
            map.put("node", node);

//        Map<String, Object> params = new HashMap<>();
//        params.put("paras",map);

            Result result = db.execute("RETURN zdr.apoc.isContainChinese({node}) AS value", map);
            long bool = (long) result.next().get("value");
            System.out.println(bool);
        }
    }

    @Test
    public void test11() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        try (Transaction tx = db.beginTx()) {
            Node node = db.createNode();
            String prefix = "sysuser_id_";
            node.setProperty("name", "Test");
            node.setProperty("key1", "Test");
            node.setProperty("born", "的撒");
//            node.setProperty(prefix+"sadsad32432c", "sadsad32432c");
            Map<String, Object> map = new HashMap<>();
            map.put("node", node);

//        Map<String, Object> params = new HashMap<>();
//        params.put("paras",map);

            Result result = db.execute("RETURN zdr.apoc.isContainAuthority({node}) AS value", map);
            boolean bool = (boolean) result.next().get("value");
            System.out.println(bool);
        }
    }

    @Test
    public void test12() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        try (Transaction tx = db.beginTx()) {
            Node nodesOne = db.createNode();
            nodesOne.setProperty("name", 60667);

            Node nodesTwo = db.createNode();
            nodesTwo.setProperty("name", 60652);

            Node nodesThree = db.createNode();
            nodesThree.setProperty("name", 60669);

            Node nodesFour = db.createNode();
            nodesFour.setProperty("name", 80988);

            // sourceList

            List<Node> sourceList = new ArrayList<>();
            sourceList.add(nodesOne);
            sourceList.add(nodesOne);
            sourceList.add(nodesTwo);
            sourceList.add(nodesTwo);

            List<Node> targetList = new ArrayList<>();
            targetList.add(nodesThree);
            targetList.add(nodesFour);
            targetList.add(nodesOne);
            targetList.add(nodesThree);

            Map<String, Object> map = new HashMap<>();
            map.put("nodeCollections", new Object[]{sourceList, targetList});

//        Map<String, Object> params = new HashMap<>();
//        params.put("nodeCollections",map);

            Result result = db.execute("RETURN zdr.apoc.mergeNodes({nodeCollections}) AS value", map);
            List<Node> bool = (List<Node>) result.next().get("value");
            System.out.println(bool.size());
        }
    }

    @Test
    public void test13() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        try (Transaction tx = db.beginTx()) {
            Node nodesOne = db.createNode();
            nodesOne.setProperty("name", 60667);
            nodesOne.setProperty("Hometown", "BeiJing");
            nodesOne.setProperty("location", "Oak Ridge, Tennessee");
            nodesOne.setProperty("addresses", "Oak Ridge, Tennessee");

            Map<String, Object> map = new HashMap<>();
            map.put("node", nodesOne);
            List<String> list = new ArrayList<>();
            list.add("Hometown1");
            list.add("location1");
            list.add("");
            list.add("locality1");
            list.add("addresses");
            list.add("userCardLocation1");
            map.put("locMultiFields", list);//new String[]{"CurrentCity","Location"}

//        Map<String, Object> params = new HashMap<>();
//        params.put("nodeCollections",map);

//            Result result = db.execute("RETURN zdr.apoc.locMultiFieldsFullTextSearchCondition({node},{locMultiFields}) AS value", map);
//            String str = (String) result.next().get("value");
//            System.out.println(str);

            Result result = db.execute("RETURN zdr.apoc.nodeIsContainsKey({node},{locMultiFields}) AS value", map);
            boolean str = (boolean) result.next().get("value");
            System.out.println(str);
        }
    }

    @Test
    public void test14() {
        ZdrProcedures zdrProcedures = new ZdrProcedures();
        String str = "test,中文s，！";
        char[] array = str.toCharArray();
        for (int i = 0; i < array.length; i++) {
            char c = array[i];
            System.out.println("rawChar:" + c + " bool:");
        }
    }

    @Test
    public void test15() {
//        zdr.apoc.removeIdsFromRawList
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        try (Transaction tx = db.beginTx()) {
            List<Long> rawIDs = new ArrayList<>();
//            rawIDs.add(60667l);
//            rawIDs.add(60667l);
//            rawIDs.add(60652l);
//            rawIDs.add(60652l);

            List<Long> ids = new ArrayList<>();
            ids.add(60667l);
            ids.add(60652l);
            Map<String, Object> map = new HashMap<>();
            map.put("rawIDs", rawIDs);
            map.put("ids", ids);

            Result result = db.execute("RETURN zdr.apoc.removeIdsFromRawList({rawIDs},{ids}) AS value", map);
            List<Long> rawIds = (List<Long>) result.next().get("value");
            if (rawIds != null) {
                for (int i = 0; i < rawIds.size(); i++) {
                    long aLong = rawIds.get(i);
                    System.out.println(aLong);
                }
            }
        }
    }

}

