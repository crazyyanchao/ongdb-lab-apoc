package casia.isiteam.zdr.neo4j.index;

import casia.isiteam.zdr.neo4j.procedures.CustomerProcedures;
import casia.isiteam.zdr.neo4j.result.NodeResult;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.graphdb.*;
import org.neo4j.harness.junit.Neo4jRule;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * @PACKAGE_NAME: casia.isiteam.zdr.neo4j.index
 * @Description: TODO(中文分词全文索引测试)
 * @date 2019/4/9 18:02
 */
public class FulltextIndexTest {

    // 测试过程
    @Rule
    public Neo4jRule neo4j = new Neo4jRule().withProcedure(FulltextIndex.class);

    // 测试函数
    @Rule
    public Neo4jRule neo4jFunc = new Neo4jRule().withFunction(FulltextIndex.class);

    @Test
    public void addChineseFulltextIndex() {
        PropertyConfigurator.configureAndWatch("dic/log4j.properties");

        GraphDatabaseService db = neo4j.getGraphDatabaseService();

        Map<String, Object> map = new HashMap<>();
        String indexName = "index";
        String labelName = "Test";
        List<String> propKeys = new ArrayList<>();
        propKeys.add("key1");

        map.put("indexName", indexName);
        map.put("labelName", labelName);
        map.put("propKeys", propKeys);

        try (Transaction tx = db.beginTx()) {
            // 创建节点
            Node node = db.createNode(Label.label("Loc"));
            node.setProperty("name", "A");
            node.setProperty("description", "复联终章快上映了好激动，据说知识图谱与人工智能技术应用到了那部电影！吖啶基氨基甲烷磺酰甲氧基苯胺是一种药嘛？");
            Node node1 = db.createNode(Label.label("Loc"));
            node1.setProperty("name", "B");
            node1.setProperty("description", "复联终章快上映了好激动，据说知识图谱与人工智能技术应用到了那部电影！吖啶基氨基甲烷磺酰甲氧基苯胺是一种药嘛？");
            Node node2 = db.createNode(Label.label("Loc"));
            node2.setProperty("name", "C");
            node2.setProperty("description", "复联终章快上映了好激动，据说知识图谱与人工智能技术应用到了那部电影！");

            // 给节点建立中文全文索引
            Result res = db.execute("CALL zdr.index.addChineseFulltextIndex('IKAnalyzer', 'Loc', ['description']) YIELD message RETURN message");
            String message = (String) res.next().get("message");
            System.out.println(message);

            // 查询节点
            Result res2 = db.execute("CALL zdr.index.chineseFulltextIndexSearch('IKAnalyzer', 'description:吖啶基氨基甲烷磺酰甲氧基苯胺', 100) YIELD node RETURN node");
            while (res2.hasNext()) {
                Node nodeSearch = (Node) res2.next().get("node");
                System.out.println("ID:" + nodeSearch.getId());
                System.out.println("Labels:");
                nodeSearch.getLabels().forEach(v -> {
                    System.out.println(v);
                });
                Map<String, Object> mapObj = nodeSearch.getAllProperties();
                for (Map.Entry entry : mapObj.entrySet()) {
                    System.out.println(entry.getKey() + ":" + entry.getValue());
                }
            }
            tx.success();
        }
    }

    @Test
    public void chineseFulltextIndexSearch() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();

        // 查询节点
        try (Transaction tx = db.beginTx()) {
            Result res = db.execute("CALL zdr.index.chineseFulltextIndexSearch('IKAnalyzer', '复联', 100) YIELD node RETURN node");
            while (res.hasNext()) {
                Node message = (Node) res.next().get("message");
                System.out.println(message.getId());
                System.out.println(message.getProperty("name"));
            }
        }
    }

    @Test
    public void iKAnalyzer() {
        GraphDatabaseService db = neo4jFunc.getGraphDatabaseService();

        Map<String, Object> map = new HashMap<>();
        String text = "复联终章快上映了好激动，据说知识图谱与人工智能技术应用到了那部电影！吖啶基氨基甲烷磺酰甲氧基苯胺是一种药嘛？";
        map.put("text", text);
        map.put("useSmart", true);

        try (Transaction tx = db.beginTx()) {
            Result res = db.execute("RETURN zdr.index.iKAnalyzer({text},{useSmart}) AS words", map);
            List<String> words = (List<String>) res.next().get("words");
            System.out.println(JSONArray.parseArray(JSON.toJSONString(words)));
        }
    }
}
