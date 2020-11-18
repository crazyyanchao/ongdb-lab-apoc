package data.lab.ongdb.procedures;

import org.junit.Rule;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.harness.junit.Neo4jRule;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.procedures
 * @Description: TODO
 * @date 2020/11/18 11:06
 */
public class FunctionPartitionTest {

    @Rule
    public Neo4jRule neo4j = new Neo4jRule().withFunction(FunctionPartition.class);

    @Test
    public void structureMergeToListMap() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();

        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("fields", Arrays.asList("area_code","author",123));
        hashMap.put("items",Arrays.asList(Arrays.asList("001","HORG001",234),Arrays.asList("002","HORG002",344)));

        Result result = db.execute("RETURN olab.structure.mergeToListMap({fields},{items}) AS value", hashMap);
        String string = (String) result.next().get("value");
        System.out.println(string);
    }
}