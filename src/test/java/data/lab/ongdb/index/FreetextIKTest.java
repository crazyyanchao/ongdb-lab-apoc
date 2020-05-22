package data.lab.ongdb.index;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.harness.junit.Neo4jRule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.neo4j.index
 * @Description: TODO(Describe the role of this JAVA class)
 * @date 2019/4/23 9:51
 */
public class FreetextIKTest {

    // 测试函数
    @Rule
    public Neo4jRule neo4j = new Neo4jRule().withFunction(FreetextIK.class);

    @Test
    public void iKAnalyzer() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();

        Map<String, Object> map = new HashMap<>();
        String text = "复联终章快上映了好激动，据说知识图谱与人工智能技术应用到了那部电影！吖啶基氨基甲烷磺酰甲氧基苯胺是一种药嘛？";
        map.put("text", text);
        map.put("useSmart", true);

        Result res = db.execute("RETURN zdr.index.iKAnalyzer({text},{useSmart}) AS words", map);
        List<String> words = (List<String>) res.next().get("words");
        System.out.println(JSONArray.parseArray(JSON.toJSONString(words)));
    }

    @Test
    public void test() {
        int index = 0;
        System.out.println(index++);
        System.out.println(index);

        int begin = 100;
        System.out.println(++begin);
        System.out.println(begin);
    }

}