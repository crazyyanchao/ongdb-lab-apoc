package data.lab.ongdb.clustering;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import data.lab.ongdb.result.PathResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.procedure.*;

import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.clustering
 * @Description: TODO(聚类过程)
 * @date 2020/5/29 13:38
 */
public class Cluster {
    /**
     * 运行环境/上下文
     */
    @Context
    public GraphDatabaseService db;

    /**
     * @param masterNodeLabel:对此类标签的节点进行聚类计算
     * @param relWeightMap:权重分值分配map【权重为-1表示此类节点关系之间不计算相似度直接分类】
     * @param threshold:最小相似权重设置(关系权重加总之后的得分不能小于这个值)
     * @param slavesMarkField:对所有的从节点设置主节点的ID作为分簇标识
     * @return
     * @Description: TODO(对指定关系模式的节点进行聚类操作 - 并对所有从节点设置主节点的唯一ID - 默认支持两层关系碰撞做聚类)
     */
    @Procedure(name = "olab.cluster.collision", mode = Mode.WRITE)
    @Description("CALL olab.cluster.collision({masterNodeLabel},{relName1:weightiness,relName2:weightiness...},{threshold},{slavesMarkField},{depth}) YIELD count")
    public Stream<PathResult> cluster(@Name("masterNodeLabel") String masterNodeLabel,
                                                         @Name("relWeightMap") Map<String, Integer> relWeightMap,
                                                         @Name("threshold") Number threshold,
                                                         @Name("slavesMarkField") String slavesMarkField) {
        String cypher = "MATCH (n:" + masterNodeLabel + "),(m:" + masterNodeLabel + ") " +
                "MATCH p=(n)-[*.." + 2 + "]-(m) ";
        return Stream.of(new PathResult(0));
    }

}

