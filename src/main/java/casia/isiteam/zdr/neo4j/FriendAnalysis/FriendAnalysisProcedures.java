package casia.isiteam.zdr.neo4j.FriendAnalysis;
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

import casia.isiteam.zdr.neo4j.result.NodeFriendCountList;
import casia.isiteam.zdr.neo4j.result.NodeResult;
import casia.isiteam.zdr.neo4j.util.NodeHandle;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.logging.Log;
import org.neo4j.procedure.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author YanchaoMa yanchaoma@foxmail.com
 * @PACKAGE_NAME: casia.isiteam.zdr.neo4j.FriendAnalysis
 * @Description: TODO(好友关系分析过程)
 * @date 2019/3/30 17:05
 */
public class FriendAnalysisProcedures {
    /**
     * 运行环境/上下文
     */
    @Context
    public GraphDatabaseService db;

    @Context
    public Log log;

    /**
     * 统计两两好友之间的关系数量并动态更新节点属性
     * 1.Nodes list
     * WITH [60667,60652,60669,60635,80988] AS groupIds
     * MATCH (n) WHERE id(n) IN groupIds
     * WITH collect(n) AS nodes
     * UNWIND nodes AS source
     * UNWIND nodes AS target
     * WITH source,target WHERE id(source)<id(target)
     * MATCH paths=(source)-[:好友]-(target) WITH collect(source) AS sourceNodes,collect(target) AS targetNodes
     * CALL zdr.apoc.publicFriendAnalysis(sourceNodes,targetNodes) YIELD node RETURN node
     *
     * 2.Nodes id list
     * WITH [60667,60652,60669,60635,80988] AS groupIds
     * MATCH (n) WHERE id(n) IN groupIds
     * WITH collect(n) AS nodes
     * UNWIND nodes AS source
     * UNWIND nodes AS target
     * WITH source,target WHERE id(source)<id(target)
     * MATCH paths=(source)-[:好友]-(target) WITH collect(id(source)) AS sourceList,collect(id(target)) AS targetList
     * CALL zdr.apoc.publicFriendAnalysisMap(sourceList,targetList) YIELD list WITH list
     * UNWIND list AS row
     * MATCH (n) WHERE id(n)=row.id SET n.targetGroupFriendsRelaCount=row.count RETURN n
     * **/

    /**
     * @param
     * @return
     * @Description: TODO(两两之间的好友关系分析)
     */
    @Procedure(name = "zdr.apoc.publicFriendAnalysis", mode = Mode.WRITE)
    @Description("Public friend analysis")
    public Stream<NodeResult> publicFriendAnalysis(@Name("sourceList") List<Node> sourceList, @Name("targetList") List<Node> targetList) {

        List<Node> nodes = new ArrayList<>();

        // 合并节点集合
        nodes.addAll(sourceList);
        nodes.addAll(targetList);

        // 统计好友关系数
        HashMap<Long, Integer> countMap = countFriedsRela(nodes);

        // 节点集合排重
        NodeHandle nodeHandle = new NodeHandle();
        nodes = nodeHandle.distinctNodes(nodes);

        // 给节点更新属性并返回
        return returnFriendResultNodes(nodes, countMap);
    }

    /**
     * @param
     * @return
     * @Description: TODO(两两之间的好友关系分析)
     */
    @Procedure(name = "zdr.apoc.publicFriendAnalysisMap", mode = Mode.WRITE)
    @Description("Public friend analysis")
    public Stream<NodeFriendCountList> publicFriendAnalysisMap(@Name("sourceList") List<Long> sourceList, @Name("targetList") List<Long> targetList) {

        List<Long> nodes = new ArrayList<>();

        // 合并节点集合
        nodes.addAll(sourceList);
        nodes.addAll(targetList);

        // 统计好友关系数
        HashMap<Long, Integer> countMap = countFriedsRelaIds(nodes);

        // 给节点更新属性并返回
        return Stream.of(new NodeFriendCountList(returnFriendResultNodesById(countMap)));
    }

    /**
     * @param
     * @return
     * @Description: TODO(将统计结果更新为节点属性并返回节点集合)
     */
    private List<Map<String, Object>> returnFriendResultNodesById(HashMap<Long, Integer> countMap) {
        List<Map<String, Object>> list = new ArrayList<>();
        countMap.forEach((k, v) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", k);
            map.put("count", v);
            list.add(map);
        });
        return list;
    }

    /**
     * @param
     * @return
     * @Description: TODO(将统计结果更新为节点属性并返回节点集合)
     */
    private Stream<NodeResult> returnFriendResultNodes(List<Node> nodes, HashMap<Long, Integer> countMap) {
        List<NodeResult> output = new ArrayList<>();
        try (Transaction tx = db.beginTx()) {
            // 给节点更新属性 targetGroupFriendsRelaCount
            nodes.forEach(node -> {
                node.setProperty("targetGroupFriendsRelaCount", countMap.get(node.getId()));
                output.add(new NodeResult(node));
            });

            log.debug("Public friend analysis");
            tx.success();
        }
        return output.stream();
    }

    /**
     * @param
     * @return
     * @Description: TODO(统计好友关系数)
     */
    private HashMap<Long, Integer> countFriedsRela(List<Node> nodes) {
        HashMap<Long, Integer> countMap = new HashMap<>();
        nodes.forEach(node -> {
            long id = node.getId();
            if (countMap.containsKey(id)) {
                int count = countMap.get(id);
                count++;
                countMap.put(id, count);
            } else {
                countMap.put(id, 1);
            }
        });
        return countMap;
    }

    /**
     * @param
     * @return
     * @Description: TODO(统计好友关系数)
     */
    private HashMap<Long, Integer> countFriedsRelaIds(List<Long> nodes) {
        HashMap<Long, Integer> countMap = new HashMap<>();
        nodes.forEach(nodeId -> {
            long id = nodeId;
            if (countMap.containsKey(id)) {
                int count = countMap.get(id);
                count++;
                countMap.put(id, count);
            } else {
                countMap.put(id, 1);
            }
        });
        return countMap;
    }
}
