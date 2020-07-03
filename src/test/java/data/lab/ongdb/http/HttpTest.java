package data.lab.ongdb.http;

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
 * @PACKAGE_NAME: data.lab.ongdb.http
 * @Description: TODO
 * @date 2020/7/2 16:15
 */
public class HttpTest {

    @Rule
    public Neo4jRule neo4j = new Neo4jRule().withFunction(Http.class);

    @Test
    public void post() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        Map<String, Object> params = new HashMap<>();
        params.put("api", "http://localhost:7424/ongdb/read/d/transaction/commit");
        params.put("input", "{\"statements\": [{\"statement\": \"match (n) return n limit 10;\"}],\"user\": \"ongdb\",\"password\": \"ongdb%dev\"}");
        Result result = db.execute("RETURN olab.http.post({api},{input}) AS resultPost", params);
        String resultPost = (String) result.next().get("resultPost");
        System.out.println(resultPost);
    }

    @Test
    public void get() {
    }

    @Test
    public void put() {
    }

    @Test
    public void delete() {
    }
}

