package data.lab.ongdb.index;

import org.apache.log4j.PropertyConfigurator;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.graphdb.*;
import org.neo4j.harness.junit.Neo4jRule;

import java.util.*;

import static org.hamcrest.CoreMatchers.allOf;
import static org.junit.Assert.*;
import static org.neo4j.helpers.collection.MapUtil.map;

/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.index.FulltextIndexTest
 * @Description: TODO(中文分词全文索引测试)
 * @date 2020/5/22 10:50
 */
public class FulltextIndexTest {

    private GraphDatabaseService db;

    @Before
    public void setUp() throws Exception {
//        System.setProperty("hadoop.home.dir", "C:\\Users\\11416\\Desktop\\project\\workspace\\neo4j-apoc-procedures-3.4.0.1\\hadoop");
//        db = new TestGraphDatabaseFactory().newImpermanentDatabase();
//        TestUtil.registerProcedure(db, FulltextIndex.class);
    }

    @After
    public void tearDown() {
//        db.shutdown();
        db = null;
    }

    // 测试过程
    @Rule
    public Neo4jRule neo4j = new Neo4jRule().withProcedure(FulltextIndex.class);

    // 测试函数
    @Rule
    public Neo4jRule neo4jFunc = new Neo4jRule().withFunction(FulltextIndex.class);

    @Test
    public void addChineseFulltextIndex() {
        PropertyConfigurator.configureAndWatch("dic/log4j.properties");

        GraphDatabaseService db = neo4j.getGraphDatabaseService();

        try (Transaction tx = db.beginTx()) {
            // 创建节点
            Node node = db.createNode(Label.label("Loc"));
            node.setProperty("name", "A");
            node.setProperty("description", "复联终章快上映了好激动，据说知识图谱与人工智能技术应用到了那部电影！吖啶基氨基甲烷磺酰甲氧基苯胺是一种药嘛？");
            Node node1 = db.createNode(Label.label("Loc"));
            node1.setProperty("name", "B");
            node1.setProperty("description", "复联终章快上映了好激动，据说知识图谱与人工智能技术应用到了那部电影！吖啶基氨基甲烷磺酰甲氧基苯胺是一种药嘛？");
            Node node2 = db.createNode(Label.label("Loc"));
            node2.setProperty("name", "C");
            node2.setProperty("description", "复联终章快上映了好激动，据说知识图谱与人工智能技术应用到了那部电影！");

            // 给节点建立中文全文索引
            Result res = db.execute("CALL olab.index.addChineseFulltextIndex('IKAnalyzer', ['description'],'Loc') YIELD message RETURN message");
//            Result res = db.execute("CALL olab.index.addChineseFulltextAutoIndex('IKAnalyzer', 'Loc', ['description'],{autoUpdate:'true'}) YIELD label,property,nodeCount RETURN label,property,nodeCount");
//            Result res = db.execute("CALL olab.index.addAllNodes('IKAnalyzer',{Loc:['description','year']},{autoUpdate:'true'})");

            Map<String, Object> map = res.next();
            for (Map.Entry entry : map.entrySet()) {
                System.out.println(entry.getKey() + ":" + entry.getValue());
            }

            Node node3 = db.createNode(Label.label("Loc"));
            node3.setProperty("name", "D");
            node3.setProperty("description", "复联终章快上映了好激动，据说知识图谱与人工智能技术应用到了那部电影！");
            tx.success();
        }
        try (Transaction tx = db.beginTx()) {
            // 查询节点
            Result res2 = db.execute("CALL olab.index.chineseFulltextIndexSearch('IKAnalyzer', 'description:复联终章', 100) YIELD node,weight RETURN node,weight");

            while (res2.hasNext()) {
                Map<String, Object> mapO = res2.next();
                Node nodeSearch = (Node) mapO.get("node");
                double hitScore = (double) mapO.get("weight");
                System.out.println("ID:" + nodeSearch.getId() + " Score:" + hitScore);
                Map<String, Object> mapObj = nodeSearch.getAllProperties();
                for (Map.Entry entry : mapObj.entrySet()) {
                    System.out.println(entry.getKey() + ":" + entry.getValue());
                }
            }
            tx.success();
        }
    }

