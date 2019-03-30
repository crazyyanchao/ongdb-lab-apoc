package casia.isiteam.zdr.neo4j.FriendAnalysis;

import casia.isiteam.zdr.neo4j.procedures.CustomerProcedures;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.graphdb.*;
import org.neo4j.harness.junit.Neo4jRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
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
 * @PACKAGE_NAME: casia.isiteam.zdr.neo4j.FriendAnalysis
 * @Description: TODO(Public friend analysis)
 * @date 2019/3/30 17:13
 */
public class FriendAnalysisProceduresTest {

    @Rule
    public Neo4jRule neo4j = new Neo4jRule()
            .withProcedure(FriendAnalysisProcedures.class);

    //    CALL apoc.merge.node(['Person'],{ssid:'123'}, {name:'John'}) YIELD node RETURN node
    @Test
    public void publicFriendAnalysis() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        try (Transaction tx = db.beginTx()) {

//            id(source)	id(target)  result
////            60667	60669           60667:3条好友关系
////            60667	80988           60652:2条好友关系
////            60652	60667           60669:2条好友关系
////            60652	60669           80988:1条好友关系

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
            map.put("sourceList", sourceList);
            map.put("targetList", targetList);

            Result res = db.execute("CALL zdr.apoc.publicFriendAnalysis({sourceList},{targetList}) YIELD node RETURN node", map);

            while (res.hasNext()) {
                Node node = (Node) res.next().get("node");
                System.out.println("Node id:" + node.getId());
                System.out.println("Node label:" + node.getLabels());
                System.out.println("Node properties:" + node.getProperty("targetGroupFriendsRelaCount"));
            }

        }

    }

    @Test
    public void publicFriendAnalysisMap() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        try (Transaction tx = db.beginTx()) {

//            id(source)	id(target)  result
////            60667	60669           60667:3条好友关系
////            60667	80988           60652:2条好友关系
////            60652	60667           60669:2条好友关系
////            60652	60669           80988:1条好友关系

            // sourceList

            List<Long> sourceList = new ArrayList<>();
            sourceList.add(60667l);
            sourceList.add(60667l);
            sourceList.add(60652l);
            sourceList.add(60652l);

            List<Long> targetList = new ArrayList<>();
            targetList.add(60669l);
            targetList.add(80988l);
            targetList.add(60667l);
            targetList.add(60669l);

            Map<String, Object> map = new HashMap<>();
            map.put("sourceList", sourceList);
            map.put("targetList", targetList);

            Result res = db.execute("CALL zdr.apoc.publicFriendAnalysisMap({sourceList},{targetList}) YIELD list RETURN list", map);

            while (res.hasNext()) {
                List<Map<String, Object>> listMap = (List<Map<String, Object>>) res.next().get("list");
                for (int i = 0; i < listMap.size(); i++) {
                    Map<String, Object> stringObjectMap =  listMap.get(i);
                    stringObjectMap.forEach((k,v)->{
                        System.out.println(k+","+v);
                    });
                }
//                System.out.println("Node id:" + node.getId());
//                System.out.println("Node label:" + node.getLabels());
//                System.out.println("Node properties:" + node.getProperty("targetGroupFriendsRelaCount"));
            }

        }

    }
}


