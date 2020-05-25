package data.lab.ongdb.similarity;

import org.junit.Rule;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.harness.junit.Neo4jRule;

import java.util.Map;

/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.similarity
 * @Description: TODO
 * @date 2020/5/25 17:34
 */
public class SimilarityProceTest {

    @Rule
    public Neo4jRule neo4j = new Neo4jRule().withProcedure(SimilarityProce.class);

    @Test
    public void simHashSimilarity() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        db.execute("CREATE (n {simhash:'1010111110110100111001000100110010000110100110101110101110000110'}) SET n:组织机构:中文名称 " +
                "CREATE (m {simhash:'1010111110110100111001000100110010000110100110101110101110000110'}) SET m:组织机构:中文名称");
        Result resultPath = db.execute("MATCH (n:组织机构:中文名称),(m:组织机构:中文名称) \n" +
                "WHERE n<>m AND NOT ((n)-[:相似简介]-(m))\n" +
                "CALL olab.simhash.build.rel(n,m,'simhash','simhash','相似简介',3) YIELD relId RETURN relId");
        while (resultPath.hasNext()) {
            Map<String, Object> map = resultPath.next();
            System.out.println(map.size());
        }
    }
}

