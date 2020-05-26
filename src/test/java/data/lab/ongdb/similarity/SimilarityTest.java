package data.lab.ongdb.similarity;

import org.junit.Rule;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.harness.junit.Neo4jRule;

import java.util.HashMap;
import java.util.Map;

/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.friendAnalysis
 * @Description: TODO
 * @date 2020/5/22 18:08
 */
public class SimilarityTest {

    @Rule
    public Neo4jRule neo4j = new Neo4jRule().withFunction(Similarity.class);

    @Test
    public void shouldGreetWorld() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        String name = "公司系经长春经济体制改革委员会长体改(1993)165号文批准，由长春第一光学仪器厂、长春长顺体育综合开发公司、天津利源总公司三家共同发起成立的股份有限公司。公司股票于1996年7月15日上市。";
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        Result result = db.execute("RETURN olab.simhash({name}) AS simHash", params);
        String greeting = (String) result.next().get("simHash");
        System.out.println(greeting);
    }

    @Test
    public void shouldGreetWorldThreshold() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        String name = "公司系经长春经济体制改革委员会长体改(1993)165号文批准，由长春第一光学仪器厂、长春长顺体育综合开发公司、天津利源总公司三家共同发起成立的股份有限公司。公司股票于1996年7月15日上市。";
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        Result result = db.execute("RETURN olab.simhash.threshold({name},60) AS simHash", params);
        String greeting = (String) result.next().get("simHash");
        System.out.println(greeting);
    }

}

