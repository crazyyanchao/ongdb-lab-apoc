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
        String createNode = "CREATE (n {simhash:'1010111110110100111001000100110010000110100110101110101110000110'}) SET n:组织机构:中文名称 " +
                "CREATE (m {simhash:'1010111110110100111001000100110010000110100110101110101110000110'}) SET m:组织机构:中文名称";
        db.execute(createNode);

        String buildSimHashRel = "MATCH (n:组织机构:中文名称),(m:组织机构:中文名称) \n" +
                "WHERE n<>m AND NOT ((n)-[:相似简介]-(m))\n" +
                "CALL olab.simhash.build.rel(n,m,'simhash','simhash','相似简介',3,false) YIELD pathJ RETURN pathJ";
        Result resultPath = db.execute(buildSimHashRel);
        while (resultPath.hasNext()) {
            Map<String, Object> map = resultPath.next();
            Object object = map.get("pathJ");
            System.out.println(object);
        }
        System.out.println(createNode);
        System.out.println(buildSimHashRel);
    }

    @Test
    public void editSimilarityPathBuild() {

        /**
         * 主要计算英文短文本相似性
         * cn:0.9
         * en:0.8
         **/

        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        String createNode = "CREATE (n {editDis:'Google M Inc.'}) SET n:组织机构:中文名称 " +
                "CREATE (m {editDis:'Google T Inc.'}) SET m:组织机构:中文名称";
        db.execute(createNode);

        String buildRel = "MATCH (n:组织机构:中文名称),(m:组织机构:中文名称) \n" +
                "WHERE n<>m AND NOT ((n)-[:相似名称]-(m))\n" +
                "CALL olab.editDistance.build.rel(n,m,'editDis','editDis','相似名称',0.9,true) YIELD pathJ RETURN pathJ";
        Result resultPath = db.execute(buildRel);
        while (resultPath.hasNext()) {
            Map<String, Object> map = resultPath.next();
            Object object = map.get("pathJ");
            System.out.println(object);
        }
        System.out.println(createNode);
        System.out.println(buildRel);
    }

    @Test
    public void editSimilarityPathBuildCross_1() {

        /**
         * 主要计算英文短文本相似性
         * cn:0.9
         * en:0.8
         **/

        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        String createNode = "CREATE (n {editDis:'Google M Inc.'}) SET n:组织机构:中文名称 " +
                "CREATE (m {editDis:'Google T Inc.'}) SET m:组织机构:中文名称";
        db.execute(createNode);

        String buildRel = "MATCH (n:组织机构:中文名称),(m:组织机构:中文名称) \n" +
                "WHERE n<>m AND NOT ((n)-[:相似名称]-(m))\n" +
                "CALL olab.editDistance.build.rel.cross(n,m,'关联别名','name','editDis','editDis','相似名称',0.9,true) YIELD pathJ RETURN pathJ";
        Result resultPath = db.execute(buildRel);
        while (resultPath.hasNext()) {
            Map<String, Object> map = resultPath.next();
            Object object = map.get("pathJ");
            System.out.println(object);
        }
        System.out.println(createNode);
        System.out.println(buildRel);
    }

    @Test
    public void editSimilarityPathBuildCross_2() {

        /**
         * 主要计算英文短文本相似性
         * cn:0.9
         * en:0.8
         **/

        GraphDatabaseService db = neo4j.getGraphDatabaseService();
//        String createNode = "CREATE (n {editDis:'Google M Inc.'}) SET n:组织机构:中文名称 " +
//                "CREATE (m {editDis:'Google T Inc.'}) SET m:组织机构:中文名称";
//        db.execute(createNode);
//
//        String creteCrossRel = "MATCH (n),(m) WHERE n.name='Google M Inc.' AND m.name='Google T Inc.' " +
//                "CREATE (n1 {name:'谷歌'}) SET n1:组织机构:中文简称 " +
//                "CREATE (n2 {name:'Google'}) SET n2:组织机构:英文简称 " +
//                "CREATE (m1 {name:'谷歌M'}) SET m1:组织机构:中文简称 " +
//                "CREATE (m2 {name:'谷歌M'}) SET m2:组织机构:英文简称 " +
//                "CREATE (n)-[:关联别名]->(n1) " +
//                "CREATE (n)-[:关联别名]->(n2) " +
//                "CREATE (m)-[:关联别名]->(m1) " +
//                "CREATE (m)-[:关联别名]->(m2) ";

        String creteCrossRel = "CREATE (n {editDis:'Google M Inc.'}) SET n:组织机构:中文名称 " +
                "CREATE (m {editDis:'Google T Inc.'}) SET m:组织机构:中文名称 " +
                "CREATE (n1 {name:'谷歌'}) SET n1:组织机构:中文简称 " +
                "CREATE (n2 {name:'Google'}) SET n2:组织机构:英文简称 " +
                "CREATE (m1 {name:'谷歌M'}) SET m1:组织机构:中文简称 " +
                "CREATE (m2 {name:'谷歌M'}) SET m2:组织机构:英文简称 " +
                "CREATE (n)-[:关联别名]->(n1) " +
                "CREATE (n)-[:关联别名]->(n2) " +
                "CREATE (m)-[:关联别名]->(m1) " +
                "CREATE (m)-[:关联别名]->(m2) ";
        db.execute(creteCrossRel);

        String buildRel = "MATCH (n:组织机构:中文名称),(m:组织机构:中文名称) \n" +
                "WHERE n<>m AND NOT ((n)-[:相似名称]-(m))\n" +
                "CALL olab.editDistance.build.rel.cross(n,m,'关联别名','name','editDis','editDis','相似名称',0.9,true) YIELD pathJ RETURN pathJ";
        Result resultPath = db.execute(buildRel);
        while (resultPath.hasNext()) {
            Map<String, Object> map = resultPath.next();
            Object object = map.get("pathJ");
            System.out.println(object);
        }
        System.out.println(creteCrossRel);
        System.out.println(buildRel);
    }

    @Test
    public void editSimilarityPathBuildCross_3() {
        /**
         * 主要计算英文短文本相似性
         * cn:0.9
         * en:0.8
         **/
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        String creteCrossRel = "CREATE (n {editDis:'Google M Inc.'}) SET n:组织机构:中文名称 " +
                "CREATE (m {editDis:'Google T Inc.'}) SET m:组织机构:中文名称 " +
                "CREATE (n1 {name:'谷歌'}) SET n1:组织机构:中文简称 " +
                "CREATE (n2 {name:'Google'}) SET n2:组织机构:英文简称 " +
                "CREATE (m1 {name:'谷歌M'}) SET m1:组织机构:中文简称 " +
                "CREATE (m2 {name:'谷歌'}) SET m2:组织机构:英文简称 " +
                "CREATE (n)-[:关联别名]->(n1) " +
                "CREATE (n)-[:关联别名]->(n2) " +
                "CREATE (m)-[:关联别名]->(m1) " +
                "CREATE (m)-[:关联别名]->(m2) ";
        db.execute(creteCrossRel);

        String buildRel = "MATCH (n:组织机构:中文名称),(m:组织机构:中文名称) \n" +
                "WHERE n<>m AND NOT ((n)-[:相似名称]-(m))\n" +
                "CALL olab.editDistance.build.rel.cross.encn(n,m,'关联别名','name','editDis','editDis','相似名称',0.9,0.8,true) YIELD pathJ RETURN pathJ";
        Result resultPath = db.execute(buildRel);
        while (resultPath.hasNext()) {
            Map<String, Object> map = resultPath.next();
            Object object = map.get("pathJ");
            System.out.println(object);
        }
        System.out.println(creteCrossRel);
        System.out.println(buildRel);
    }
}


