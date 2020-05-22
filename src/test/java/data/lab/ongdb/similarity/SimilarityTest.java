package data.lab.ongdb.similarity;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import data.lab.ongdb.procedures.Procedures;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.harness.junit.Neo4jRule;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.similarity
 * @Description: TODO
 * @date 2020/5/22 15:16
 */
public class SimilarityTest {

    @Rule
    public Neo4jRule neo4j = new Neo4jRule().withProcedure(Similarity.class);

    @Test
    public void produceSimHash() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        Map<String, Object> params = new HashMap<>();
        params.put("text", "北京全媒体融媒体国内媒体国外媒体世界媒体");
        Result result = db.execute("RETURN olab.simhash({text}) AS simHash", params);
        while (result.hasNext()) {
            Map<String, Object> map = result.next();
            System.out.println(map.get("simHash"));
        }
    }

}

