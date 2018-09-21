package casia.isiteam.zdr.neo4j.procedures;

import org.junit.Rule;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.harness.junit.Neo4jRule;

import java.util.HashMap;
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
 * @PACKAGE_NAME: casia.isiteam.zdr.neo4j.procedures
 * @Description: TODO(Describe the role of this JAVA class)
 * @author YanchaoMa yanchaoma@foxmail.com
 * @date 2018/8/3 13:31
 *
 *
 */
public class HelloWorldTest {

    @Rule
    public Neo4jRule neo4j = new Neo4jRule()
            .withFunction( HelloWorld.class );

    @Test
    public void shouldGreetWorld() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();

        try ( Transaction tx = db.beginTx() ) {
            String name = "World";

            Map<String, Object> params = new HashMap<>(  );
            params.put("name", name);

            Result result = db.execute( "RETURN zdr.apoc.hello({name}) as greeting", params);

            String greeting = (String) result.next().get("greeting");

            assertEquals( "Hello, "+ name, greeting );
        }
    }

    @Test
    public void test01(){
        System.out.println(String.format("Hello, %s", "neo4j"));
    }
}