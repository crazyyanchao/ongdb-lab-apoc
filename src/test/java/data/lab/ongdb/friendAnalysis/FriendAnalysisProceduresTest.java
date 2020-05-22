package data.lab.ongdb.friendAnalysis;

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
/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.friendAnalysis.FriendAnalysisProceduresTest
 * @Description: TODO(Public friend analysis)
 * @date 2020/5/22 10:46
 */
public class FriendAnalysisProceduresTest {

    // 测试过程
    @Rule
    public Neo4jRule neo4j = new Neo4jRule().withProcedure(FriendAnalysis.class);

    // 测试函数
    @Rule
    public Neo4jRule neo4jFunc = new Neo4jRule().withFunction(FriendAnalysis.class);

    //    CALL apoc.merge.node(['Person'],{ssid:'123'}, {name:'John'}) YIELD node RETURN node
    @Test
    public void publicFriendAnalysis() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        try (Transaction tx = db.beginTx()) {

//            id(source)	id(target)  result
////            60667	60669           60667:3条好友关系
////            60667	80988           60652:2条好友关系
////            60652	60667           60669:2条好友关系
////            60652	60669           80988:1条好友关系

            Node nodesOne = db.createNode();
            nodesOne.setProperty("name", 60667);

            Node nodesTwo = db.createNode();
            nodesTwo.setProperty("name", 60652);

            Node nodesThree = db.createNode();
            nodesThree.setProperty("name", 60669);

            Node nodesFour = db.createNode();
            nodesFour.setProperty("name", 80988);

            // sourceList

            List<Node> sourceList = new ArrayList<>();
            sourceList.add(nodesOne);
            sourceList.add(nodesOne);
            sourceList.add(nodesTwo);
            sourceList.add(nodesTwo);

            List<Node> targetList = new ArrayList<>();
            targetList.add(nodesThree);
            targetList.add(nodesFour);
            targetList.add(nodesOne);
            targetList.add(nodesThree);

            Map<String, Object> map = new HashMap<>();
            map.put("sourceList", sourceList);
            map.put("targetList", targetList);

            Result res = db.execute("CALL olab.publicFriendAnalysis({sourceList},{targetList}) YIELD node RETURN node", map);

            while (res.hasNext()) {
                Node node = (Node) res.next().get("node");
                System.out.println("Node id:" + node.getId());
                System.out.println("Node label:" + node.getLabels());
                System.out.println("Node properties:" + node.getProperty("targetGroupFriendsRelaCount"));
            }

        }

    }

    @Test
    public void publicFriendAnalysisMap() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        try (Transaction tx = db.beginTx()) {

//            id(source)	id(target)  result
////            60667	60669           60667:3条好友关系
////            60667	80988           60652:2条好友关系
////            60652	60667           60669:2条好友关系
////            60652	60669           80988:1条好友关系

            // sourceList

            List<Long> sourceList = new ArrayList<>();
            sourceList.add(60667l);
            sourceList.add(60667l);
            sourceList.add(60652l);
            sourceList.add(60652l);

            List<Long> targetList = new ArrayList<>();
            targetList.add(60669l);
            targetList.add(80988l);
            targetList.add(60667l);
            targetList.add(60669l);

            Map<String, Object> map = new HashMap<>();
            map.put("sourceList", sourceList);
            map.put("targetList", targetList);

            Result res = db.execute("CALL olab.publicFriendAnalysisMap({sourceList},{targetList}) YIELD list RETURN list", map);

            while (res.hasNext()) {
                List<Map<String, Object>> listMap = (List<Map<String, Object>>) res.next().get("list");
                for (int i = 0; i < listMap.size(); i++) {
                    Map<String, Object> stringObjectMap = listMap.get(i);
                    stringObjectMap.forEach((k, v) -> {
                        System.out.println(k + "," + v);
                    });
                }
//                System.out.println("Node id:" + node.getId());
//                System.out.println("Node label:" + node.getLabels());
//                System.out.println("Node properties:" + node.getProperty("targetGroupFriendsRelaCount"));
            }

        }

    }

    @Test
    public void targetNodesRelasFilter() {
        GraphDatabaseService db = neo4jFunc.getGraphDatabaseService();
        try (Transaction tx = db.beginTx()) {
//            // 创建节点
//            Node node = db.createNode(Label.label("Loc"));
//            node.setProperty("name", "A");
//            node.setProperty("description", "复联终章快上映了好激动，据说知识图谱与人工智能技术应用到了那部电影！吖啶基氨基甲烷磺酰甲氧基苯胺是一种药嘛？");
//            Node node1 = db.createNode(Label.label("Loc"));
//            node1.setProperty("name", "B");
//            node1.setProperty("description", "复联终章快上映了好激动，据说知识图谱与人工智能技术应用到了那部电影！吖啶基氨基甲烷磺酰甲氧基苯胺是一种药嘛？");
//            Node node2 = db.createNode(Label.label("Loc"));
//            node2.setProperty("name", "C");
//            node2.setProperty("description", "复联终章快上映了好激动，据说知识图谱与人工智能技术应用到了那部电影！");
//
//            // 创建关系
//            db.execute("MATCH (n),(m) WHERE n.name='A' AND m.name='B' MERGE p=(n)-[r:NEXT]->(m) RETURN p");
//            db.execute("MATCH (m),(f) WHERE m.name='B' AND f.name='c' MERGE p=(m)-[r:NEXT]->(f) RETURN p");

            Result res = db.execute("MATCH p=(n)-[*2]-(m) WHERE olab.targetNodesRelasFilter(relationships(p),['NEXT','LAST'],m,['Linkin','Loc','City'])=true RETURN m");
            while (res.hasNext()) {
                Node nodeRe = (Node) res.next().get("m");
                System.out.println("Node id:" + nodeRe.getId());
                System.out.println("Node label:" + nodeRe.getLabels());
                System.out.println("Node properties:" + nodeRe.getProperty("targetGroupFriendsRelaCount"));
            }

        }
    }

    private GraphDatabaseService db;

    @Before
    public void setUp() throws Exception {
//        db = new TestGraphDatabaseFactory().newImpermanentDatabase();
//        TestUtil.registerProcedure(db, FriendAnalysis.class);
    }

    @After
    public void tearDown() {
        db.shutdown();
        db = null;
    }

    @Test
    public void testMethod() {
        FriendAnalysis friendAnalysis = new FriendAnalysis();
        friendAnalysis.isPathNode(null, null);
    }

    @Test
    public void targetNodesRelasFilter2() {

        // given
        // create 90k nodes - this force 2 batches during indexing
//        execute("CREATE (:Movie{name:'person1'})-[r:NEXT]->(n:Movie{name:'person2'})-[r2:NEXT]->(:Movie{name:'person3'})");
//
//        // 操作属性忽略标签
//        execute("MATCH p=(n)-[*2]-(m) WHERE olab.targetNodesRelasFilter(relationships(p),['NEXT','LAST'],m,['Linkin','Loc','City'])=true RETURN p");

        //        execute("MATCH p=(n)-[*2]-(m) WHERE olab.targetNodesRelasFilter(relationships(p),null,m,null)=true RETURN p");

        try (Transaction tx = db.beginTx()) {
            FriendAnalysis analysis = new FriendAnalysis();
            Node node = db.createNode(Label.label("Loc"));
            node.setProperty("name", "A");
            List<String> cN = new ArrayList<>();
            cN.add("Loc");
            cN.add("City");
            analysis.isPathNode(node, cN);

            List<Relationship> relationships = new ArrayList<>();
            relationships.add(null);

            List<String> conditionRelas = new ArrayList<>();
            conditionRelas.add("rela");
            analysis.isPathRelas(relationships, conditionRelas);
        }
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