    @Test
    public void addNodeChineseFulltextIndex() {

        GraphDatabaseService db = neo4j.getGraphDatabaseService();

        try (Transaction tx = db.beginTx()) {
            // 创建节点
            for (int i = 0; i < 10; i++) {
                Node node = db.createNode(Label.label("Loc"));
                node.setProperty("name", "A");
                if (i == 5) {
                    node.setProperty("_entity_name", "印度复仇者联盟"); // 美国复仇者联盟
                } else {
                    node.setProperty("_entity_name", "复仇者联盟");
                }
//                node.setProperty("description", "复联终章快上映了好激动，据说知识图谱与人工智能技术应用到了那部电影！吖啶基氨基甲烷磺酰甲氧基苯胺是一种药嘛？");
            }

            // 给节点建立中文全文索引
            db.execute("CALL olab.index.addChineseFulltextIndex('Loc', ['description'],'Loc') YIELD message RETURN message");
            db.execute("MATCH (n) WHERE n.name='A' WITH n CALL olab.index.addNodeChineseFulltextIndex(n, ['description']) RETURN *");

            tx.success();
        }
        try (Transaction tx = db.beginTx()) {
            // 查询节点
//            Result res2 = db.execute("CALL olab.index.chineseFulltextIndexSearch('Loc', 'description:复联*', 3) YIELD node,weight RETURN node,weight");
//            Result res2 = db.execute("CALL olab.index.chineseFulltextIndexSearch('Loc', 'description:复联*',-1) YIELD node,weight RETURN node,weight");
            // +(description:复联*) OR +(_entity_name:美国)
            // +(description:复联) AND -(_entity_name:美国)
            Result res2 = db.execute("CALL olab.index.chineseFulltextIndexSearch('Loc', '+(_entity_name:印度*)', 3) YIELD node,weight RETURN node,weight");

            while (res2.hasNext()) {
                Map<String, Object> mapO = res2.next();
                Node nodeSearch = (Node) mapO.get("node");
                double hitScore = (double) mapO.get("weight");
                System.out.println("ID:" + nodeSearch.getId() + " Score:" + hitScore);
                Map<String, Object> mapObj = nodeSearch.getAllProperties();
                for (Map.Entry entry : mapObj.entrySet()) {
                    System.out.println(entry.getKey() + ":" + entry.getValue());
                }
            }
            tx.success();
        }
    }

    @Test
    public void chineseFulltextIndexSearch() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();

