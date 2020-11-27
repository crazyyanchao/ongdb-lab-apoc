package data.lab.ongdb.function;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.harness.junit.Neo4jRule;

import java.util.List;

/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.function
 * @Description: TODO
 * @date 2020/7/3 10:32
 */
public class PathFilterTest {

    private static final String OPERATOR_GRAPH_CYPHER = "merge (c1:算子 {interface:'/read/d/transaction/c1'}) set c1 +={name:'c1',hupdatetime:'20200702121021',calculation_engine:'{api:\\'http://localhost:7424/ongdb/read/d/transaction/c1\\',input:\\'?\\'}'}\n" +
            "merge (c2:算子 {interface:'/read/d/transaction/c1'}) set c2 +={name:'c2',hupdatetime:'20200702121021',calculation_engine:'{api:\\'http://localhost:7424/ongdb/read/d/transaction/c2\\',input:\\'?\\'}'}\n" +
            "merge (c3:算子 {interface:'/read/d/transaction/c3'}) set c3 +={name:'c3',hupdatetime:'20200702121021',calculation_engine:'{api:\\'http://localhost:7424/ongdb/read/d/transaction/c3\\',input:\\'?\\'}'}\n" +
            "merge (c4:算子 {interface:'/read/d/transaction/c4'}) set c4 +={name:'c4',hupdatetime:'20200702121021',calculation_engine:'{api:\\'http://localhost:7424/ongdb/read/d/transaction/c4\\',input:\\'?\\'}'}\n" +
            "merge (c5:算子 {interface:'/read/d/transaction/c5'}) set c5 +={name:'c5',hupdatetime:'20200702121021',calculation_engine:'{api:\\'http://localhost:7424/ongdb/read/d/transaction/c5\\',input:\\'?\\'}'}\n" +
            "merge (c6:算子 {interface:'/read/d/transaction/c6'}) set c6 +={name:'c6',hupdatetime:'20200702121021',calculation_engine:'{api:\\'http://localhost:7424/ongdb/read/d/transaction/c6\\',input:\\'?\\'}'}\n" +
            "merge (c1)-[r1:输入]->(c3) set r1 +={hupdatetime:'20200702121021',operator_combine_list:'[[\\'/read/d/transaction/c1\\']]'}\n" +
            "merge (c2)-[r2:输入]->(c3) set r2 +={hupdatetime:'20200702121021',operator_combine_list:'[[\\'/read/d/transaction/c2\\']]'}\n" +
            "merge (c3)-[r3:输入]->(c5) set r3 +={hupdatetime:'20200702121021',operator_combine_list:'[[\\'/read/d/transaction/c3\\',\\'/read/d/transaction/c4\\']]'}\n" +
            "merge (c4)-[r4:输入]->(c5) set r4 +={hupdatetime:'20200702121021',operator_combine_list:'[[\\'/read/d/transaction/c3\\',\\'/read/d/transaction/c4\\']]'}\n" +
            "merge (c5)-[r5:输入]->(c6) set r5 +={hupdatetime:'20200702121021',operator_combine_list:'[[\\'/read/d/transaction/c5\\']]'}";

    @Rule
    public Neo4jRule neo4j = new Neo4jRule().withFunction(PathFilter.class);

    GraphDatabaseService db ;

    @Before
    public void setUp() throws Exception {
        db= neo4j.getGraphDatabaseService();
        db.execute(OPERATOR_GRAPH_CYPHER);
    }

    @Test
    public void operatorSortList() {
        Result res = db.execute("MATCH p=(n:算子)-[:输入]->(:算子)\n" +
                "WITH COLLECT(p) AS pathList\n" +
                "RETURN olab.operator.sort([0],pathList) AS operatorSortList");
        Object operatorSortList =res.next().get("operatorSortList");
//        for (Long[] longs: operatorSortList) {
//            for (long id: longs) {
//                System.out.println(id+" ");
//            }
//            System.out.println();
//        }
        System.out.println(operatorSortList);
    }

}

