package data.lab.ongdb.procedures;

import org.junit.Rule;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.harness.junit.Neo4jRule;

import static org.junit.Assert.*;

/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.neo4j.procedures
 * @Description: TODO(Describe the role of this JAVA class)
 * @date 2018/8/3 13:30
 */
public class CustomerProceduresTest {

    @Rule
    public Neo4jRule neo4j = new Neo4jRule()
            .withProcedure(CustomerProcedures.class);

    @Test
    public void shouldMountMyProcedures() throws Throwable {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();

        try (Transaction tx = db.beginTx()) {
            Result res = db.execute("CALL olab.createCustomer('Test') YIELD node RETURN node");

            Node node = (Node) res.next().get("node");

            System.out.println(node.getId());
            System.out.println(node.getLabels());
            System.out.println(node.getRelationships());

            assertEquals(node.getProperty("name"), "Test");
        }

        try (Transaction tx = db.beginTx()) {
            Result res = db.execute("CALL training.recommendOnly(\"Test\")");

            System.out.println(res.resultAsString());
        }
    }

    @Test
    public void testRecommend() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();

        try (Transaction tx = db.beginTx()) {
            // CALL training.recommendOnly("Dee Dee Titley_0_linkedin") YIELD stringObjectMap RETURN stringObjectMap
//            Result res = db.execute("CALL training.recommendOnly(\"Dee Dee Titley_0_linkedin\") YIELD stringObjectMap RETURN stringObjectMap");

            Result res = db.execute("CALL olab.index.search('linkin', 'name:china*', 10) YIELD node RETURN node");

//
//            System.out.println(res.resultAsString());
//            System.out.println(res.next());
//            Node node = (Node) res.next().get("stringObjectMap");
//            CustomerProcedures.Movie movie = (CustomerProcedures.Movie) res.next().get("stringObjectMap");
//            movie.stringObjectMap.forEach((k, v) -> System.out.println("key:value = " + k + ":" + v));
//            System.out.println(node.getId());
//            System.out.println(node.getLabels());
//            System.out.println(node.getRelationships());

        }
    }

}
