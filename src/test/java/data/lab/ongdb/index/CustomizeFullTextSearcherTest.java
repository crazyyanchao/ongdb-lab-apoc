package data.lab.ongdb.index;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;
import org.junit.rules.Timeout;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseBuilder;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.harness.junit.Neo4jRule;
import org.neo4j.kernel.internal.GraphDatabaseAPI;
import org.neo4j.test.rule.CleanupRule;
import org.neo4j.test.rule.TestDirectory;
import org.neo4j.test.rule.VerboseTimeout;
import org.neo4j.test.rule.fs.DefaultFileSystemRule;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.index.CustomizeFullTextSearcherTest
 * @Description: TODO(测试自定义得分计算)
 * @date 2020/5/22 10:48
 */
public class CustomizeFullTextSearcherTest {

    @Rule
    public Neo4jRule neo4j = new Neo4jRule().withProcedure(CustomizeFullTextSearcher.class);

    //    static final String QUERY_NODES = "CALL olab.index.fulltext.queryNodesBySimHash(\"%s\", \"%s\")";
    static final String QUERY_NODES = "CALL db.index.fulltext.queryNodes(\"%s\", \"%s\")";

    static final String NODE_CREATE = "CALL db.index.fulltext.createNodeIndex(\"%s\", %s, %s )";

    private GraphDatabaseAPI db;
    private GraphDatabaseBuilder builder;

    private final Timeout timeout = VerboseTimeout.builder().withTimeout(1, TimeUnit.HOURS).build();
    private final DefaultFileSystemRule fs = new DefaultFileSystemRule();
    private final TestDirectory testDirectory = TestDirectory.testDirectory();
    private final ExpectedException expectedException = ExpectedException.none();
    private final CleanupRule cleanup = new CleanupRule();

    private static final Label LABEL = Label.label("Label");
    private static final String PROP = "prop";
    static final String NODE = "node";
    static final String RELATIONSHIP = "relationship";
    private static final String SCORE = "score";

    @Rule
    public final RuleChain rules = RuleChain.outerRule(timeout).around(fs).around(testDirectory).around(expectedException).around(cleanup);


    @Before
    public void before() {
        GraphDatabaseFactory factory = new GraphDatabaseFactory();
        builder = factory.newEmbeddedDatabaseBuilder(testDirectory.databaseDir());
        builder.setConfig(GraphDatabaseSettings.store_internal_log_level, "DEBUG");
    }

    @After
    public void tearDown() {
        if (db != null) {
            db.shutdown();
        }
    }

    private GraphDatabaseAPI createDatabase() {
        return (GraphDatabaseAPI) cleanup.add(builder.newGraphDatabase());
    }

    static String array(String... args) {
        return Arrays.stream(args).map(s -> "\"" + s + "\"").collect(Collectors.joining(", ", "[", "]"));
    }

    private void awaitIndexesOnline() {
        try (Transaction tx = db.beginTx()) {
            db.schema().awaitIndexesOnline(1, TimeUnit.MINUTES);
            tx.success();
        }
    }

    /**
     * @param
     * @return
     * @Description: TODO(测试SimHash算法)
     */
    @Test
    public void queryFulltextForNodesBySimHash() {

        db = createDatabase();

        try (Transaction tx = db.beginTx()) {
            db.execute(format(NODE_CREATE, "nodes", array(LABEL.name()), array("prop1", "prop2"))).close();
            tx.success();
        }
        long nodeId;
        try (Transaction tx = db.beginTx()) {
            Node node = db.createNode(LABEL);
            nodeId = node.getId();
            node.setProperty("prop1", "foo");
            node.setProperty("prop2", "bar");
            tx.success();
        }

        awaitIndexesOnline();

        try (Transaction tx = db.beginTx()) {
            Node node = db.getNodeById(nodeId);
            node.setProperty("prop2", 42);
            tx.success();
        }

        try (Transaction tx = db.beginTx()) {
            assertQueryFindsIds(db, true, "nodes", "foo", nodeId);
            Result result = db.execute(format(QUERY_NODES, "nodes", "bar"));
            while (result.hasNext()) {
                Map<String, Object> map = result.next();
                for (Map.Entry entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " " + entry.getValue());
                }
            }
            result.close();
            tx.success();
        }
    }

    /**
     * @param
     * @return
     * @Description: TODO(测试SimHash算法)
     */
    @Test
    public void queryFulltextForNodesBySimHash_2() {
        db = (GraphDatabaseAPI) neo4j.getGraphDatabaseService();

        try (Transaction tx = db.beginTx()) {
            db.execute(format(NODE_CREATE, "nodes", array(LABEL.name()), array("prop1", "prop2"))).close();
            tx.success();
        }
        long nodeId;
        try (Transaction tx = db.beginTx()) {
            Node node = db.createNode(LABEL);
            nodeId = node.getId();
            node.setProperty("prop1", "foo");
            node.setProperty("prop2", "bar");
            tx.success();
        }

        awaitIndexesOnline();

        try (Transaction tx = db.beginTx()) {
            Node node = db.getNodeById(nodeId);
            node.setProperty("prop2", 42);
            System.out.println(node.getId());
            System.out.println(node.getProperty("prop1"));
            tx.success();
        }

        try (Transaction tx = db.beginTx()) {
            assertQueryFindsIds(db, true, "nodes", "foo", nodeId);
            Result result = db.execute(format(QUERY_NODES, "nodes", "bar"));
            while (result.hasNext()) {
                Map<String, Object> map = result.next();
                for (Map.Entry entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " " + entry.getValue());
                }
            }
            result.close();
            tx.success();
        }
    }

    static void assertQueryFindsIds(GraphDatabaseService db, boolean queryNodes, String index, String query, long... ids) {
        try (Transaction tx = db.beginTx()) {
            String queryCall = queryNodes ? QUERY_NODES : null;
            Result result = db.execute(format(queryCall, index, query));
            int num = 0;
            Double score = Double.MAX_VALUE;
            while (result.hasNext()) {
                Map entry = result.next();
                Long nextId = ((Entity) entry.get(queryNodes ? NODE : RELATIONSHIP)).getId();
                Double nextScore = (Double) entry.get(SCORE);
//                assertThat( nextScore, lessThanOrEqualTo( score ) );
                score = nextScore;
                if (num < ids.length) {
                    assertEquals(format("Result returned id %d, expected %d", nextId, ids[num]), ids[num], nextId.longValue());
                } else {
                    fail(format("Result returned id %d, which is beyond the number of ids (%d) that were expected.", nextId, ids.length));
                }
                num++;
            }
            assertEquals("Number of results differ from expected", ids.length, num);
            tx.success();
        }
    }

}

