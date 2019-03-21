package casia.isiteam.zdr.neo4j.procedures;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.index.lucene.QueryContext;
import org.neo4j.kernel.api.exceptions.InvalidArgumentsException;
import org.neo4j.logging.Log;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Mode;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.Procedure;

import static org.neo4j.helpers.collection.MapUtil.map;

public class CustomerProcedures {

    /**
     * 运行环境/上下文
     */
    @Context
    public GraphDatabaseService db;

    @Context
    public Log log;

    public static Label Customer = Label.label("Customer");

    /**
     * mode - 执行模式:
     * • Mode.READ – 对图执行只读操作
     * • Mode.WRITE - 对图执行读写操作
     * • Mode.SCHEMA – 操作数据库模式，例如创建索引、限制等
     * • Mode.DBMS – 系统操作，但是不包括图操作
     * • Mode.DEFAULT – 缺省是 Mode.READ
     *
     * @param
     * @return
     * @Description: TODO(@ Description的内容会在Neo4j浏览器中调用dbms.functions () 时显示)
     */
    @Procedure(name = "zdr.apoc.createCustomer", mode = Mode.WRITE)
    @Description("customers.create(name) | Create a new Customer node")
    public Stream<NodeResult> createCustomer(@Name("name") String name) {
        List<NodeResult> output = new ArrayList<>();

        try (Transaction tx = db.beginTx()) {
            Node node = db.createNode(Customer);

            node.setProperty("name", name);


            output.add(new NodeResult(node));

            log.debug("Creating Customer with Node ID " + node.getId());

            tx.success();
        }

        return output.stream();
    }

    /**
     * @param
     * @Description: TODO(结果对象)
     * @return
     */
    public static class NodeResult {
        public Node node;

        public NodeResult(Node node) {
            this.node = node;
        }
    }

    @Procedure(name = "training.recommendOnly", mode = Mode.READ)
    @Description("Find recommender by an linkin account")
    public Stream<Movie> recommendOnly(@Name("name") String name) throws InvalidArgumentsException, IOException {
        String query = "MATCH (n:LinkedinID {name: {name}}) RETURN n";
        return db.execute(query, map("name", name))
                .stream()
                .map(Movie::new);
    }

    public class Movie {
        public Map<String, Object> stringObjectMap;

        public Movie(Map<String, Object> stringObjectMap) {
            stringObjectMap.forEach((k, v) -> System.out.println("key:value = " + k + ":" + v));
            this.stringObjectMap = stringObjectMap;
        }
    }

    @Procedure(value = "zdr.index.search", mode = Mode.WRITE)
    @Description("CALL zdr.index.search(String indexName, String query, long limit) YIELD node,执行LUCENE全文检索，返回前{limit个结果}")
    public Stream<ChineseHit> search(@Name("indexName") String indexName, @Name("query") String query, @Name("limit") long limit) {
        if (!db.index().existsForNodes(indexName)) {
            log.debug("如果索引不存在则跳过本次查询：`%s`", indexName);
            return Stream.empty();
        }
        return db.index()
                .forNodes(indexName)
                .query(new QueryContext(query).sortByScore().top((int) limit))
                .stream()
                .map(ChineseHit::new);
    }

    public static class ChineseHit {
        public Node node;

        public ChineseHit(Node node) {
            this.node = node;
        }
    }

}