        // 查询节点
        try (Transaction tx = db.beginTx()) {
            Result res = db.execute("CALL olab.index.chineseFulltextIndexSearch('IKAnalyzer', '复联', 100) YIELD node RETURN node");
            while (res.hasNext()) {
                Node message = (Node) res.next().get("message");
                System.out.println(message.getId());
                System.out.println(message.getProperty("name"));
            }
        }
    }

    @Test
    public void autoIndex() {
        PropertyConfigurator.configureAndWatch("dic/log4j.properties");
        // given
        // create 90k nodes - this force 2 batches during indexing
        execute("UNWIND range(80000,90000) as x CREATE (n:Movie{name:'person'+x}) SET n.description='description'+x");
        execute("UNWIND range(80000,90000) as x CREATE (n:Person{name:'person'+x}) SET n.description='description'+x");

        // 操作属性忽略标签
//        execute("CALL olab.index.addChineseFulltextAutoIndex('people',['name','description'],'Movie',{autoUpdate:'true'})");

        // then
        ResourceIterator<Node> iterator = search("people", "name:person89999");
        try (Transaction tx = db.beginTx()) {
            while (iterator.hasNext()) {
                Node node = iterator.next();

                Iterable<Label> labelIterable = node.getLabels();
                StringBuilder builder = new StringBuilder();
                labelIterable.forEach(v -> {
                    builder.append(v + ":");
                });
                System.out.print(builder.toString());

                Map<String, Object> mapObj = node.getAllProperties();
                for (Map.Entry entry : mapObj.entrySet()) {
                    System.out.print(entry.getKey() + ":" + entry.getValue() + " ");
                }
                System.out.println();
            }
            tx.success();
        }

//        // SECOND TEST
//        execute("UNWIND range(10000,20000) as x CREATE (n:Movie{name:'person'+x}) SET n.description='description'+x");
//        // then
////        ResourceIterator<Node> iterator2 = search("people", "name:person19999");
//        ResourceIterator<Node> iterator2 = nodeSearch("people", "name:person19999");
//
//        try (Transaction tx = db.beginTx()) {
//            while (iterator2.hasNext()) {
//                Node node = iterator.next();
//
//                Iterable<Label> labelIterable = node.getLabels();
//                StringBuilder builder = new StringBuilder();
//                labelIterable.forEach(v -> {
//                    builder.append(v + ":");
//                });
//                System.out.print(builder.toString());
//
//                Map<String, Object> mapObj = node.getAllProperties();
//                for (Map.Entry entry : mapObj.entrySet()) {
//                    System.out.print(entry.getKey() + ":" + entry.getValue() + " ");
//                }
//                System.out.println();
//            }
//            tx.success();
//        }
    }

    private void execute(String query) {
        execute(query, Collections.EMPTY_MAP);
    }

    private void execute(String query, Map<String, Object> params) {
        db.execute(query, params).close();
    }

    @SafeVarargs
    private static <T extends PropertyContainer> void assertSingle(Iterator<T> iter, Matcher<? super T>... matchers) {
        try {
            assertTrue("should contain at least one value", iter.hasNext());
            assertThat(iter.next(), allOf(matchers));
            assertFalse("should contain at most one value", iter.hasNext());
        } finally {
            if (iter instanceof ResourceIterator<?>) {
                ((ResourceIterator<?>) iter).close();
            }
        }
    }

    private ResourceIterator<Node> search(String index, String value) {
//        olab.index.chineseFulltextIndexSearch(String indexName, String query, long limit) YIELD node,weight RETURN node,weight
        return db.execute("CALL olab.index.chineseFulltextIndexSearch({index}, {value},{limit}) YIELD node,weight SET node.weight=weight RETURN node ORDER BY node.weight DESC",
                map("index", index, "value", value, "limit", 100)).columnAs("node");
    }

    private ResourceIterator<Node> nodeSearch(String index, String value) {
        return db.execute("MATCH (node) WHERE node.name CONTAINS '" + value.split(":")[1] + "' WITH node,id(node) AS weight SET node.weight=weight RETURN node ORDER BY node.weight DESC",
                map("index", index, "value", value, "limit", 100)).columnAs("node");
    }

    private static Matcher<? super PropertyContainer> hasProperty(String key, Object value) {
        return new TypeSafeDiagnosingMatcher<PropertyContainer>() {
            @Override
            protected boolean matchesSafely(PropertyContainer item, Description mismatchDescription) {
                Object property;
                try (Transaction tx = item.getGraphDatabase().beginTx()) {
                    property = item.getProperty(key, null);
                    tx.success();
                }
                if (property == null) {
                    mismatchDescription.appendText("property ").appendValue(key).appendText(" not present");
                    return false;
                }
                if (value instanceof Matcher<?>) {
                    Matcher<?> matcher = (Matcher<?>) value;
                    if (!matcher.matches(property)) {
                        matcher.describeMismatch(property, mismatchDescription);
                        return false;
                    }
                    return true;
                }
                if (!property.equals(value)) {
                    mismatchDescription.appendText("property ").appendValue(key).appendText("has value").appendValue(property);
                    return false;
                }
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("entity with property ").appendValue(key).appendText("=").appendValue(value);
            }
        };
    }
}
