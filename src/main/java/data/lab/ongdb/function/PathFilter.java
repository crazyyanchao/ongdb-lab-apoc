package data.lab.ongdb.function;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.neo4j.graphdb.*;
import org.neo4j.procedure.*;

import java.util.*;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.function.PathFilter
 * @Description: TODO(路径过滤)
 * @date 2020/5/22 10:22
 */
public class PathFilter {
    /**
     * 运行环境/上下文
     */
    @Context
    public GraphDatabaseService db;

    /**
     * @param pathList:COLLECT(p) AS pathList
     * @return
     * @Description: TODO(分析输入节点PATH按照关系层级分类节点 【 输入一个完整的计算逻辑图 】 【 输出层级执行顺序LIST 】)
     */
    @UserFunction(name = "olab.operator.sort")
    @Description("RETURN olab.operator.sort(pathList) AS operatorSortList")
    public List<List<Long>> operatorSortList(@Name("inputNodeList") List<Long> inputNodeList, @Name("pathList") List<Path> pathList) {
        List<List<Long>> operatorSortList = new ArrayList<>();
        /**
         * key为END-NODE，value为对应到key的START-NODE
         * **/
        Map<Node, List<Node>> map = new HashMap<>();
        for (Path path : pathList) {
            Node startNode = path.startNode();
            Node endNode = path.endNode();
            if (map.containsKey(endNode)) {
                List<Node> startNodeList = map.get(endNode);
                startNodeList.add(startNode);
                map.put(endNode, startNodeList);
            } else {
                List<Node> startNodeList = new ArrayList<>();
                startNodeList.add(startNode);
                map.put(endNode, startNodeList);
            }
        }
        /**
         * 算子执行逻辑图的层数
         * **/
        int maxPathDepth = map.size() + 1;

        /**
         * 放入初始入参节点
         * **/
        operatorSortList.add(inputNodeList);

        for (int i = 0; i < maxPathDepth; i++) {
            /**
             * 找到当前这层节点的下一层节点
             * **/
            List<Long> inputNodeListLast = findLastNodes(operatorSortList.get(i), pathList);
            if (inputNodeListLast.isEmpty()) {
                operatorSortList.add(inputNodeListLast);
            }
        }
        return operatorSortList;
    }

    /**
     * @param longs:当前这层节点的IDS
     * @param pathList:当前计算图的路径
     * @return
     * @Description: TODO
     */
    private List<Long> findLastNodes(List<Long> longs, List<Path> pathList) {
        List<Long> lastNodeList = new ArrayList<>();

        // 当前这层直连的节点
        for (int i = 0; i < pathList.size(); i++) {
            Path path = pathList.get(i);
            long startNodeId = path.startNode().getId();
            if (longs.contains(startNodeId)) {
                lastNodeList.add(path.endNode().getId());
            }
        }
        // 当前这层节点两层关联的节点start节点
        for (int i = 0; i < pathList.size(); i++) {
            Path path = pathList.get(i);
            long startNodeId = path.startNode().getId();
            if (longs.contains(startNodeId)) {
                lastNodeList.add(path.endNode().getId());
            }
        }

        return lastNodeList;
    }

    /**
     * @param node:目标节点列表
     * @param filterLabels:目标节点必须满足的标签（任意满足一个即可）
     * @return 路径中必须要有这些标签filterLabels类型的节点
     * @Description: TODO(通过关系和节点标签过滤路径 - 寻找满足条件的点)
     */
    @UserFunction(name = "olab.filter.pathByNodeLabels")
    @Description("Filter target path by node labels")
    public boolean targetPathFilterByNodeLabels(@Name("node") List<Node> node, @Name("conditionLabels") List<String> filterLabels) {

        List<Boolean> booleanList = new ArrayList<>();
        for (int i = 0; i < node.size(); i++) {
            Node n = node.get(i);
            boolean isContainLabel = isPathNode(n, filterLabels);
            if (isContainLabel) {
                booleanList.add(isContainLabel);
            }
        }

        if (booleanList.size() >= filterLabels.size()) {
            return true;
        }
        return false;
    }

    /**
     * @param
     * @return
     * @Description: TODO(将输入的数据转换为JSON)
     */
    @UserFunction(name = "olab.convert.json")
    @Description("CONVERT JSON")
    public String convertJson(@Name("object") Object object) {
        if (object instanceof Node) {
            return packNode((Node) object).toJSONString();
        } else if (object instanceof Path) {
            return packPath((Path) object).toJSONString();
        } else if (object instanceof Map) {
            return JSONObject.parseObject(JSON.toJSONString(object)).toJSONString();
        }
        return "";
    }

    private static JSONObject packPath(Path path) {
        JSONObject graph = new JSONObject();
        JSONArray relationships = new JSONArray();
        JSONArray nodes = new JSONArray();
        JSONArray objectNodes = packNodeByPath(path);
        objectNodes.forEach(node -> {
            JSONObject nodeObj = (JSONObject) node;
            if (!nodes.contains(nodeObj)) {
                nodes.add(nodeObj);
            }
        });

        JSONArray objectRelas = packRelations(path);
        objectRelas.forEach(relation -> {
            JSONObject relationObj = (JSONObject) relation;
            if (!relationships.contains(relationObj)) {
                relationships.add(relationObj);
            }
        });
        graph.put("relationships", relationships);
        graph.put("nodes", nodes);
        return graph;
    }

    private static JSONArray packRelations(Path path) {
        JSONArray arrayRelations = new JSONArray();
        for (Relationship relationship : path.relationships()) {
            arrayRelations.add(packRelation(relationship));
        }
        return arrayRelations;
    }

    private static JSONObject packRelation(Relationship relationship) {
        JSONObject currentRelation = new JSONObject();
        currentRelation.put("startNode", relationship.getStartNodeId());
        currentRelation.put("id", relationship.getId());
        currentRelation.put("type", relationship.getType().name());
        currentRelation.put("endNode", relationship.getEndNodeId());
        currentRelation.put("properties", JSONObject.parseObject(JSON.toJSONString(relationship.getAllProperties())));
        return currentRelation;
    }

    private static JSONArray packNodeByPath(Path path) {
        JSONArray pathNodes = new JSONArray();
        for (Node node : path.nodes()) {
            pathNodes.add(packNode(node));
        }
        return pathNodes;
    }

    private static JSONObject packNode(Node node) {
        JSONObject currentNode = new JSONObject();
        currentNode.put("id", node.getId());
        currentNode.put("properties", JSONObject.parseObject(JSON.toJSONString(node.getAllProperties())));

        ArrayList labelList = new ArrayList();
        Iterable<Label> iterable = node.getLabels();
        iterable.forEach(label -> labelList.add(label.name()));
        currentNode.put("labels", JSONArray.parseArray(JSON.toJSONString(labelList)));

        return currentNode;
    }

    /**
     * @param node:当前节点
     * @param filterLabels:当前节点标签包含在此标签列表即可
     * @return
     * @Description: TODO(过滤节点)
     */
    protected boolean isPathNode(Node node, List<String> filterLabels) {
        if (filterLabels == null) {
            return true;
        }
        Iterable<Label> labels = node.getLabels();
        for (Iterator<Label> iterator = labels.iterator(); iterator.hasNext(); ) {
            Label label = iterator.next();
            if (filterLabels.contains(label.name())) {
                return true;
            }
        }
        return false;
    }

}

