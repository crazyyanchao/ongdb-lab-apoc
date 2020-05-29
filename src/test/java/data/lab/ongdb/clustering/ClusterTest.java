package data.lab.ongdb.clustering;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.harness.junit.Neo4jRule;

/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.clustering
 * @Description: TODO
 * @date 2020/5/29 14:16
 */
public class ClusterTest {

    @Rule
    public Neo4jRule neo4j = new Neo4jRule().withProcedure(Cluster.class);


    /**
     * @param
     * @return
     * @Description: TODO(生成样例数据的结构图image \ company.png)
     */
    @Before
    public void setUp() throws Exception {
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
    }

    @Test
    public void cluster() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        String cluster = "" +
                "CALL olab.cluster.collision(['组织机构','中文名称'],{关联人:3,关联网址:3,关联城市:1},'PREClusterHeart公司',2,'cluster_id') YIELD clusterNum RETURN clusterNum";
        Result result = db.execute(cluster);
        while (result.hasNext()) {
            int count = (int) result.next().get("count");
            System.out.println(count);
        }
    }
}

