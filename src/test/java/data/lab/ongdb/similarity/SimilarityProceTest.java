package data.lab.ongdb.similarity;

import com.sun.org.apache.bcel.internal.generic.RETURN;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
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
                "CREATE (m {simhash:'1010111110110100111000000100110010000110100110101110101110000110'}) SET m:组织机构:中文名称";
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
    public void simHashSimilarityCross() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        String createNode1 = "CREATE (n:组织机构:中文名称 {name:'Alibaba天猫'}) SET n.brief_intro_cn='1010111110110100111001000100110010000110100110101110101110000110',n.brief_intro_en='1010111110110100111000000100110010000110100110101110101110000110'\n" +
                "CREATE (m:组织机构:中文名称 {name:'Alibaba阿猫'}) SET m.brief_intro_cn='11110000100111001000100110010000110100110101110101110000110',m.brief_intro_en='111110110100111000000100110010000110100110101110101110000110',m.business_intro_cn='1010111110110100111001000100110010000110100110101110101110000110'";
        db.execute(createNode1);

        String buildSimHashRel = "MATCH (n:组织机构:中文名称),(m:组织机构:中文名称) " +
                "WHERE n<>m AND NOT ((n)-[:相似简介]-(m)) " +
                "CALL olab.simhash.build.rel.cross(n,m,['brief_intro_cn','brief_intro_en'],['brief_intro_cn','brief_intro_en','business_intro_cn'],'相似简介',3,true) YIELD pathJ RETURN pathJ";
        Result resultPath = db.execute(buildSimHashRel);
        while (resultPath.hasNext()) {
            Map<String, Object> map = resultPath.next();
            Object object = map.get("pathJ");
            System.out.println(object);
        }
        System.out.println(createNode1);
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

    @Test
    public void similarityCollision() {
        /**
         * 生成测试数据集
         * **/
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        String cypher1 = "MERGE (n0:组织机构 {name:'嘉实基金'}) SET n0:中文名称\n" +
                "MERGE (n1:组织机构 {name:'嘉实JS'}) SET n1:中文简称\n" +
                "MERGE (n2:组织机构 {name:'JIA SHI JI JIN'}) SET n2:中文拼音简称\n" +
                "MERGE (n3:组织机构 {name:'HARVEST FUND'}) SET n3:英文名称\n" +
                "MERGE (n4:组织机构 {name:'HARVEST Inc.'}) SET n4:英文简称\n" +
                "MERGE (n5:成立日期 {name:'20180101'}) \n" +
                "MERGE (n6:人物 {name:'赵总'}) SET n6:法人代表\n" +
                "MERGE (n7:人物 {name:'李总'}) SET n7:总经理\n" +
                "MERGE (n8:电子邮箱 {name:'jsfund@jsfund.cn'})\n" +
                "MERGE (n9:城市 {name:'北京'})\n" +
                "MERGE (n10:区县 {name:'东城区'})\n" +
                "MERGE (n11:网址 {name:'www.jsfund.cn'})\n" +
                "MERGE (n12:联系电话 {name:'010-65215000'})\n" +
                "MERGE (n13:邮编 {name:'10000'}) SET n13:注册地址邮编\n" +
                "MERGE (n14:邮编 {name:'10001'}) SET n14:联系地址邮编\n" +
                "MERGE (n15:传真 {name:'5000'})\n" +
                "MERGE (n16:交易代码 {name:'10901'})\n" +
                "MERGE (n17:所属行业 {name:'金融业'})\n" +
                "MERGE (n18:注册资本 {name:'10000000'})\n" +
                "MERGE (n0)-[:关联别名]->(n1)\n" +
                "MERGE (n0)-[:关联别名]->(n2)\n" +
                "MERGE (n0)-[:关联别名]->(n3)\n" +
                "MERGE (n0)-[:关联别名]->(n4)\n" +
                "MERGE (n0)-[:关联别名]->(n4)\n" +
                "MERGE (n0)-[:关联日期]->(n5)\n" +
                "MERGE (n0)-[:关联人]->(n6)\n" +
                "MERGE (n0)-[:关联人]->(n7)\n" +
                "MERGE (n0)-[:关联邮箱]->(n8)\n" +
                "MERGE (n0)-[:关联城市]->(n9)\n" +
                "MERGE (n0)-[:关联城市]->(n9)\n" +
                "MERGE (n0)-[:关联区县]->(n10)\n" +
                "MERGE (n0)-[:关联网址]->(n11)\n" +
                "MERGE (n0)-[:关联电话]->(n12)\n" +
                "MERGE (n0)-[:关联邮编]->(n13)\n" +
                "MERGE (n0)-[:关联邮编]->(n14)\n" +
                "MERGE (n0)-[:关联传真]->(n15)\n" +
                "MERGE (n0)-[:关联交易代码]->(n16)\n" +
                "MERGE (n0)-[:关联行业]->(n17)\n" +
                "MERGE (n0)-[:关联资本]->(n18)";
        String cypher2 = "MERGE (n0:组织机构 {name:'嘉实'}) SET n0:中文名称\n" +
                "MERGE (n1:组织机构 {name:'嘉fund'}) SET n1:中文简称\n" +
                "MERGE (n2:组织机构 {name:'JIA SHI'}) SET n2:中文拼音简称\n" +
                "MERGE (n3:组织机构 {name:'HARVEST FUND'}) SET n3:英文名称\n" +
                "MERGE (n4:组织机构 {name:'HARVEST Inc.'}) SET n4:英文简称\n" +
                "MERGE (n5:成立日期 {name:'20180102'}) \n" +
                "MERGE (n6:人物 {name:'学军总'}) SET n6:法人代表\n" +
                "MERGE (n7:人物 {name:'丹总'}) SET n7:总经理\n" +
                "MERGE (n8:电子邮箱 {name:'jsfund@jsfund.cn'})\n" +
                "MERGE (n9:城市 {name:'北京'})\n" +
                "MERGE (n10:区县 {name:'东城区'})\n" +
                "MERGE (n11:网址 {name:'www.jsfund.cn'})\n" +
                "MERGE (n12:联系电话 {name:'010-65215000'})\n" +
                "MERGE (n13:邮编 {name:'10001'}) SET n13:注册地址邮编\n" +
                "MERGE (n14:邮编 {name:'10001'}) SET n14:联系地址邮编\n" +
                "MERGE (n15:传真 {name:'5000'})\n" +
                "MERGE (n16:交易代码 {name:'10901'})\n" +
                "MERGE (n17:所属行业 {name:'基金业'})\n" +
                "MERGE (n18:注册资本 {name:'10002000'})\n" +
                "MERGE (n0)-[:关联别名]->(n1)\n" +
                "MERGE (n0)-[:关联别名]->(n2)\n" +
                "MERGE (n0)-[:关联别名]->(n3)\n" +
                "MERGE (n0)-[:关联别名]->(n4)\n" +
                "MERGE (n0)-[:关联别名]->(n4)\n" +
                "MERGE (n0)-[:关联日期]->(n5)\n" +
                "MERGE (n0)-[:关联人]->(n6)\n" +
                "MERGE (n0)-[:关联人]->(n7)\n" +
                "MERGE (n0)-[:关联邮箱]->(n8)\n" +
                "MERGE (n0)-[:关联城市]->(n9)\n" +
                "MERGE (n0)-[:关联城市]->(n9)\n" +
                "MERGE (n0)-[:关联区县]->(n10)\n" +
                "MERGE (n0)-[:关联网址]->(n11)\n" +
                "MERGE (n0)-[:关联电话]->(n12)\n" +
                "MERGE (n0)-[:关联邮编]->(n13)\n" +
                "MERGE (n0)-[:关联邮编]->(n14)\n" +
                "MERGE (n0)-[:关联传真]->(n15)\n" +
                "MERGE (n0)-[:关联交易代码]->(n16)\n" +
                "MERGE (n0)-[:关联行业]->(n17)\n" +
                "MERGE (n0)-[:关联资本]->(n18)";
        String cypher3 = "MERGE (n0:组织机构 {name:'嘉实远见'}) SET n0:中文名称\n" +
                "MERGE (n1:组织机构 {name:'远见'}) SET n1:中文简称\n" +
                "MERGE (n2:组织机构 {name:'JIA SHI YUAN JIAN'}) SET n2:中文拼音简称\n" +
                "MERGE (n3:组织机构 {name:'HARVEST YJ'}) SET n3:英文名称\n" +
                "MERGE (n4:组织机构 {name:'HARVEST YJ Inc.'}) SET n4:英文简称\n" +
                "MERGE (n5:成立日期 {name:'20190102'}) \n" +
                "MERGE (n6:人物 {name:'M总'}) SET n6:法人代表\n" +
                "MERGE (n7:人物 {name:'K总'}) SET n7:总经理\n" +
                "MERGE (n8:电子邮箱 {name:'jsfundyj@jsfund.cn'})\n" +
                "MERGE (n9:城市 {name:'北京'})\n" +
                "MERGE (n10:区县 {name:'东城区'})\n" +
                "MERGE (n11:网址 {name:'www.jsfundyj.cn'})\n" +
                "MERGE (n12:联系电话 {name:'010-65315002'})\n" +
                "MERGE (n13:邮编 {name:'10003'}) SET n13:注册地址邮编\n" +
                "MERGE (n14:邮编 {name:'10002'}) SET n14:联系地址邮编\n" +
                "MERGE (n15:传真 {name:'5001'})\n" +
                "MERGE (n16:交易代码 {name:'10501'})\n" +
                "MERGE (n17:所属行业 {name:'金融科技'})\n" +
                "MERGE (n18:注册资本 {name:'5002000'})\n" +
                "MERGE (n0)-[:关联别名]->(n1)\n" +
                "MERGE (n0)-[:关联别名]->(n2)\n" +
                "MERGE (n0)-[:关联别名]->(n3)\n" +
                "MERGE (n0)-[:关联别名]->(n4)\n" +
                "MERGE (n0)-[:关联别名]->(n4)\n" +
                "MERGE (n0)-[:关联日期]->(n5)\n" +
                "MERGE (n0)-[:关联人]->(n6)\n" +
                "MERGE (n0)-[:关联人]->(n7)\n" +
                "MERGE (n0)-[:关联邮箱]->(n8)\n" +
                "MERGE (n0)-[:关联城市]->(n9)\n" +
                "MERGE (n0)-[:关联城市]->(n9)\n" +
                "MERGE (n0)-[:关联区县]->(n10)\n" +
                "MERGE (n0)-[:关联网址]->(n11)\n" +
                "MERGE (n0)-[:关联电话]->(n12)\n" +
                "MERGE (n0)-[:关联邮编]->(n13)\n" +
                "MERGE (n0)-[:关联邮编]->(n14)\n" +
                "MERGE (n0)-[:关联传真]->(n15)\n" +
                "MERGE (n0)-[:关联交易代码]->(n16)\n" +
                "MERGE (n0)-[:关联行业]->(n17)\n" +
                "MERGE (n0)-[:关联资本]->(n18)";
        db.execute(cypher1);
        db.execute(cypher2);
        db.execute(cypher3);

        /**
         * 根据关系权重阈值计算两个节点的相似度
         * **/
        String cypher = "MATCH (n:`组织机构`:`中文名称`) WITH n SKIP 0 LIMIT 100\n" +
                "MATCH (m:`组织机构`:`中文名称`) WHERE n<>m WITH n,m\n" +
                "MATCH p=(n)-[*..2]-(m) WHERE n<>m \n" +
                "WITH extract(r IN relationships(p) | TYPE(r)) AS relList,n,m\n" +
                "WITH collect(relList) AS collectList,n,m\n" +
                "CALL olab.similarity.collision(n,m,collectList,{关联人:3,关联网址:3,关联城市:1}) YIELD similarity,startNode,endNode \n" +
                "RETURN similarity,startNode,endNode ORDER BY similarity DESC LIMIT 100";
        Result resultPath = db.execute(cypher);
        while (resultPath.hasNext()) {
            Map<String, Object> map = resultPath.next();
            Object object = map.get("similarity");
            Node startNode = (Node) map.get("startNode");
            Node endNode = (Node) map.get("endNode");
            System.out.println(startNode.getId()+" "+endNode.getId()+" "+object);
        }
        System.out.println(cypher);
    }
}


